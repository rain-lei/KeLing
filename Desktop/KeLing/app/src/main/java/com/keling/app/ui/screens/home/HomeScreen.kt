package com.keling.app.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.ui.components.*
import com.keling.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToTask: (String) -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToCourseDetail: (String) -> Unit,
    onNavigateToFocus: () -> Unit,
    onNavigateToAIAssistant: () -> Unit,
    onNavigateToCampusPlanet: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(PaperBackground),
            contentPadding = PaddingValues(
                horizontal = KelingSpacing.horizontalPage,
                vertical = KelingSpacing.verticalPage
            )
        ) {
            // 顶部：问候 + 日期 + 头像（参考图结构）
            item {
                HomeHeader(
                    userName = uiState.userName,
                    dateString = remember { formatHomeDate() },
                    level = uiState.level,
                    experience = uiState.experience,
                    maxExperience = uiState.maxExperience,
                    streak = uiState.streak
                )
            }

            // 学习计划大卡片：三格数据 + 平均进度 + 进度条
            item {
                LearningPlanCard(
                    todayTaskCount = uiState.todayTaskCount,
                    completedTaskCount = uiState.completedTaskCount,
                    studyMinutes = uiState.todayStudyMinutes,
                    onLearnMore = onNavigateToCourses
                )
            }

            // 快速开始
            item {
                QuickStartSection(
                    onStartTask = onNavigateToFocus,
                    onOpenSchedule = onNavigateToCourses,
                    onOpenAI = onNavigateToAIAssistant
                )
            }

            // 云端校园星球入口
            item {
                CampusPlanetEntry(
                    onClick = onNavigateToCampusPlanet,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(140.dp)
                )
            }

            // 今日任务
            item {
                SectionHeader(title = "今日任务", actionText = "查看全部")
            }
            
            items(uiState.todayTasks) { task ->
                TaskCard(
                    title = task.title,
                    description = task.description,
                    progress = task.progress,
                    difficulty = task.difficulty.name,
                    expReward = task.experienceReward,
                    onClick = { onNavigateToTask(task.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
            
            // 最近课程
            item {
                SectionHeader(title = "最近课程", actionText = "课程表")
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.recentCourses) { course ->
                        CourseCard(
                            name = course.name,
                            teacherName = course.teacherName,
                            progress = course.progress,
                            credits = course.credits,
                            onClick = { onNavigateToCourseDetail(course.id) },
                            modifier = Modifier.width(280.dp)
                        )
                    }
                }
            }
            
            // 能力成长
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "能力成长")
                Spacer(modifier = Modifier.height(12.dp))
                
                NeonCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    glowColor = NeonPurple
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        RadarChart(
                            data = uiState.skillGrowth,
                            modifier = Modifier.size(180.dp),
                            color = NeonPurple
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 技能标签
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        uiState.skillGrowth.forEach { (skill, value) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = skill,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkSecondary
                                )
                                Text(
                                    text = "${(value * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NeonPurple
                                )
                            }
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
        
        // AI助手浮动按钮
        AIFloatingButton(
            onClick = onNavigateToAIAssistant,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

private fun formatHomeDate(): String {
    val sdf = SimpleDateFormat("EEEE，M月d日", Locale.CHINESE)
    return sdf.format(Date())
}

@Composable
private fun HomeHeader(
    userName: String,
    dateString: String,
    level: Int,
    experience: Int,
    maxExperience: Int,
    streak: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = KelingSpacing.horizontalPage, vertical = KelingSpacing.verticalPage)
    ) {
        // 一行：左侧问候+日期，右侧头像
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "你好，$userName 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkSecondary
                )
            }
            // 头像：圆形 + 首字或图标
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(NeonBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // 经验值条
        ExperienceBar(
            currentExp = experience,
            maxExp = maxExperience,
            level = level,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = NeonOrange,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "连续学习 $streak 天",
                style = MaterialTheme.typography.labelSmall,
                color = NeonOrange
            )
        }
    }
}

/** 学习计划大卡片：三格数据 + 平均进度 + 进度条（参考设计图） */
@Composable
private fun LearningPlanCard(
    todayTaskCount: Int,
    completedTaskCount: Int,
    studyMinutes: Int,
    onLearnMore: () -> Unit
) {
    val averageProgress = if (todayTaskCount > 0) (completedTaskCount.toFloat() / todayTaskCount * 100).toInt() else 0
    val upcomingCount = (todayTaskCount - completedTaskCount).coerceAtLeast(0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KelingSpacing.horizontalPage, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(PaperSurface)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "学习计划",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(20.dp))
            // 三格数据：总任务、已完成、待办
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PlanStatItem(
                    icon = Icons.Default.Assignment,
                    value = todayTaskCount.toString(),
                    label = "总任务",
                    color = NeonGreen
                )
                PlanStatItem(
                    icon = Icons.Default.CheckCircle,
                    value = completedTaskCount.toString(),
                    label = "已完成",
                    color = NeonGreen
                )
                PlanStatItem(
                    icon = Icons.Default.Schedule,
                    value = upcomingCount.toString(),
                    label = "待办",
                    color = NeonGreen
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // 平均进度 + 了解更多
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$averageProgress%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    )
                    Text(
                        text = "平均进度",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkSecondary
                    )
                }
                TextButton(onClick = onLearnMore) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = NeonBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "了解更多",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // 今日进度条
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "今日进度",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkSecondary
                    )
                    Text(
                        text = "$completedTaskCount / $todayTaskCount 任务 · ${studyMinutes} 分钟",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                NeonProgressBar(
                    progress = if (todayTaskCount > 0) completedTaskCount.toFloat() / todayTaskCount else 0f,
                    color = NeonBlue,
                    modifier = Modifier.fillMaxWidth(),
                    height = 8.dp
                )
            }
        }
    }
}

@Composable
private fun PlanStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = InkPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = InkSecondary
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    NeonCard(
        modifier = modifier,
        glowColor = color
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun QuickStartSection(
    onStartTask: () -> Unit,
    onOpenSchedule: () -> Unit,
    onOpenAI: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionButton(
            icon = Icons.Default.PlayArrow,
            label = "开始学习",
            color = NeonGreen,
            onClick = onStartTask,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.CalendarToday,
            label = "今日课表",
            color = NeonBlue,
            onClick = onOpenSchedule,
            modifier = Modifier.weight(1f)
        )
        QuickActionButton(
            icon = Icons.Default.SmartToy,
            label = "AI助手",
            color = NeonPurple,
            onClick = onOpenAI,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GradientCard(
        modifier = modifier,
        gradientColors = listOf(color, color.copy(alpha = 0.5f)),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = InkPrimary
            )
        }
    }
}

@Composable
private fun CampusPlanetEntry(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeonCard(
        modifier = modifier,
        glowColor = NeonBlue,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 星空与流光背景，营造宇宙粒子感
            StarryBackground(
                modifier = Modifier
                    .matchParentSize()
            )
            StreamingLightEffect(
                modifier = Modifier
                    .matchParentSize(),
                color = NeonPurple
            )

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "云端校园星球",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "探索论坛星球 · 实践星球，解锁校园任务与社交",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onAction: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = InkPrimary
        )
        if (actionText != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonBlue
                )
            }
        }
    }
}
