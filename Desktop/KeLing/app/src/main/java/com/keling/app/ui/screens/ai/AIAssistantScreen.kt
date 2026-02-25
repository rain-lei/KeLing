package com.keling.app.ui.screens.ai

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.keling.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    onBack: () -> Unit,
    viewModel: AIAssistantViewModel = hiltViewModel()
) {
    var inputText by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val messages = uiState.messages
    val isTyping = uiState.isTyping
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
    ) {
        // 顶部栏
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // AI头像
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(NeonBlue, NeonPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "灵",
                            style = MaterialTheme.typography.labelLarge,
                            color = PaperBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "课灵助手",
                            style = MaterialTheme.typography.titleMedium,
                            color = InkPrimary
                        )
                        Text(
                            text = if (isTyping) "正在输入..." else "在线",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isTyping) NeonBlue else NeonGreen
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = InkPrimary
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* 更多选项 */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = InkSecondary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PaperSurface
            )
        )
        
        // 消息列表
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }
            
            if (isTyping) {
                item {
                    TypingIndicator()
                }
            }
        }
        
        // 快捷功能
        QuickActions(
            onActionClick = { action ->
                coroutineScope.launch {
                    viewModel.sendMessage(action)
                    listState.animateScrollToItem(uiState.messages.size.coerceAtLeast(0))
                }
            }
        )
        
        // 输入区域
        ChatInput(
            value = inputText,
            onValueChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    inputText = ""
                    coroutineScope.launch {
                        viewModel.sendMessage(inputText)
                        listState.animateScrollToItem(uiState.messages.size.coerceAtLeast(0))
                    }
                }
            },
            onVoice = { /* 语音输入 */ }
        )
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
            // AI头像
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(NeonBlue, NeonPurple)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "灵",
                    style = MaterialTheme.typography.labelSmall,
                    color = PaperBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (message.isFromUser) NeonBlue.copy(alpha = 0.2f) else PaperSurface,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                    )
                )
                .border(
                    width = 1.dp,
                    color = if (message.isFromUser) NeonBlue.copy(alpha = 0.3f) else PaperBorder,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = InkPrimary
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    
    Row(
        modifier = Modifier.padding(start = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 200)
                ),
                label = "dot$index"
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = NeonBlue.copy(alpha = alpha),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun QuickActions(onActionClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionChip(
            text = "今日任务",
            onClick = { onActionClick("帮我看看今天有什么任务") }
        )
        QuickActionChip(
            text = "学习建议",
            onClick = { onActionClick("给我一些学习建议") }
        )
        QuickActionChip(
            text = "知识点解答",
            onClick = { onActionClick("我想问一个知识点问题") }
        )
    }
}

@Composable
private fun QuickActionChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = PaperSurface,
        border = BorderStroke(1.dp, PaperBorder)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = InkSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoice: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PaperSurface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 语音按钮
        IconButton(
            onClick = onVoice,
            modifier = Modifier
                .size(44.dp)
                .background(PaperSurface, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "语音",
                tint = NeonPurple
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 输入框
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    "有什么可以帮你的？",
                    color = InkMuted
                )
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonBlue,
                unfocusedBorderColor = PaperBorder,
                focusedContainerColor = PaperSurface,
                unfocusedContainerColor = PaperSurface,
                focusedTextColor = InkPrimary,
                unfocusedTextColor = InkPrimary,
                cursorColor = NeonBlue
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 发送按钮
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .size(44.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(NeonBlue, NeonPurple)
                    ),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "发送",
                tint = Color.White
            )
        }
    }
}
