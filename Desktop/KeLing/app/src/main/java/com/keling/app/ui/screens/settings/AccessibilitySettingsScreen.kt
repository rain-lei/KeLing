package com.keling.app.ui.screens.settings

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.ui.components.*
import com.keling.app.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilitySettingsScreen(
    onBack: () -> Unit
) {
    val viewModel: AccessibilityViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val testSentence = "这是课灵无障碍语音测试。"

    fun speakTest() {
        var localTts: TextToSpeech? = null
        localTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                localTts?.language = Locale.CHINESE
                localTts?.setSpeechRate(state.ttsSpeechRate)
                localTts?.speak(testSentence, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                localTts?.shutdown()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "无障碍设置",
                    style = MaterialTheme.typography.titleLarge,
                    color = InkPrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = InkPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PaperBackground
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 视觉辅助
            AccessibilitySection(title = "视觉辅助") {
                // 字体大小
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "字体大小",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary
                            )
                            Text(
                                text = when (state.fontSizeLevel) {
                                    0 -> "小"
                                    1 -> "默认"
                                    2 -> "大"
                                    else -> "超大"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("小" to 12, "默认" to 14, "大" to 18, "超大" to 24).forEachIndexed { index, (label, size) ->
                            val isSelected = state.fontSizeLevel == index
                            FilterChip(
                                onClick = { viewModel.setFontSizeLevel(index) },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = size.sp
                                    )
                                },
                                selected = isSelected,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonBlue.copy(alpha = 0.2f),
                                    selectedLabelColor = NeonBlue,
                                    containerColor = PaperSurface,
                                    labelColor = InkSecondary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AccessibilitySwitch(
                    title = "高对比度模式",
                    description = "增强颜色对比度，文字与轮廓更清晰",
                    checked = state.highContrastMode,
                    onCheckedChange = { viewModel.setHighContrastMode(it) }
                )

                AccessibilitySwitch(
                    title = "减少动画效果",
                    description = "减少界面动画，降低视觉干扰",
                    checked = state.reduceMotion,
                    onCheckedChange = { viewModel.setReduceMotion(it) }
                )
            }

            // 听觉辅助
            AccessibilitySection(title = "听觉辅助") {
                AccessibilitySwitch(
                    title = "语音朗读 (TTS)",
                    description = "开启后可使用测试语音",
                    checked = state.ttsEnabled,
                    onCheckedChange = { viewModel.setTtsEnabled(it) }
                )

                if (state.ttsEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "语速",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary
                            )
                            Text(
                                text = "${state.ttsSpeechRate}x",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeonBlue
                            )
                        }

                        Slider(
                            value = state.ttsSpeechRate,
                            onValueChange = { viewModel.setTtsSpeechRate(it) },
                            valueRange = 0.5f..2f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonBlue,
                                activeTrackColor = NeonBlue,
                                inactiveTrackColor = PaperBorder
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("慢", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                            Text("快", style = MaterialTheme.typography.labelSmall, color = InkMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    NeonOutlinedButton(
                        text = "测试语音",
                        onClick = { speakTest() },
                        color = NeonPurple,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 运动辅助
            AccessibilitySection(title = "运动辅助") {
                AccessibilitySwitch(
                    title = "手势控制",
                    description = "启用滑动手势进行导航操作",
                    checked = state.gestureControlEnabled,
                    onCheckedChange = { viewModel.setGestureControlEnabled(it) }
                )

                if (state.gestureControlEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))

                    NeonCard(glowColor = NeonGreen.copy(alpha = 0.3f)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            GestureHint(
                                gesture = "单指左右滑动",
                                action = "翻页"
                            )
                            GestureHint(
                                gesture = "双指点击",
                                action = "返回上一级"
                            )
                            GestureHint(
                                gesture = "长按电源键3次",
                                action = "紧急求助"
                            )
                        }
                    }
                }
            }

            // 语音指令
            AccessibilitySection(title = "语音指令") {
                NeonCard(glowColor = NeonPurple.copy(alpha = 0.3f)) {
                    Text(
                        text = "支持的语音指令",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        VoiceCommandHint(
                            command = "\"课灵，帮帮我\"",
                            description = "唤醒AI助手"
                        )
                        VoiceCommandHint(
                            command = "\"下一页\"",
                            description = "翻到下一页"
                        )
                        VoiceCommandHint(
                            command = "\"返回主界面\"",
                            description = "回到首页"
                        )
                        VoiceCommandHint(
                            command = "\"联系助教\"",
                            description = "紧急求助"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AccessibilitySection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = InkPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        NeonCard(glowColor = PaperBorder) {
            Column(content = content)
        }
    }
}

@Composable
private fun AccessibilitySwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonBlue,
                checkedTrackColor = NeonBlue.copy(alpha = 0.3f),
                uncheckedThumbColor = InkMuted,
                uncheckedTrackColor = PaperBorder
            )
        )
    }
}

@Composable
private fun GestureHint(
    gesture: String,
    action: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = gesture,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSecondary
        )
        Text(
            text = action,
            style = MaterialTheme.typography.bodyMedium,
            color = NeonGreen
        )
    }
}

@Composable
private fun VoiceCommandHint(
    command: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            tint = NeonPurple,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = command,
            style = MaterialTheme.typography.bodyMedium,
            color = NeonPurple,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = InkSecondary
        )
    }
}
