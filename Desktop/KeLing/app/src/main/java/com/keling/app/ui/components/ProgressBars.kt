package com.keling.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.keling.app.ui.theme.*

/**
 * 进度条 - 古风朱砂/金渐变，流动动画
 */
@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = NeonBlue,
    backgroundColor: Color = PaperBorder,
    height: Dp = 8.dp,
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (animated) tween(durationMillis = 700, easing = FastOutSlowInEasing)
        else snap(),
        label = "progress"
    )
    
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(height / 2))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.8f),
                            color,
                            color.copy(alpha = 0.9f)
                        )
                    )
                )
        )
    }
}

/**
 * 环形进度条 - 科幻风格
 */
@Composable
fun CircularNeonProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    color: Color = NeonBlue,
    backgroundColor: Color = PaperBorder,
    showPercentage: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "circularProgress"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val sweepAngle = animatedProgress * 360f
            val stroke = strokeWidth.toPx()
            val diameter = size.toPx() - stroke
            val topLeft = Offset(stroke / 2, stroke / 2)
            
            // 背景圆环
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            
            // 进度圆环
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        color.copy(alpha = 0.3f),
                        color,
                        color.copy(alpha = 0.8f)
                    )
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            
            // 发光端点
            if (animatedProgress > 0) {
                rotate(degrees = -90f + sweepAngle, pivot = center) {
                    drawCircle(
                        color = color,
                        radius = stroke / 2 + 4.dp.toPx(),
                        center = Offset(center.x, stroke / 2 + 4.dp.toPx()),
                        alpha = 0.5f
                    )
                }
            }
        }
        
        if (showPercentage) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )
        }
    }
}

/**
 * 经验值进度条
 */
@Composable
fun ExperienceBar(
    currentExp: Int,
    maxExp: Int,
    level: Int,
    modifier: Modifier = Modifier
) {
    val progress = currentExp.toFloat() / maxExp.toFloat()
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 等级徽章
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(NeonGold, NeonGold.copy(alpha = 0.3f))
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$level",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lv.$level",
                    style = MaterialTheme.typography.titleSmall,
                    color = NeonGold
                )
            }
            
            Text(
                text = "$currentExp / $maxExp XP",
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        NeonProgressBar(
            progress = progress,
            color = NeonGold,
            height = 10.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 雷达图/能力图
 */
@Composable
fun RadarChart(
    data: Map<String, Float>,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    color: Color = NeonBlue
) {
    val labels = data.keys.toList()
    val values = data.values.toList()
    val count = labels.size
    
    if (count < 3) return
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val centerX = size.toPx() / 2
            val centerY = size.toPx() / 2
            val radius = size.toPx() / 2 - 20.dp.toPx()
            val angleStep = 360f / count
            
            // 绘制背景网格
            for (level in 1..5) {
                val levelRadius = radius * level / 5
                val path = Path()
                for (i in 0 until count) {
                    val angle = Math.toRadians((angleStep * i - 90).toDouble())
                    val x = centerX + (levelRadius * Math.cos(angle)).toFloat()
                    val y = centerY + (levelRadius * Math.sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(
                    path = path,
                    color = PaperBorder,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            
            // 绘制轴线
            for (i in 0 until count) {
                val angle = Math.toRadians((angleStep * i - 90).toDouble())
                val endX = centerX + (radius * Math.cos(angle)).toFloat()
                val endY = centerY + (radius * Math.sin(angle)).toFloat()
                drawLine(
                    color = PaperBorder,
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx()
                )
            }
            
            // 绘制数据区域
            val dataPath = Path()
            for (i in 0 until count) {
                val value = values[i].coerceIn(0f, 1f)
                val angle = Math.toRadians((angleStep * i - 90).toDouble())
                val x = centerX + (radius * value * Math.cos(angle)).toFloat()
                val y = centerY + (radius * value * Math.sin(angle)).toFloat()
                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()
            
            // 填充区域
            drawPath(
                path = dataPath,
                color = color.copy(alpha = 0.3f)
            )
            
            // 边框
            drawPath(
                path = dataPath,
                color = color,
                style = Stroke(width = 2.dp.toPx())
            )
            
            // 数据点
            for (i in 0 until count) {
                val value = values[i].coerceIn(0f, 1f)
                val angle = Math.toRadians((angleStep * i - 90).toDouble())
                val x = centerX + (radius * value * Math.cos(angle)).toFloat()
                val y = centerY + (radius * value * Math.sin(angle)).toFloat()
                drawCircle(
                    color = color,
                    radius = 6.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}
