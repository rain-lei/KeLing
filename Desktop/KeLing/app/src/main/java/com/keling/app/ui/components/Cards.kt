package com.keling.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.keling.app.ui.theme.*

/**
 * 古风卡片 - 金/朱砂边框光晕
 */
@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    glowColor: Color = NeonBlue,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = NeonPurple.copy(alpha = 0.2f),
                spotColor = glowColor.copy(alpha = 0.35f)
            )
            .clip(shape)
            .background(PaperSurface)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        NeonGold.copy(alpha = 0.5f),
                        glowColor.copy(alpha = 0.6f),
                        glowColor.copy(alpha = 0.3f)
                    )
                ),
                shape = shape
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * 渐变卡片 - 朱砂到金
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(NeonBlue, NeonPurple),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors.map { it.copy(alpha = 0.15f) }
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(colors = gradientColors),
                shape = shape
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * 任务卡片
 */
@Composable
fun TaskCard(
    title: String,
    description: String,
    progress: Float,
    difficulty: String,
    expReward: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val difficultyColor = when (difficulty) {
        "EASY" -> DifficultyEasy
        "MEDIUM" -> DifficultyMedium
        "HARD" -> DifficultyHard
        else -> DifficultyExpert
    }

    NeonCard(
        modifier = modifier.fillMaxWidth(),
        glowColor = difficultyColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
            }
            
            // 经验值奖励
            Box(
                modifier = Modifier
                    .background(
                        color = NeonGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+$expReward XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 进度条
        NeonProgressBar(
            progress = progress,
            color = difficultyColor,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 难度标签
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DifficultyBadge(difficulty = difficulty)
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = InkSecondary
            )
        }
    }
}

/**
 * 课程卡片
 */
@Composable
fun CourseCard(
    name: String,
    teacherName: String,
    progress: Float,
    credits: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    NeonCard(
        modifier = modifier.fillMaxWidth(),
        glowColor = NeonPurple,
        onClick = onClick
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = InkPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "授课教师：$teacherName",
            style = MaterialTheme.typography.bodySmall,
            color = InkSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "学分：$credits",
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeonProgressBar(
                progress = progress,
                color = NeonPurple,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = NeonPurple
            )
        }
    }
}

/**
 * 难度标签
 */
@Composable
fun DifficultyBadge(difficulty: String) {
    val (color, text) = when (difficulty) {
        "EASY" -> DifficultyEasy to "简单"
        "MEDIUM" -> DifficultyMedium to "中等"
        "HARD" -> DifficultyHard to "困难"
        else -> DifficultyExpert to "专家"
    }
    
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
