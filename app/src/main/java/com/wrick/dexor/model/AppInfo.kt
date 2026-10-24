package com.wrick.dexor.model

import androidx.compose.runtime.Immutable

enum class InstallSource {
    SYSTEM, GOOGLE_PLAY, USER
}

enum class AppFilter {
    USER, SYSTEM
}

enum class SortMode {
    NAME, DEX_STATUS, SOURCE
}

enum class ShizukuState {
    NOT_RUNNING,
    NO_PERMISSION,
    READY
}

data class CompileMode(
    val id: String,
    val label: String,
    val description: String,
    val estimatedSecondsPerApp: Int,
    val isReset: Boolean = false
)

val COMPILE_MODES = listOf(
    CompileMode(
        id = "speed-profile",
        label = "Speed Profile",
        description = "根据可用 Profile 编译热点代码；没有 Profile 时可能采用 Verify",
        estimatedSecondsPerApp = 15
    ),
    CompileMode(
        id = "speed",
        label = "Speed",
        description = "不依赖 Profile，将所有方法编译为机器码，以执行速度为目标",
        estimatedSecondsPerApp = 30
    ),
    CompileMode(
        id = "everything",
        label = "Everything",
        description = "更广泛地编译代码，解析全部类引用，可能增加耗时和存储占用",
        estimatedSecondsPerApp = 60
    ),
    CompileMode(
        id = "space",
        label = "Space",
        description = "仅编译核心初始化方法，兼顾稳定启动与最小存储占用",
        estimatedSecondsPerApp = 15
    ),
    CompileMode(
        id = "verify",
        label = "Verify",
        description = "仅验证 DEX 结构，清除所有原生代码，释放最大存储空间",
        estimatedSecondsPerApp = 5
    ),
    CompileMode(
        id = "reset",
        label = "清除",
        description = "重置编译状态，清除本地 Profile 和编译产物",
        estimatedSecondsPerApp = 3,
        isReset = true
    )
)

fun getDexReasonDisplay(reason: String): String = when (reason.lowercase()) {
    "cmdline" -> "手动优化"
    "install" -> "应用安装"
    "bg-dexopt" -> "后台维护"
    "first-boot" -> "首次开机"
    "boot-after-ota" -> "系统升级"
    "inactive" -> "闲置回收"
    "reset" -> "已重置"
    "unknown" -> "未知"
    else -> reason
}

@Immutable
data class AppInfo(
    val name: String,
    val packageName: String,
    val source: InstallSource,
    val dexStatus: String,
    val dexReason: String,
    val lastUpdateTime: Long,
    val lastStatusChangeTime: Long? = null,
    val lastCompilationTime: Long? = null,
    val versionName: String,
    val versionCode: Long,
    val targetSdk: Int,
    val minSdk: Int?,
    val codePath: String?,
    val dataDir: String?
)
