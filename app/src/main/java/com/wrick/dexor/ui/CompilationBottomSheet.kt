package com.wrick.dexor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wrick.dexor.model.COMPILE_MODES
import com.wrick.dexor.ui.theme.DexorDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompilationBottomSheet(
    appName: String?,
    batchCount: Int = 0,
    forceCompile: Boolean = true,
    onForceCompileChange: ((Boolean) -> Unit)? = null,
    onDismissRequest: () -> Unit,
    onCompileModeSelected: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color(0xFF131720)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = if (appName != null) "优化应用：$appName"
                       else "批量优化 ($batchCount 个应用)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // 强制编译选项
            if (onForceCompileChange != null) {
                Surface(
                    color = Color(0xFF1A2130),
                    shape = RoundedCornerShape(DexorDimensions.cornerMedium),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "强制重新编译 (-f)",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "即使已有编译产物，也强制重新执行选定模式",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF90A4AE)
                            )
                        }
                        Switch(
                            checked = forceCompile,
                            onCheckedChange = onForceCompileChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2196F3)
                            )
                        )
                    }
                }
            }

            Divider(color = Color(0xFF1E2430), modifier = Modifier.padding(vertical = 8.dp))

            COMPILE_MODES.forEach { mode ->
                Surface(
                    color = Color.Transparent,
                    onClick = { onCompileModeSelected(mode.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (mode.isReset) Color(0xFFFF8A80) else Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "单应用约 ${mode.estimatedSecondsPerApp} 秒",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF78909C)
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = mode.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF90A4AE)
                        )
                        if (batchCount > 0) {
                            val totalSec = mode.estimatedSecondsPerApp * batchCount
                            val minutes = totalSec / 60
                            val seconds = totalSec % 60
                            val timeStr = if (minutes > 0) "${minutes} 分 ${seconds} 秒" else "${seconds} 秒"
                            Text(
                                text = "预计总耗时：约 $timeStr ($batchCount 个软件包)",
                                fontSize = 11.sp,
                                color = Color(0xFF90CAF9),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
