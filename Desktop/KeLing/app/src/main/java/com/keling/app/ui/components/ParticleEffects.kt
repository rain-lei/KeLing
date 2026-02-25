package com.keling.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.keling.app.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

/**
 * 粒子类型
 */
enum class ParticleType {
    NORMAL,     // 普通任务
    ACHIEVEMENT,// 成就解锁
    CELEBRATION,// 庆祝效果
    SYSTEM      // 系统提示
}

/**
 * 粒子数据
 */
data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var radius: Float,
    var alpha: Float,
    var color: Color,
    var life: Float = 1f,
    var decay: Float = 0.02f
)

/**
 * 粒子效果配置
 */
data class ParticleConfig(
    val particleCount: Int = 50,
    val colors: List<Color> = listOf(NeonBlue, NeonPurple, NeonPink),
    val minRadius: Float = 2f,
    val maxRadius: Float = 6f,
    val minSpeed: Float = 1f,
    val maxSpeed: Float = 4f,
    val gravity: Float = 0.05f,
    val decay: Float = 0.015f
)

/**
 * 粒子系统 - 任务完成效果
 */
@Composable
fun TaskCompleteParticles(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    config: ParticleConfig = ParticleConfig(
        particleCount = 50,
        colors = listOf(NeonGreen, NeonBlue, NeonGold)
    )
) {
    if (!isActive) return
    
    var particles by remember { mutableStateOf(emptyList<Particle>()) }
    var isInitialized by remember { mutableStateOf(false) }
    
    LaunchedEffect(isActive) {
        if (isActive && !isInitialized) {
            particles = createParticles(
                count = config.particleCount,
                centerX = 0.5f,
                centerY = 0.5f,
                colors = config.colors,
                config = config
            )
            isInitialized = true
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )
    
    // 更新粒子状态
    LaunchedEffect(time) {
        particles = particles.mapNotNull { particle ->
            particle.apply {
                x += vx
                y += vy
                vy += config.gravity
                life -= decay
                alpha = life.coerceIn(0f, 1f)
            }
            if (particle.life > 0) particle else null
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawParticle(particle, size.width, size.height)
        }
    }
}

/**
 * 成就解锁粒子效果
 */
@Composable
fun AchievementUnlockParticles(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isActive) return
    
    val config = ParticleConfig(
        particleCount = 200,
        colors = listOf(NeonGold, NeonOrange, NeonPink, NeonPurple),
        minRadius = 3f,
        maxRadius = 8f,
        minSpeed = 2f,
        maxSpeed = 8f,
        decay = 0.01f
    )
    
    var particles by remember { mutableStateOf(emptyList<Particle>()) }
    var burstComplete by remember { mutableStateOf(false) }
    
    LaunchedEffect(isActive) {
        if (isActive && !burstComplete) {
            // 创建爆发效果
            particles = createBurstParticles(
                count = config.particleCount,
                colors = config.colors,
                config = config
            )
            burstComplete = true
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "achievementParticles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(33, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "achievementTime"
    )
    
    LaunchedEffect(time) {
        particles = particles.mapNotNull { particle ->
            particle.apply {
                x += vx
                y += vy
                vy += 0.1f // 重力
                vx *= 0.99f // 空气阻力
                life -= decay
                alpha = (life * 1.5f).coerceIn(0f, 1f)
            }
            if (particle.life > 0) particle else null
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawGlowingParticle(particle, size.width, size.height)
        }
    }
}

/**
 * 星星闪烁效果
 */
@Composable
fun StarryBackground(
    modifier: Modifier = Modifier,
    starCount: Int = 30
) {
    val stars = remember {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3 + 1,
                twinkleSpeed = Random.nextFloat() * 2000 + 1000
            )
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        stars.forEachIndexed { index, star ->
            val alpha = (sin(System.currentTimeMillis() / star.twinkleSpeed + index) + 1) / 2
            drawCircle(
                color = NeonBlue.copy(alpha = alpha.toFloat() * 0.6f),
                radius = star.size,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}

/**
 * 流光效果
 */
@Composable
fun StreamingLightEffect(
    modifier: Modifier = Modifier,
    color: Color = NeonBlue
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streaming")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "streamOffset"
    )
    
    Canvas(modifier = modifier) {
        val streamCount = 5
        for (i in 0 until streamCount) {
            val baseOffset = (offset + i.toFloat() / streamCount) % 1f
            val x = baseOffset * size.width
            val alpha = sin(baseOffset * PI).toFloat() * 0.5f
            
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = 20.dp.toPx(),
                center = Offset(x, size.height / 2)
            )
        }
    }
}

// 辅助函数和数据类
private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val twinkleSpeed: Float
)

private fun createParticles(
    count: Int,
    centerX: Float,
    centerY: Float,
    colors: List<Color>,
    config: ParticleConfig
): List<Particle> {
    return List(count) {
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val speed = Random.nextFloat() * (config.maxSpeed - config.minSpeed) + config.minSpeed
        
        Particle(
            x = centerX,
            y = centerY,
            vx = cos(angle) * speed * 0.01f,
            vy = sin(angle) * speed * 0.01f - 0.02f,
            radius = Random.nextFloat() * (config.maxRadius - config.minRadius) + config.minRadius,
            alpha = 1f,
            color = colors.random(),
            decay = config.decay
        )
    }
}

private fun createBurstParticles(
    count: Int,
    colors: List<Color>,
    config: ParticleConfig
): List<Particle> {
    return List(count) {
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val speed = Random.nextFloat() * (config.maxSpeed - config.minSpeed) + config.minSpeed
        
        Particle(
            x = 0.5f,
            y = 0.5f,
            vx = cos(angle) * speed * 0.02f,
            vy = sin(angle) * speed * 0.02f - 0.03f,
            radius = Random.nextFloat() * (config.maxRadius - config.minRadius) + config.minRadius,
            alpha = 1f,
            color = colors.random(),
            life = Random.nextFloat() * 0.5f + 0.5f,
            decay = config.decay
        )
    }
}

private fun DrawScope.drawParticle(
    particle: Particle,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val x = particle.x * canvasWidth
    val y = particle.y * canvasHeight
    
    drawCircle(
        color = particle.color.copy(alpha = particle.alpha),
        radius = particle.radius,
        center = Offset(x, y)
    )
}

private fun DrawScope.drawGlowingParticle(
    particle: Particle,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val x = particle.x * canvasWidth
    val y = particle.y * canvasHeight
    
    // 外层光晕
    drawCircle(
        color = particle.color.copy(alpha = particle.alpha * 0.3f),
        radius = particle.radius * 2,
        center = Offset(x, y)
    )
    
    // 核心
    drawCircle(
        color = particle.color.copy(alpha = particle.alpha),
        radius = particle.radius,
        center = Offset(x, y)
    )
}
