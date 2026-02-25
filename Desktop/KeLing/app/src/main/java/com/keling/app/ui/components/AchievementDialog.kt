package com.keling.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.keling.app.ui.theme.*

/**
 * 成就解锁对话框 - 带震动和动画效果
 */
@Composable
fun AchievementUnlockDialog(
    achievementName: String,
    achievementDescription: String,
    rarity: String,
    expReward: Int,
    onDismiss: () -> Unit
) {
    var showParticles by remember { mutableStateOf(true) }
    
    // 弹入动画
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dialogScale"
    )
    
    // 光环旋转与闪烁动画
    val infiniteTransition = rememberInfiniteTransition(label = "halo")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "haloRotation"
    )
    
    // 闪烁动画
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    val rarityColor = when (rarity) {
        "COMMON" -> TextSecondary
        "RARE" -> NeonBlue
        "EPIC" -> NeonPurple
        "LEGENDARY" -> NeonGold
        else -> TextSecondary
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 粒子效果背景
            AchievementUnlockParticles(
                isActive = showParticles,
                modifier = Modifier.fillMaxSize()
            )
            
            // 对话框内容
            Card(
                modifier = Modifier
                    .scale(scale)
                    .width(300.dp)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                rarityColor.copy(alpha = glowAlpha),
                                rarityColor.copy(alpha = glowAlpha * 0.5f),
                                rarityColor.copy(alpha = glowAlpha)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkCard
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 标题
                    Text(
                        text = "成就解锁！",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = rarityColor
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 成就图标
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // 旋转光环
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .rotate(rotation)
                                .alpha(glowAlpha * 0.5f)
                                .background(
                                    brush = Brush.sweepGradient(
                                        colors = listOf(
                                            rarityColor,
                                            Color.Transparent,
                                            rarityColor.copy(alpha = 0.5f),
                                            Color.Transparent,
                                            rarityColor
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                        
                        // 内圈
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            rarityColor.copy(alpha = 0.3f),
                                            rarityColor.copy(alpha = 0.1f)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .border(
                                    width = 3.dp,
                                    color = rarityColor,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = rarityColor,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 成就名称
                    Text(
                        text = achievementName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 描述
                    Text(
                        text = achievementDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 稀有度标签
                    RarityBadge(rarity = rarity)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 奖励
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = NeonGold.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = NeonGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+$expReward 经验值",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonGold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 确认按钮
                    NeonButton(
                        text = "太棒了！",
                        onClick = onDismiss,
                        color = rarityColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun RarityBadge(rarity: String) {
    val (color, text) = when (rarity) {
        "COMMON" -> TextSecondary to "普通"
        "RARE" -> NeonBlue to "稀有"
        "EPIC" -> NeonPurple to "史诗"
        "LEGENDARY" -> NeonGold to "传说"
        else -> TextSecondary to "普通"
    }
    
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * 任务完成庆祝效果
 */
@Composable
fun TaskCompleteCelebration(
    isVisible: Boolean,
    expGained: Int,
    onDismiss: () -> Unit
) {
    if (!isVisible) return
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "celebrationScale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // 粒子效果
        TaskCompleteParticles(
            isActive = isVisible,
            modifier = Modifier.fillMaxSize()
        )
        
        // 经验值获得提示
        Column(
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
                text = "任务完成！",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NeonGreen
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = NeonGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+$expGained XP",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeonGold
                )
            }
        }
    }
    
    // 自动消失
    LaunchedEffect(isVisible) {
        if (isVisible) {
            kotlinx.coroutines.delay(2500)
            onDismiss()
        }
    }
}
