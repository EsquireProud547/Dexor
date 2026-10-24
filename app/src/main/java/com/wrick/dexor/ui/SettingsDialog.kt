package com.wrick.dexor.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.LocaleListCompat
import com.wrick.dexor.R
import com.wrick.dexor.model.PrivilegeState
import com.wrick.dexor.ui.theme.DexorDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    privilegeState: PrivilegeState,
    onRequestPermission: () -> Unit,
    onRefreshState: () -> Unit,
    onBack: () -> Unit,
    onProbeRoot: () -> Unit = {}
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current

    Scaffold(
                topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_about),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(DexorDimensions.minTouchTarget)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F1115),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF090A0C)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DexorDimensions.spaceDefault, vertical = DexorDimensions.spaceDefault),
            verticalArrangement = Arrangement.spacedBy(DexorDimensions.spaceLarge)
        ) {
            // 应用品牌展示卡片
            HeroAppCard(context = context)

            // 第 1 节：特权通道状态
            SettingsSection(title = stringResource(R.string.privilege_section)) {
                ShizukuStatusCard(
                    privilegeState = privilegeState,
                    onRequestPermission = onRequestPermission,
                    onRefreshShizuku = onRefreshState,
                    onProbeRoot = onProbeRoot
                )
            }

            // 第 2 节：界面语言
            SettingsSection(title = stringResource(R.string.language_section)) {
                LanguageCard()
            }

            // 第 3 节：隐私与系统安全性
            SettingsSection(title = stringResource(R.string.privacy_section)) {
                PrivacySafetyCard()
            }

            // 第 4 节：开源与致谢
            SettingsSection(title = stringResource(R.string.about_section)) {
                CreditsLegalCard(
                    onOpenProfile = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DeveshTone"))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    },
                    onOpenRepo = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DeveshTone/Dexor"))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    },
                    onOpenReleases = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DeveshTone/Dexor/releases"))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    }
                )
            }

            Spacer(Modifier.height(DexorDimensions.spaceLarge))
        }
    }
}

// 兼容别名
@Composable
fun SettingsDialog(
    privilegeState: PrivilegeState,
    onRequestPermission: () -> Unit,
    onRefreshState: () -> Unit,
    onDismiss: () -> Unit,
    onProbeRoot: () -> Unit = {}
) {
    SettingsScreen(
        privilegeState = privilegeState,
        onRequestPermission = onRequestPermission,
        onRefreshState = onRefreshState,
        onBack = onDismiss,
        onProbeRoot = onProbeRoot
    )
}

@Composable
private fun HeroAppCard(context: android.content.Context) {
    val appIconBitmap = remember(context) {
        try {
            val drawable = context.packageManager.getApplicationIcon(context.packageName)
            drawable.toBitmap(width = 128, height = 128).asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    Surface(
        shape = RoundedCornerShape(DexorDimensions.cornerLarge),
        color = Color(0xFF131720),
        border = BorderStroke(1.dp, Color(0xFF1E2636)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DexorDimensions.spaceDefault),
            verticalArrangement = Arrangement.spacedBy(DexorDimensions.spaceMedium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(DexorDimensions.cornerMedium),
                    color = Color(0xFF090A0C),
                    border = BorderStroke(1.dp, Color(0xFF1E2636)),
                    modifier = Modifier.size(DexorDimensions.appIconSizeDetail)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (appIconBitmap != null) {
                            Image(
                                bitmap = appIconBitmap,
                                contentDescription = stringResource(R.string.dexor_icon),
                                modifier = Modifier.size(44.dp)
                            )
                        } else {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF1976D2).copy(alpha = 0.25f),
                                modifier = Modifier.size(36.dp)
                            ) {}
                        }
                    }
                }

                Spacer(Modifier.width(DexorDimensions.spaceDefault))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Dexor",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(DexorDimensions.cornerSmall),
                            color = Color(0xFF1B5E20).copy(alpha = 0.35f),
                            border = BorderStroke(0.8.dp, Color(0xFF4CAF50).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "v1.0",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.app_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF90A4AE)
                    )
                }
            }

            // 核心特性徽章
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusPill(label = stringResource(R.string.badge_offline), accent = Color(0xFF81C784))
                StatusPill(label = stringResource(R.string.badge_open_source), accent = Color(0xFF90CAF9))
            }
        }
    }
}

@Composable
private fun StatusPill(label: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(DexorDimensions.cornerSmall),
        color = accent.copy(alpha = 0.12f),
        border = BorderStroke(0.8.dp, accent.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = accent,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ShizukuStatusCard(
    privilegeState: PrivilegeState,
    onRequestPermission: () -> Unit,
    onRefreshShizuku: () -> Unit,
    onProbeRoot: () -> Unit = {}
) {
    val context = LocalContext.current
    val (statusColor, statusTitle, statusDesc) = when (privilegeState) {
        PrivilegeState.SHIZUKU_READY -> Triple(
            Color(0xFF4CAF50),
            stringResource(R.string.status_shizuku_ready),
            stringResource(R.string.status_shizuku_ready_desc)
        )
        PrivilegeState.ROOT_READY -> Triple(
            Color(0xFF4CAF50),
            stringResource(R.string.status_root_ready),
            stringResource(R.string.status_root_ready_desc)
        )
        PrivilegeState.ROOT_CHECKING -> Triple(
            Color(0xFF90CAF9),
            stringResource(R.string.status_checking),
            stringResource(R.string.status_checking_desc)
        )
        PrivilegeState.NEED_SHIZUKU_PERMISSION -> Triple(
            Color(0xFFFFA726),
            stringResource(R.string.status_need_permission),
            stringResource(R.string.status_need_permission_desc)
        )
        PrivilegeState.NO_CHANNEL -> Triple(
            Color(0xFFEF5350),
            stringResource(R.string.status_no_channel),
            stringResource(R.string.status_no_channel_desc)
        )
    }

    Surface(
        shape = RoundedCornerShape(DexorDimensions.cornerMedium),
        color = Color(0xFF131720),
        border = BorderStroke(1.dp, Color(0xFF1E2636)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DexorDimensions.spaceDefault),
            verticalArrangement = Arrangement.spacedBy(DexorDimensions.spaceMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(Modifier.width(DexorDimensions.spaceSmall))
                    Text(
                        text = stringResource(R.string.privilege_channel),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(DexorDimensions.cornerSmall),
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(0.8.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = statusTitle,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = statusDesc,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF90A4AE),
                lineHeight = 20.sp
            )

            if (privilegeState == PrivilegeState.NEED_SHIZUKU_PERMISSION) {
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(DexorDimensions.cornerSmall),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = DexorDimensions.minTouchTarget)
                ) {
                    Text(stringResource(R.string.grant_shizuku_permission), fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                Text(
                    text = stringResource(R.string.need_help_guide),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF90CAF9),
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/guide/setup/"))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        }
                        .padding(vertical = 2.dp)
                )
            } else if (privilegeState == PrivilegeState.NO_CHANNEL) {
                Button(
                    onClick = onProbeRoot,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(DexorDimensions.cornerSmall),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = DexorDimensions.minTouchTarget)
                ) {
                    Text(stringResource(R.string.detect_root_channel), fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DexorDimensions.spaceSmall)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/guide/setup/"))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        shape = RoundedCornerShape(DexorDimensions.cornerSmall),
                        border = BorderStroke(1.dp, Color(0xFF1E2636)),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = DexorDimensions.minTouchTarget),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF90CAF9))
                    ) {
                        Text(stringResource(R.string.setup_guide), style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = onRefreshShizuku,
                        shape = RoundedCornerShape(DexorDimensions.cornerSmall),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = DexorDimensions.minTouchTarget)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(R.string.recheck), style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
                }
            } else if (privilegeState == PrivilegeState.ROOT_CHECKING) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFF90CAF9),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(DexorDimensions.spaceSmall))
                    Text(
                        stringResource(R.string.probing_root),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF90A4AE)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacySafetyCard() {
    Surface(
        shape = RoundedCornerShape(DexorDimensions.cornerMedium),
        color = Color(0xFF131720),
        border = BorderStroke(1.dp, Color(0xFF1E2636)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DexorDimensions.spaceDefault),
            verticalArrangement = Arrangement.spacedBy(DexorDimensions.spaceMedium)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF81C784),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.privacy_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Text(
                text = stringResource(R.string.privacy_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF90A4AE),
                lineHeight = 20.sp
            )

            Divider(color = Color(0xFF1A2130), thickness = 0.8.dp)

            Text(
                text = stringResource(R.string.system_safety_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Text(
                text = stringResource(R.string.system_safety_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF90A4AE),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun CreditsLegalCard(
    onOpenProfile: () -> Unit,
    onOpenRepo: () -> Unit,
    onOpenReleases: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(DexorDimensions.cornerMedium),
        color = Color(0xFF131720),
        border = BorderStroke(1.dp, Color(0xFF1E2636)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DexorDimensions.spaceDefault),
            verticalArrangement = Arrangement.spacedBy(DexorDimensions.spaceMedium)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenProfile() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.developer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF90A4AE)
                )
                Text(
                    text = "DeveshTone (@DeveshTone)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF90CAF9)
                )
            }

            Divider(color = Color(0xFF1A2130), thickness = 0.8.dp)

            MinimalInfoRow(label = stringResource(R.string.license), value = "MIT License")
            Divider(color = Color(0xFF1A2130), thickness = 0.8.dp)

            Text(
                text = stringResource(R.string.third_party),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            MinimalInfoRow(label = "Shizuku API", value = "Apache 2.0 (RikkaApps)")
            MinimalInfoRow(label = "Jetpack Compose", value = "Apache 2.0 (Google LLC)")
            MinimalInfoRow(label = "Room SQLite", value = "Apache 2.0 (Google LLC)")
            MinimalInfoRow(label = "Coil", value = "Apache 2.0 (Coil Contributors)")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DexorDimensions.spaceSmall)
            ) {
                OutlinedButton(
                    onClick = onOpenRepo,
                    shape = RoundedCornerShape(DexorDimensions.cornerSmall),
                    border = BorderStroke(1.dp, Color(0xFF1E2636)),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = DexorDimensions.minTouchTarget),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF90CAF9))
                ) {
                    Text(stringResource(R.string.github_repo), style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = onOpenReleases,
                    shape = RoundedCornerShape(DexorDimensions.cornerSmall),
                    border = BorderStroke(1.dp, Color(0xFF1E2636)),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = DexorDimensions.minTouchTarget),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF90CAF9))
                ) {
                    Text(stringResource(R.string.releases), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

/**
 * 界面语言选择:跟随系统 / 简体中文 / English。
 * 基于 AppCompatDelegate 的 per-app locale,选择立即生效并自动持久化;
 * 系统语言为中文时默认显示中文,其余语言回退英文。
 */
@Composable
private fun LanguageCard() {
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val selectedTag = when {
        appLocales.isEmpty -> "system"
        appLocales.get(0)?.language == "zh" -> "zh"
        else -> "en"
    }

    data class LangOption(val tag: String, val labelRes: Int)
    val options = listOf(
        LangOption("system", R.string.language_system),
        LangOption("zh", R.string.language_zh),
        LangOption("en", R.string.language_en)
    )

    Surface(
        shape = RoundedCornerShape(DexorDimensions.cornerMedium),
        color = Color(0xFF131720),
        border = BorderStroke(1.dp, Color(0xFF1E2636)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(DexorDimensions.spaceDefault)) {
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = DexorDimensions.minTouchTarget)
                        .clip(RoundedCornerShape(DexorDimensions.cornerSmall))
                        .clickable {
                            AppCompatDelegate.setApplicationLocales(
                                when (option.tag) {
                                    "zh" -> LocaleListCompat.forLanguageTags("zh-CN")
                                    "en" -> LocaleListCompat.forLanguageTags("en")
                                    else -> LocaleListCompat.getEmptyLocaleList()
                                }
                            )
                        }
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedTag == option.tag,
                        onClick = {
                            AppCompatDelegate.setApplicationLocales(
                                when (option.tag) {
                                    "zh" -> LocaleListCompat.forLanguageTags("zh-CN")
                                    "en" -> LocaleListCompat.forLanguageTags("en")
                                    else -> LocaleListCompat.getEmptyLocaleList()
                                }
                            )
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF90CAF9))
                    )
                    Spacer(Modifier.width(DexorDimensions.spaceSmall))
                    Text(
                        text = stringResource(option.labelRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFECEFF1)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DexorDimensions.spaceSmall)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF90CAF9),
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
        content()
    }
}

@Composable
private fun MinimalInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF90A4AE),
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFECEFF1),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
