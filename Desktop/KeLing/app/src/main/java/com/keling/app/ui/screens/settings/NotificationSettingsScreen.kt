package com.keling.app.ui.screens.settings

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.keling.app.ui.components.NeonCard
import com.keling.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit
) {
    var pushEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var taskReminder by remember { mutableStateOf(true) }
    var studyReminder by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "通知设置",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "推送与提醒",
                style = MaterialTheme.typography.titleMedium,
                color = InkPrimary
            )
            NeonCard(glowColor = PaperBorder) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    NotificationSwitch(
                        title = "推送通知",
                        subtitle = "任务提醒、学习打卡",
                        checked = pushEnabled,
                        onCheckedChange = { pushEnabled = it }
                    )
                    NotificationSwitch(
                        title = "声音",
                        subtitle = "音效反馈",
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it }
                    )
                    NotificationSwitch(
                        title = "震动",
                        subtitle = "触觉反馈",
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it }
                    )
                }
            }

            Text(
                text = "提醒类型",
                style = MaterialTheme.typography.titleMedium,
                color = InkPrimary
            )
            NeonCard(glowColor = PaperBorder) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    NotificationSwitch(
                        title = "任务提醒",
                        subtitle = "待办任务到期提醒",
                        checked = taskReminder,
                        onCheckedChange = { taskReminder = it }
                    )
                    NotificationSwitch(
                        title = "学习打卡提醒",
                        subtitle = "每日学习目标提醒",
                        checked = studyReminder,
                        onCheckedChange = { studyReminder = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NotificationSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
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
                text = subtitle,
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
