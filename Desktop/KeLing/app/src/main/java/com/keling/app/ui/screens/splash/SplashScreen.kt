package com.keling.app.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.uiState.collectAsState()
    var startAnimation by remember { mutableStateOf(false) }
    
    // 入场缩放 - 修仙式浮现
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.6f,
        animationSpec = tween(
            durationMillis = 1100,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )
    
    // 古风金朱光晕呼吸
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
    }
    LaunchedEffect(splashState.isLoggedIn) {
        when (splashState.isLoggedIn) {
            true -> onNavigateToHome()
            false -> onNavigateToLogin()
            null -> { }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground),
        contentAlignment = Alignment.Center
    ) {
        // 古风金朱光效
        Box(
            modifier = Modifier
                .size(300.dp)
                .alpha(glowAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonGold.copy(alpha = 0.25f),
                            NeonBlue.copy(alpha = 0.2f),
                            PaperBackground
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .scale(scale)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo - 朱砂到金
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(NeonBlue, NeonPurple, NeonGold)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "课灵",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 应用名称
            Text(
                text = "课灵",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 标语
            Text(
                text = "游戏化学习，让知识更有趣",
                style = MaterialTheme.typography.bodyLarge,
                color = InkSecondary
            )
        }
        
        // 底部版本信息
        Text(
            text = "v1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = InkMuted,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(alpha)
        )
    }
}
