package com.wrick.dexor.model

import androidx.compose.runtime.Immutable
import com.wrick.dexor.R

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

/**
 * 特权通道统一状态:Shizuku 优先,Root 直连自动回退。
 * ROOT_READY 下完全无需安装/启动 Shizuku。
 */
enum class PrivilegeState {
    SHIZUKU_READY,           // Shizuku 在线且已授权
    ROOT_READY,              // Root 直连通道可用(自动回退)
    ROOT_CHECKING,           // 正在探测 Root 通道
    NEED_SHIZUKU_PERMISSION, // Shizuku 在线但未授权,且 Root 不可用
    NO_CHANNEL;              // 无任何特权通道(只读模式)

    fun isReady(): Boolean = this == SHIZUKU_READY || this == ROOT_READY
}

data class CompileMode(
    val id: String,
    val labelRes: Int,
    val descriptionRes: Int,
    val estimatedSecondsPerApp: Int,
    val isReset: Boolean = false
)

val COMPILE_MODES = listOf(
    CompileMode(
        id = "speed-profile",
        labelRes = R.string.mode_speed_profile_label,
        descriptionRes = R.string.mode_speed_profile_desc,
        estimatedSecondsPerApp = 15
    ),
    CompileMode(
        id = "speed",
        labelRes = R.string.mode_speed_label,
        descriptionRes = R.string.mode_speed_desc,
        estimatedSecondsPerApp = 30
    ),
    CompileMode(
        id = "everything",
        labelRes = R.string.mode_everything_label,
        descriptionRes = R.string.mode_everything_desc,
        estimatedSecondsPerApp = 60
    ),
    CompileMode(
        id = "space",
        labelRes = R.string.mode_space_label,
        descriptionRes = R.string.mode_space_desc,
        estimatedSecondsPerApp = 15
    ),
    CompileMode(
        id = "verify",
        labelRes = R.string.mode_verify_label,
        descriptionRes = R.string.mode_verify_desc,
        estimatedSecondsPerApp = 5
    ),
    CompileMode(
        id = "reset",
        labelRes = R.string.mode_reset_label,
        descriptionRes = R.string.mode_reset_desc,
        estimatedSecondsPerApp = 3,
        isReset = true
    )
)

/** 返回 dexopt 触发原因的本地化文案资源 ID,由调用方经 stringResource 解析 */
fun getDexReasonRes(reason: String): Int = when (reason.lowercase()) {
    "cmdline" -> R.string.reason_cmdline
    "install" -> R.string.reason_install
    "bg-dexopt" -> R.string.reason_bg_dexopt
    "first-boot" -> R.string.reason_first_boot
    "boot-after-ota" -> R.string.reason_boot_after_ota
    "inactive" -> R.string.reason_inactive
    "reset" -> R.string.reason_reset
    "unknown" -> R.string.reason_unknown
    else -> 0
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
