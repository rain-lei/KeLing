package com.keling.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.ui.components.*
import com.keling.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToLearningReport: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // 顶部设置按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = InkSecondary
                )
            }
        }
        
        // 用户头像和信息（连续学习天数随签到数据更新）
        ProfileHeader(streak = profileState.streak)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 等级和经验
        ExperienceBar(
            currentExp = 2450,
            maxExp = 3000,
            level = 12,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 统计数据
        StatsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 功能入口
        ProfileMenuSection(
            onNavigateToLearningReport = onNavigateToLearningReport
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileHeader(streak: Int = 0) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(NeonBlue, NeonPurple)
                    )
                )
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(NeonBlue, NeonPurple, NeonPink)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "学",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 用户名
        Text(
            text = "学习达人",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = InkPrimary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 学校班级
        Text(
            text = "大连理工大学 · 计算机2024级",
            style = MaterialTheme.typography.bodyMedium,
            color = InkSecondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 连续学习天数
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = NeonOrange.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = NeonOrange,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "连续学习 $streak 天",
                style = MaterialTheme.typography.labelLarge,
                color = NeonOrange
            )
        }
    }
}

@Composable
private fun StatsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            value = "45",
            label = "完成任务",
            icon = Icons.Default.Assignment,
            color = NeonBlue,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "12",
            label = "获得成就",
            icon = Icons.Default.EmojiEvents,
            color = NeonGold,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "21h",
            label = "学习时长",
            icon = Icons.Default.Timer,
            color = NeonGreen,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
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
private fun ProfileMenuSection(
    onNavigateToLearningReport: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "学习工具",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = InkPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ProfileMenuItem(
            icon = Icons.Default.Analytics,
            title = "学情报告",
            subtitle = "查看学习分析和建议",
            color = NeonPurple,
            onClick = onNavigateToLearningReport
        )
        
        ProfileMenuItem(
            icon = Icons.Default.AccountTree,
            title = "知识图谱",
            subtitle = "探索知识点关联",
            color = NeonBlue,
            onClick = { }
        )
        
        ProfileMenuItem(
            icon = Icons.Default.History,
            title = "学习记录",
            subtitle = "查看历史学习轨迹",
            color = NeonGreen,
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "其他功能",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = InkPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ProfileMenuItem(
            icon = Icons.Default.People,
            title = "我的好友",
            subtitle = "查看好友动态",
            color = NeonPink,
            onClick = { }
        )
        
        ProfileMenuItem(
            icon = Icons.Default.Leaderboard,
            title = "排行榜",
            subtitle = "查看学习排名",
            color = NeonGold,
            onClick = { }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    NeonCard(
        glowColor = color.copy(alpha = 0.3f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
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
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = InkMuted
            )
        }
    }
}
