package com.wrick.dexor.shell

import com.wrick.dexor.shizuku.ShizukuHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * 特权命令执行后端抽象。
 *
 * 通道选择策略(ShellRouter):Shizuku 在线且已授权时优先走 Shizuku(默认 shell UID,权限更小);
 * 否则自动回退到 Root 直连 —— 每条命令通过一次性 `su -c` 子进程执行,不维持常驻 root shell,
 * 应用主进程全程保持普通 UID。两种通道下 Dexor 均不依赖对方存在。
 */
interface PrivilegedShell {
    suspend fun exec(command: String, timeoutMs: Long = DEFAULT_TIMEOUT_MS): Result<String>

    companion object {
        const val DEFAULT_TIMEOUT_MS = 60_000L
    }
}

/** Shizuku 通道:复用既有 ShizukuHelper(反射 newProcess) */
private object ShizukuShell : PrivilegedShell {
    override suspend fun exec(command: String, timeoutMs: Long): Result<String> =
        ShizukuHelper.exec(command)
}

/** Root 直连通道:每命令一次性 su 子进程,带超时与输出合并,结果进程期缓存 */
object RootShell : PrivilegedShell {

    @Volatile
    private var probed = false

    @Volatile
    private var ready = false

    /** 已探测且就绪时直接返回(不触发 su) */
    fun cachedReady(): Boolean = ready

    /**
     * 探测 Root:执行 `su -c id` 并检查 uid=0。
     * 首次探测可能触发 Magisk/KernelSU 授权弹窗;已就绪时静默返回,
     * force 仅在"当前未就绪"时才重新探测(用于用户显式重试)。
     */
    suspend fun probe(force: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        if (ready) return@withContext true
        if (probed && !force) return@withContext false
        probed = true
        ready = runSu("id", 20_000L).getOrDefault("").contains("uid=0")
        ready
    }

    override suspend fun exec(command: String, timeoutMs: Long): Result<String> =
        withContext(Dispatchers.IO) { runSu(command, timeoutMs) }

    /**
     * 执行一次 `su -c <command>`:
     * - redirectErrorStream 合并输出,后台线程持续排空管道避免缓冲区死锁;
     * - waitFor 带超时,超时即销毁进程,不留挂起的特权子进程。
     */
    private fun runSu(command: String, timeoutMs: Long): Result<String> {
        var process: Process? = null
        return try {
            process = ProcessBuilder("su", "-c", command)
                .redirectErrorStream(true)
                .start()

            val output = StringBuilder()
            val reader = Thread {
                try {
                    process.inputStream.bufferedReader().forEachLine { line ->
                        synchronized(output) { output.append(line).append('\n') }
                    }
                } catch (_: Exception) {
                }
            }.apply { isDaemon = true }
            reader.start()

            val finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
            reader.join(1_000)
            val out = synchronized(output) { output.toString() }.trim()

            when {
                !finished -> {
                    process.destroyForcibly()
                    Result.failure(Exception("su timeout (not authorized or root manager unresponsive)"))
                }
                process.exitValue() == 0 -> Result.success(out)
                else -> Result.failure(Exception("Exit ${process.exitValue()}: $out"))
            }
        } catch (e: Exception) {
            process?.destroyForcibly()
            Result.failure(Exception("Failed to execute su: ${e.message}"))
        }
    }
}

/** 通道选择与命令派发:Shizuku 优先,Root 自动回退 */
object ShellRouter {

    /**
     * 返回当前可用的最优通道。
     * @param forceRootProbe Shizuku 不可用时是否强制重新探测 Root(用户显式发起操作时为 true,
     *   可触发一次 Root 授权弹窗;后台刷新使用 false,命中缓存,不打扰用户)
     */
    suspend fun best(forceRootProbe: Boolean = false): PrivilegedShell? {
        if (ShizukuHelper.isShizukuAvailable() && ShizukuHelper.hasPermission()) return ShizukuShell
        if (RootShell.probe(forceRootProbe)) return RootShell
        return null
    }

    /** 任意通道就绪(仅使用缓存探测结果,不触发新的 su 弹窗) */
    suspend fun anyReady(): Boolean = best(forceRootProbe = false) != null

    suspend fun exec(command: String, timeoutMs: Long = PrivilegedShell.DEFAULT_TIMEOUT_MS): Result<String> =
        best()?.exec(command, timeoutMs)
            ?: Result.failure(IllegalStateException("无可用特权通道(Shizuku 未就绪且 Root 未授权)"))

    suspend fun execOrEmpty(command: String, timeoutMs: Long = PrivilegedShell.DEFAULT_TIMEOUT_MS): String =
        exec(command, timeoutMs).getOrDefault("")
}
