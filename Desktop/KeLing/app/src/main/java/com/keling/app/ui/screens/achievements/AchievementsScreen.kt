package com.keling.app.ui.screens.achievements

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.keling.app.ui.components.*
import com.keling.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("全部", "学习", "任务", "连续", "特殊")
    
    // 模拟成就数据
    val achievements = remember { createDemoAchievements() }
    val filteredAchievements = when (selectedTab) {
        0 -> achievements
        1 -> achievements.filter { it.category == "LEARNING" }
        2 -> achievements.filter { it.category == "TASK" }
        3 -> achievements.filter { it.category == "STREAK" }
        4 -> achievements.filter { it.category == "SPECIAL" }
        else -> achievements
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .statusBarsPadding()
    ) {
        // 顶部标题
        Text(
            text = "成就殿堂",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = InkPrimary,
            modifier = Modifier.padding(16.dp)
        )
        
        // 成就统计
        AchievementStats(
            total = achievements.size,
            unlocked = achievements.count { it.isUnlocked }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 标签切换
        NeonTabRow(
            tabs = tabs,
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it },
            color = NeonGold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 成就网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredAchievements) { achievement ->
                AchievementItem(achievement = achievement)
            }
        }
    }
}

@Composable
private fun AchievementStats(
    total: Int,
    unlocked: Int
) {
    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        glowColor = NeonGold
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$unlocked",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonGold
                )
                Text(
                    text = "已解锁",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary
                )
            }
            
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(PaperBorder)
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$total",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkSecondary
                )
                Text(
                    text = "总成就",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary
                )
            }
            
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(PaperBorder)
            )
            
            CircularNeonProgress(
                progress = unlocked.toFloat() / total,
                size = 60.dp,
                strokeWidth = 6.dp,
                color = NeonGold,
                showPercentage = true
            )
        }
    }
}

@Composable
private fun AchievementItem(achievement: AchievementUi) {
    val rarityColor = when (achievement.rarity) {
        "COMMON" -> InkSecondary
        "RARE" -> NeonBlue
        "EPIC" -> NeonPurple
        "LEGENDARY" -> NeonGold
        else -> InkSecondary
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "legendary")
    val legendaryGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    val alpha = if (achievement.isUnlocked) 1f else 0.4f
    val scale = if (achievement.isUnlocked) 1f else 0.95f
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .alpha(alpha)
            .background(
                color = PaperSurface,
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (achievement.isUnlocked && achievement.rarity == "LEGENDARY") {
                    Modifier.border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                NeonGold.copy(alpha = legendaryGlow),
                                NeonOrange.copy(alpha = legendaryGlow * 0.8f),
                                NeonGold.copy(alpha = legendaryGlow)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else if (achievement.isUnlocked) {
                    Modifier.border(
                        width = 1.dp,
                        color = rarityColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 成就图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (achievement.isUnlocked) rarityColor.copy(alpha = 0.2f) 
                               else PaperSurfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (achievement.category) {
                        "LEARNING" -> Icons.Default.School
                        "TASK" -> Icons.Default.Assignment
                        "STREAK" -> Icons.Default.LocalFireDepartment
                        "SPECIAL" -> Icons.Default.Star
                        else -> Icons.Default.EmojiEvents
                    },
                    contentDescription = null,
                    tint = if (achievement.isUnlocked) rarityColor else InkMuted,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 成就名称
            Text(
                text = achievement.name,
                style = MaterialTheme.typography.labelSmall,
                color = if (achievement.isUnlocked) InkPrimary else InkMuted,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            // 未解锁时显示进度
            if (!achievement.isUnlocked && achievement.progress > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                NeonProgressBar(
                    progress = achievement.progress,
                    color = rarityColor,
                    height = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // 锁定图标
        if (!achievement.isUnlocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = InkMuted.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
            )
        }
    }
}

// 数据类
data class AchievementUi(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val rarity: String,
    val isUnlocked: Boolean,
    val progress: Float = 0f
)

private fun createDemoAchievements(): List<AchievementUi> {
    return listOf(
        AchievementUi("1", "初出茅庐", "完成第一个任务", "TASK", "COMMON", true),
        AchievementUi("2", "任务新手", "完成10个任务", "TASK", "COMMON", true),
        AchievementUi("3", "任务达人", "完成50个任务", "TASK", "RARE", false, 0.6f),
        AchievementUi("4", "任务大师", "完成100个任务", "TASK", "EPIC", false, 0.3f),
        AchievementUi("5", "坚持一周", "连续学习7天", "STREAK", "COMMON", true),
        AchievementUi("6", "月度坚持", "连续学习30天", "STREAK", "RARE", false, 0.23f),
        AchievementUi("7", "百日学霸", "连续学习100天", "STREAK", "LEGENDARY", false, 0.07f),
        AchievementUi("8", "函数大师", "完成10个微积分任务", "LEARNING", "RARE", true),
        AchievementUi("9", "代码新星", "完成首个编程挑战", "LEARNING", "COMMON", true),
        AchievementUi("10", "算法专家", "通过所有算法挑战", "LEARNING", "EPIC", false, 0.4f),
        AchievementUi("11", "早起鸟儿", "连续7天早上8点前登录", "SPECIAL", "RARE", false),
        AchievementUi("12", "夜猫子", "连续7天晚上10点后学习", "SPECIAL", "RARE", true)
    )
}
