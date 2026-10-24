package com.wrick.dexor.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.wrick.dexor.model.ShizukuState
import com.wrick.dexor.ui.theme.DexorDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    shizukuState: ShizukuState,
    onRequestPermission: () -> Unit,
    onRefreshShizuku: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "设置与关于",
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
                            contentDescription = "返回",
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

            // 第 1 节：Shizuku 提权服务
            SettingsSection(title = "SHIZUKU 特权状态") {
                ShizukuStatusCard(
                    shizukuState = shizukuState,
                    onRequestPermission = onRequestPermission,
                    onRefreshShizuku = onRefreshShizuku
                )
            }

            // 第 2 节：隐私与系统安全性
            SettingsSection(title = "隐私与安全性") {
                PrivacySafetyCard()
            }

            // 第 3 节：开源与致谢
            SettingsSection(title = "关于与致谢") {
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
    shizukuState: ShizukuState,
    onRequestPermission: () -> Unit,
    onRefreshShizuku: () -> Unit,
    onDismiss: () -> Unit
) {
    SettingsScreen(
        shizukuState = shizukuState,
        onRequestPermission = onRequestPermission,
        onRefreshShizuku = onRefreshShizuku,
        onBack = onDismiss
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
                                contentDescription = "Dexor 图标",
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
                        text = "统一 DEX2OAT 管理器",
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
                StatusPill(label = "100% 离线隐私", accent = Color(0xFF81C784))
                StatusPill(label = "开源免费", accent = Color(0xFF90CAF9))
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
    shizukuState: ShizukuState,
    onRequestPermission: () -> Unit,
    onRefreshShizuku: () -> Unit
) {
    val context = LocalContext.current
    val (statusColor, statusTitle, statusDesc) = when (shizukuState) {
        ShizukuState.READY -> Triple(
            Color(0xFF4CAF50),
            "已授权并就绪",
            "Dexor 已成功连接至 Shizuku，具有执行原生 ART 编译命令的提权能力。"
        )
        ShizukuState.NO_PERMISSION -> Triple(
            Color(0xFFFFA726),
            "需要授权",
            "Shizuku 服务正在运行，但尚未向 Dexor 授予权限。请点击下方按钮完成授权以开启 DEX 编译。"
        )
        ShizukuState.NOT_RUNNING -> Triple(
            Color(0xFFEF5350),
            "服务未运行",
            "Shizuku 服务未运行。请通过无线调试 (ADB) 或 Root 启动 Shizuku 以执行编译操作。"
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
                        text = "Shizuku 服务",
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

            if (shizukuState == ShizukuState.NO_PERMISSION) {
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(DexorDimensions.cornerSmall),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = DexorDimensions.minTouchTarget)
                ) {
                    Text("授予 Shizuku 权限", fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                Text(
                    text = "需要帮助？查看官方设置指南",
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
            } else if (shizukuState == ShizukuState.NOT_RUNNING) {
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
                        Text("设置指南", style = MaterialTheme.typography.labelMedium)
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
                        Text("刷新状态", style = MaterialTheme.typography.labelMedium, color = Color.White)
                    }
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
                    text = "100% 离线 & 隐私安全",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Text(
                text = "Dexor 不申请任何网络权限（Manifest 完全未声明 android.permission.INTERNET）。应用不包含任何埋点、统计、追踪分析或崩溃日志上报 SDK，所有操作与数据仅在本地执行与保留。",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF90A4AE),
                lineHeight = 20.sp
            )

            Divider(color = Color(0xFF1A2130), thickness = 0.8.dp)

            Text(
                text = "系统安全性保障",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Text(
                text = "所有的编译操作均通过 Shizuku 调用 Android 原生包管理命令（cmd package compile）。Dexor 不会修改只读系统分区，不触碰任何系统安全配置，不影响设备官方完整性。",
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
                    text = "开发者",
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

            MinimalInfoRow(label = "开源协议", value = "MIT License")
            Divider(color = Color(0xFF1A2130), thickness = 0.8.dp)

            Text(
                text = "第三方开源组件",
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
                    Text("项目主页", style = MaterialTheme.typography.labelMedium)
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
                    Text("版本发布", style = MaterialTheme.typography.labelMedium)
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
