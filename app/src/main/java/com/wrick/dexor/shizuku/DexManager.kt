package com.wrick.dexor.shizuku

import com.wrick.dexor.shell.ShellRouter

object DexManager {
    data class DexoptState(val status: String, val reason: String)

    /** 单应用编译命令超时:`everything` 等模式在大体型应用上可能耗时数分钟 */
    private const val COMPILE_TIMEOUT_MS: Long = 30 * 60_000L

    /** 包名白名单校验,防止命令拼接注入(包名本身字符集受限,此处双保险) */
    private fun safe(packageName: String): String {
        if (!packageName.matches(Regex("^[A-Za-z0-9._]+$"))) {
            throw IllegalArgumentException("非法包名: $packageName")
        }
        return packageName
    }

    /**
     * Parse all package dexopt states from bulk command:
     *   `dumpsys package dexopt`
     * Uses streaming lineSequence and zero regexes to prevent GC pauses.
     */
    suspend fun getAllDexoptStates(): Map<String, DexoptState> {
        val output = ShellRouter.execOrEmpty("dumpsys package dexopt")
        if (output.isBlank()) return emptyMap()

        val map = HashMap<String, DexoptState>(400)
        var currentPackage: String? = null

        output.lineSequence().forEach { rawLine ->
            val line = rawLine.trim()
            if (line.startsWith("[") && line.endsWith("]")) {
                val pkgCandidate = line.substring(1, line.length - 1).trim()
                if (pkgCandidate.contains(".")) {
                    currentPackage = pkgCandidate
                }
            } else if (currentPackage != null) {
                val statusIdx = line.indexOf("[status=")
                if (statusIdx != -1) {
                    val statusEnd = line.indexOf(']', statusIdx)
                    if (statusEnd != -1) {
                        val status = line.substring(statusIdx + 8, statusEnd).trim()
                        val reasonIdx = line.indexOf("[reason=")
                        val reason = if (reasonIdx != -1) {
                            val reasonEnd = line.indexOf(']', reasonIdx)
                            if (reasonEnd != -1) line.substring(reasonIdx + 8, reasonEnd).trim() else "unknown"
                        } else "unknown"
                        map[currentPackage!!] = DexoptState(status, reason)
                    }
                }
            }
        }
        return map
    }

    /**
     * Pull Status and Reason for a single package using targeted pipe.
     * Extremely fast and avoids processing megabytes of irrelevant dumpsys text.
     */
    suspend fun getDexoptState(packageName: String): DexoptState? {
        val command = "dumpsys package dexopt | grep -F -A 2 '[${safe(packageName)}]'"
        val output = ShellRouter.execOrEmpty(command)

        if (output.isNotBlank()) {
            val statusIdx = output.indexOf("[status=")
            if (statusIdx != -1) {
                val statusEnd = output.indexOf(']', statusIdx)
                if (statusEnd != -1) {
                    val status = output.substring(statusIdx + 8, statusEnd).trim()
                    val reasonIdx = output.indexOf("[reason=")
                    val reason = if (reasonIdx != -1) {
                        val reasonEnd = output.indexOf(']', reasonIdx)
                        if (reasonEnd != -1) output.substring(reasonIdx + 8, reasonEnd).trim() else "unknown"
                    } else "unknown"
                    return DexoptState(status, reason)
                }
            }
        }

        return null
    }

    /**
     * Execute compilation via `cmd package compile -m <mode> -f <packageName>`.
     * Supported modes: speed-profile, speed, everything, space, verify, reset.
     * For 'reset', runs `cmd package compile --reset <packageName>`.
     */
    suspend fun compilePackage(packageName: String, mode: String, force: Boolean = true): Result<String> {
        val pkg = safe(packageName)
        val command = if (mode.equals("reset", ignoreCase = true)) {
            "cmd package compile --reset $pkg"
        } else {
            val forceFlag = if (force) " -f" else ""
            "cmd package compile -m $mode$forceFlag $pkg"
        }
        return ShellRouter.exec(command, COMPILE_TIMEOUT_MS)
    }
}
