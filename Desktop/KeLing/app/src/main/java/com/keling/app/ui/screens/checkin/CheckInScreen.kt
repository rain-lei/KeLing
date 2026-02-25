package com.keling.app.ui.screens.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.ui.theme.*

/**
 * 每日签到页：当天成功登录后若未签到则先进入此页，签到后进入首页。
 * 若今日已签到则自动跳转首页。
 */
@Composable
fun CheckInScreen(
    viewModel: CheckInViewModel = hiltViewModel(),
    onDone: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.alreadyCheckedIn) {
        if (uiState.alreadyCheckedIn) onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(KelingSpacing.horizontalPage),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "每日签到",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "坚持签到，记录你的学习足迹",
                style = MaterialTheme.typography.bodyMedium,
                color = InkSecondary
            )
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaperSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = NeonBlue
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (uiState.currentStreak > 0 && !uiState.alreadyCheckedIn) {
                        Text(
                            text = "已连续学习 ${uiState.currentStreak} 天",
                            style = MaterialTheme.typography.titleMedium,
                            color = InkSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = "点击下方按钮完成今日签到",
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.performCheckIn(onDone) },
                        enabled = !uiState.isCheckingIn && uiState.userId != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                    ) {
                        if (uiState.isCheckingIn) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("签到", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            uiState.error?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
