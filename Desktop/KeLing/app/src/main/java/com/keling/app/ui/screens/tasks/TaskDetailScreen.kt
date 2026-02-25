package com.keling.app.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.data.model.TaskStatus
import com.keling.app.data.model.TaskType
import com.keling.app.ui.components.*
import com.keling.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    onBack: () -> Unit,
    onStartExecution: () -> Unit = {},
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(taskId) { viewModel.loadTask(taskId) }

    Column(modifier = Modifier.fillMaxSize().background(PaperBackground)) {
        TopAppBar(
            title = { Text("任务详情", color = InkPrimary) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = InkPrimary)
                }
            },
            actions = {
                IconButton(onClick = { /* 分享 */ }) {
                    Icon(Icons.Default.Share, contentDescription = "分享", tint = InkSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
        )

        when {
            uiState.isLoading && uiState.task == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonBlue)
                }
            }
            uiState.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.error!!, color = NeonRed)
                        Spacer(Modifier.height(16.dp))
                        TextButton(onClick = onBack) { Text("返回", color = NeonBlue) }
                    }
                }
            }
            uiState.task != null -> {
                val task = uiState.task!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary
                            )
                            Spacer(Modifier.height(8.dp))
                            Row {
                                DifficultyBadge(difficulty = task.difficulty.name)
                                Spacer(Modifier.width(8.dp))
                                task.courseId?.let { id ->
                                    Text(
                                        text = id,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = InkSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    NeonCard(glowColor = NeonBlue) {
                        Text("完成进度", style = MaterialTheme.typography.titleSmall, color = InkSecondary)
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularNeonProgress(
                                progress = task.progress,
                                size = 80.dp,
                                strokeWidth = 8.dp,
                                color = NeonBlue
                            )
                            Spacer(Modifier.width(24.dp))
                            Column {
                                Text(
                                    text = "预计耗时: ${task.estimatedMinutes} 分钟",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkPrimary
                                )
                                if (task.chapterId != null) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "章节: ${task.chapterId}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = InkSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    NeonCard(glowColor = NeonGold) {
                        Text("完成奖励", style = MaterialTheme.typography.titleSmall, color = InkSecondary)
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            RewardItem(
                                icon = Icons.Default.Stars,
                                value = "+${task.experienceReward}",
                                label = "经验值",
                                color = NeonGold
                            )
                            RewardItem(
                                icon = Icons.Default.Paid,
                                value = "+${task.coinReward}",
                                label = "金币",
                                color = NeonOrange
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    NeonCard(glowColor = NeonPurple) {
                        Text("任务说明", style = MaterialTheme.typography.titleSmall, color = InkSecondary)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkPrimary
                        )
                        if (task.chapterId != null) {
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(PaperSurfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = NeonPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = task.chapterId,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkPrimary
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // 日常任务专用计时器（在应用内部完成）
                    if (task.type == TaskType.DAILY && task.status != TaskStatus.COMPLETED) {
                        DailyTaskTimer(
                            onFinish = { minutes ->
                                viewModel.completeDailyTaskWithMinutes(minutes)
                            }
                        )
                        Spacer(Modifier.height(24.dp))
                    }

                    if (task.status == TaskStatus.COMPLETED) {
                        NeonOutlinedButton(
                            text = "已完成",
                            onClick = { },
                            color = NeonGreen,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (uiState.canExecute) {
                        NeonButton(
                            text = "开始任务",
                            onClick = onStartExecution,
                            color = NeonGreen,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        NeonOutlinedButton(
                            text = "稍后提醒",
                            onClick = { },
                            color = NeonBlue,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "该任务暂不支持在应用内执行",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = InkSecondary)
    }
}

@Composable
private fun DailyTaskTimer(
    onFinish: (Int) -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            elapsedSeconds++
        }
    }

    NeonCard(glowColor = NeonGreen) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "日常任务计时",
                style = MaterialTheme.typography.titleSmall,
                color = InkSecondary
            )
            Text(
                text = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = NeonGreen
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeonButton(
                    text = if (isRunning) "暂停" else "开始计时",
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.weight(1f),
                    color = NeonGreen
                )
                NeonOutlinedButton(
                    text = "完成并打卡",
                    onClick = {
                        isRunning = false
                        val minutes = (elapsedSeconds / 60).coerceAtLeast(1)
                        onFinish(minutes)
                    },
                    modifier = Modifier.weight(1f),
                    color = NeonBlue
                )
            }
        }
    }
}
