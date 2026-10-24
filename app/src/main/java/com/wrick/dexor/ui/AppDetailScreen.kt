package com.wrick.dexor.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wrick.dexor.R
import com.wrick.dexor.model.AppInfo
import com.wrick.dexor.model.COMPILE_MODES
import com.wrick.dexor.model.InstallSource
import com.wrick.dexor.model.getDexReasonDisplay
import com.wrick.dexor.ui.theme.DexorCodeFont
import com.wrick.dexor.ui.theme.DexorDimensions
import com.wrick.dexor.ui.util.AppIconImageLoader
import com.wrick.dexor.ui.util.AppIconKey
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    app: AppInfo,
    isCompiling: Boolean,
    isShizukuReady: Boolean = true,
    onRequestPermission: () -> Unit = {},
    onBack: () -> Unit,
    onCompile: (mode: String, force: Boolean) -> Unit,
    showBackButton: Boolean = true,
    initialForceCompile: Boolean = true,
    onForceCompileChange: ((Boolean) -> Unit)? = null
) {
    // 拦截系统返回键
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    var selectedModeId by remember { mutableStateOf("speed-profile") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var forceCompileState by remember { mutableStateOf(initialForceCompile) }

    val currentModeObj = COMPILE_MODES.find { it.id == selectedModeId } ?: COMPILE_MODES[0]

    // 模式选择弹窗
    if (showFilterDialog) {
        Dialog(
            onDismissRequest = { showFilterDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF131720),
                tonalElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        "选择编译模式",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "为 ${app.name} 选择 ART 编译模式",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF90A4AE)
                    )
                    Spacer(Modifier.height(16.dp))

                    COMPILE_MODES.forEach { mode ->
                        val isSelected = selectedModeId == mode.id
                        Surface(
                            onClick = {
                                selectedModeId = mode.id
                                showFilterDialog = false
                            },
                            color = if (isSelected) Color(0xFF1E293B) else Color.Transparent,
                            shape = RoundedCornerShape(DexorDimensions.cornerMedium),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = DexorDimensions.minTouchTarget)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        selectedModeId = mode.id
                                        showFilterDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = if (mode.isReset) Color(0xFFFF8A80) else Color(0xFF90CAF9)
                                    )
                                )
                                Spacer(Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            mode.label,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (mode.isReset) Color(0xFFFF8A80) else Color.White
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "~${mode.estimatedSecondsPerApp}秒",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF78909C)
                                        )
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        mode.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF90A4AE)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showFilterDialog = false },
                            shape = RoundedCornerShape(DexorDimensions.cornerSmall)
                        ) {
                            Text(
                                "取消",
                                color = Color(0xFF90CAF9),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = app.name,
                            maxLines = 1,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = app.packageName,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = DexorCodeFont),
                            color = Color(0xFF90A4AE)
                        )
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(DexorDimensions.minTouchTarget)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F1115),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFF131720),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(DexorDimensions.spaceDefault),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 左下角：已选模式选择入口
                    Surface(
                        onClick = { showFilterDialog = true },
                        color = Color(0xFF1A2130),
                        shape = RoundedCornerShape(DexorDimensions.cornerMedium),
                        modifier = Modifier.defaultMinSize(minHeight = DexorDimensions.minTouchTarget)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "已选模式",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF90A4AE)
                                )
                                Text(
                                    currentModeObj.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentModeObj.isReset) Color(0xFFFF8A80) else Color(0xFF90CAF9)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "更改模式",
                                tint = Color(0xFF90CAF9)
                            )
                        }
                    }

                    // 右下角：开始优化按钮
                    Button(
                        onClick = {
                            if (!isShizukuReady) {
                                onRequestPermission()
                            } else {
                                onCompile(selectedModeId, forceCompileState)
                            }
                        },
                        enabled = !isCompiling,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                !isShizukuReady -> Color(0xFFE65100)
                                currentModeObj.isReset -> Color(0xFFD32F2F)
                                else -> Color(0xFF2196F3)
                            }
                        ),
                        shape = RoundedCornerShape(DexorDimensions.cornerMedium),
                        modifier = Modifier.defaultMinSize(minHeight = DexorDimensions.minTouchTarget)
                    ) {
                        if (isCompiling) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(DexorDimensions.iconSizeSmall),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(DexorDimensions.spaceSmall))
                            Text(if (currentModeObj.isReset) "正在清除..." else "正在优化...")
                        } else if (!isShizukuReady) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(DexorDimensions.iconSizeSmall)
                            )
                            Spacer(Modifier.width(DexorDimensions.spaceExtraSmall))
                            Text("授予权限")
                        } else {
                            Icon(
                                if (currentModeObj.isReset) Icons.Default.Refresh else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(DexorDimensions.iconSizeSmall)
                            )
                            Spacer(Modifier.width(DexorDimensions.spaceExtraSmall))
                            Text(if (currentModeObj.isReset) "开始清除" else "开始优化")
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF090A0C)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(DexorDimensions.spaceDefault),
            verticalArrangement = Arrangement.spacedBy(DexorDimensions.spaceDefault)
        ) {
            // 头部应用信息卡片
            Surface(
                color = Color(0xFF131720),
                shape = RoundedCornerShape(DexorDimensions.cornerLarge),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(DexorDimensions.spaceDefault)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(DexorDimensions.cornerSmall),
                            color = Color(0xFF1E2433),
                            modifier = Modifier.size(DexorDimensions.appIconSizeDetail)
                        ) {
                            AsyncImage(
                                model = remember(app.packageName) { AppIconKey(app.packageName) },
                                contentDescription = null,
                                imageLoader = AppIconImageLoader.get(context),
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(Modifier.width(DexorDimensions.spaceDefault))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                app.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Spacer(Modifier.height(DexorDimensions.spaceExtraSmall))
                            Text(
                                app.packageName,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = DexorCodeFont),
                                color = Color(0xFF90A4AE)
                            )
                        }

                        if (app.source == InstallSource.GOOGLE_PLAY) {
                            Surface(
                                color = Color(0xFF1B2838),
                                shape = RoundedCornerShape(DexorDimensions.cornerSmall)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_google_play),
                                        contentDescription = "Google Play",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "Google Play",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFECEFF1)
                                    )
                                }
                            }
                        } else {
                            Surface(
                                color = when (app.source) {
                                    InstallSource.SYSTEM -> Color(0xFF37474F)
                                    else -> Color(0xFF0D47A1)
                                },
                                shape = RoundedCornerShape(DexorDimensions.cornerSmall)
                            ) {
                                Text(
                                    text = if (app.source == InstallSource.SYSTEM) "系统应用" else "用户应用",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(DexorDimensions.spaceMedium))
                    Divider(color = Color(0xFF1E2430))
                    Spacer(Modifier.height(DexorDimensions.spaceMedium))

                    // 实时状态徽章：编译模式 & 触发原因
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DexorDimensions.spaceMedium)
                    ) {
                        MetricCard(
                            label = "编译模式",
                            value = app.dexStatus,
                            color = when (app.dexStatus.lowercase()) {
                                "speed" -> Color(0xFF2E7D32)
                                "speed-profile" -> Color(0xFF388E3C)
                                "everything" -> Color(0xFF1565C0)
                                "space" -> Color(0xFFF57F17)
                                "verify" -> Color(0xFFE65100)
                                "run-from-apk", "interpret-only" -> Color(0xFFC62828)
                                else -> Color(0xFF455A64)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "优化原因",
                            value = getDexReasonDisplay(app.dexReason),
                            color = Color(0xFF263238),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 强制编译开关卡片（参考截图 5）
            Surface(
                color = Color(0xFF131720),
                shape = RoundedCornerShape(DexorDimensions.cornerLarge),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DexorDimensions.spaceDefault),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE040FB).copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "⚡",
                                    fontSize = 18.sp
                                )
                            }
                        }
                        Spacer(Modifier.width(DexorDimensions.spaceMedium))
                        Column {
                            Text(
                                "强制编译",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "即使已有编译产物，也重新执行所选编译模式",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF90A4AE)
                            )
                        }
                    }
                    Switch(
                        checked = forceCompileState,
                        onCheckedChange = { checked ->
                            forceCompileState = checked
                            onForceCompileChange?.invoke(checked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2196F3)
                        )
                    )
                }
            }

            // 安装包信息与文件路径详情卡片
            Surface(
                color = Color(0xFF131720),
                shape = RoundedCornerShape(DexorDimensions.cornerLarge),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(DexorDimensions.spaceDefault),
                    verticalArrangement = Arrangement.spacedBy(DexorDimensions.spaceMedium)
                ) {
                    Text(
                        "安装包信息与文件路径",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF90CAF9)
                    )

                    SpecRow("应用版本", "${app.versionName} (${app.versionCode})")
                    SpecRow("目标 / 最低 SDK", "Android ${app.targetSdk} (API ${app.targetSdk}) / Min ${app.minSdk ?: "N/A"}")
                    SpecRow("最后更新时间", formatDetailTime(app.lastUpdateTime))
                    if (app.lastCompilationTime != null) {
                        SpecRow("最后编译时间", formatDetailTime(app.lastCompilationTime))
                    }
                    if (app.lastStatusChangeTime != null) {
                        SpecRow("状态变更时间", formatDetailTime(app.lastStatusChangeTime))
                    }
                    SpecRow("APK 文件路径", app.codePath ?: "未知", isMonospace = true)
                    SpecRow("私有数据目录", app.dataDir ?: "未知", isMonospace = true)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color,
        shape = RoundedCornerShape(DexorDimensions.cornerMedium),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = DexorDimensions.spaceMedium,
                vertical = DexorDimensions.spaceMedium
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFCFD8DC),
                maxLines = 1
            )
            Spacer(Modifier.height(DexorDimensions.spaceExtraSmall))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String, isMonospace: Boolean = false) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF78909C)
        )
        Text(
            text = value,
            style = if (isMonospace) MaterialTheme.typography.bodySmall.copy(fontFamily = DexorCodeFont)
                    else MaterialTheme.typography.bodyMedium,
            color = Color(0xFFECEFF1)
        )
    }
}

private val detailDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

private fun formatDetailTime(ts: Long): String {
    return synchronized(detailDateFormat) {
        detailDateFormat.format(Date(ts))
    }
}
