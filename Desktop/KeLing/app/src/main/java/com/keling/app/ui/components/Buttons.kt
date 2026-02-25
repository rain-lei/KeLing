package com.keling.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.keling.app.ui.theme.*

/**
 * 主按钮 - 古风朱砂/金渐变，强按压反馈
 */
@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NeonBlue,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )
    
    val borderAlpha by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0.7f,
        animationSpec = tween(durationMillis = 150),
        label = "borderGlow"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = if (enabled) {
                    Brush.horizontalGradient(
                        colors = listOf(color, NeonPurple.copy(alpha = 0.9f))
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(InkMuted, InkMuted)
                    )
                }
            )
            .border(
                width = if (isPressed) 2.dp else 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        NeonGold.copy(alpha = borderAlpha * 0.6f),
                        color.copy(alpha = borderAlpha)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = Color.White),
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}

/**
 * 轮廓按钮 - 次要操作，金边高亮
 */
@Composable
fun NeonOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NeonBlue,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color.Transparent)
            .border(
                width = 2.dp,
                color = if (enabled) color else InkMuted,
                shape = RoundedCornerShape(26.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(color = color),
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) color else InkMuted
        )
    }
}

/**
 * 图标按钮 - 古风金边，强按压缩放
 */
@Composable
fun NeonIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NeonBlue,
    size: Int = 48,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconScale"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .size(size.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true, color = color),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size((size * 0.5).dp)
        )
    }
}

/**
 * 浮动操作按钮 - AI助手入口，古风金朱呼吸光
 */
@Composable
fun AIFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    Box(
        modifier = modifier
            .size(64.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonGold.copy(alpha = glowAlpha),
                            NeonBlue.copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        FloatingActionButton(
            onClick = onClick,
            containerColor = PaperSurface,
            contentColor = NeonGold,
            shape = CircleShape,
            modifier = Modifier
                .size(56.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(NeonBlue, NeonGold, NeonPurple)
                    ),
                    shape = CircleShape
                )
        ) {
            Text(
                text = "灵",
                style = MaterialTheme.typography.titleLarge,
                color = NeonGold
            )
        }
    }
}

/**
 * 选项卡 - 选中金底朱砂字，切换动画
 */
@Composable
fun NeonTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NeonBlue
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(PaperSurface, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedIndex
            
            val tabScale by animateFloatAsState(
                targetValue = if (isSelected) 1.02f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "tabScale"
            )
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .scale(tabScale)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) NeonPurple.copy(alpha = 0.25f) else Color.Transparent
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 0.dp,
                        color = if (isSelected) NeonGold.copy(alpha = 0.8f) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) color else InkSecondary
                )
            }
        }
    }
}
