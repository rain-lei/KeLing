package com.keling.app.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.data.model.TaskType
import com.keling.app.ui.components.*
import com.keling.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel = hiltViewModel(),
    onNavigateToTaskDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("全部", "日常", "挑战", "组队")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .statusBarsPadding()
    ) {
        // 顶部标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "任务中心",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )
            
            Row {
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "刷新任务",
                        tint = InkSecondary
                    )
                }
                IconButton(onClick = { /* 搜索 */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = InkSecondary
                    )
                }
            }
        }
        
        // 任务统计
        TaskStatsRow(
            totalTasks = uiState.totalTasks,
            completedTasks = uiState.completedTasks,
            inProgressTasks = uiState.inProgressTasks
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 标签切换
        NeonTabRow(
            tabs = tabs,
            selectedIndex = selectedTab,
            onTabSelected = { 
                selectedTab = it
                viewModel.filterByType(
                    when (it) {
                        0 -> null
                        1 -> TaskType.DAILY
                        2 -> TaskType.CHALLENGE
                        3 -> TaskType.TEAM
                        else -> null
                    }
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 任务列表
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.filteredTasks) { task ->
                TaskCard(
                    title = task.title,
                    description = task.description,
                    progress = task.progress,
                    difficulty = task.difficulty.name,
                    expReward = task.experienceReward,
                    onClick = { onNavigateToTaskDetail(task.id) }
                )
            }
            
            if (uiState.filteredTasks.isEmpty()) {
                item {
                    EmptyTasksPlaceholder()
                }
            }
        }
    }
}

@Composable
private fun TaskStatsRow(
    totalTasks: Int,
    completedTasks: Int,
    inProgressTasks: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TaskStatItem(
            label = "总任务",
            value = totalTasks.toString(),
            color = NeonBlue,
            modifier = Modifier.weight(1f)
        )
        TaskStatItem(
            label = "进行中",
            value = inProgressTasks.toString(),
            color = NeonOrange,
            modifier = Modifier.weight(1f)
        )
        TaskStatItem(
            label = "已完成",
            value = completedTasks.toString(),
            color = NeonGreen,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TaskStatItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    NeonCard(
        modifier = modifier,
        glowColor = color
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = InkSecondary
            )
        }
    }
}

@Composable
private fun EmptyTasksPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = NeonGreen,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "暂无任务",
            style = MaterialTheme.typography.titleMedium,
            color = InkSecondary
        )
        Text(
            text = "休息一下或探索新的挑战吧！",
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted
        )
    }
}
