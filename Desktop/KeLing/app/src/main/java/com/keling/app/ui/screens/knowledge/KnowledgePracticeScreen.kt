package com.keling.app.ui.screens.knowledge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.data.model.LearningRecord
import com.keling.app.ui.components.NeonButton
import com.keling.app.ui.components.NeonCard
import com.keling.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgePracticeScreen(
    onBack: () -> Unit,
    viewModel: KnowledgePracticeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.point?.name?.let { "$it · 刷题练习" } ?: "刷题练习",
                        color = InkPrimary
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaperBackground
                )
            )
        },
        containerColor = PaperBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PaperBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NeonBlue)
                }
            } else if (uiState.error != null) {
                Text(uiState.error ?: "加载失败", color = NeonRed)
            } else {
                // 进度与统计
                NeonCard(glowColor = NeonBlue) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "练习进度",
                            style = MaterialTheme.typography.titleSmall,
                            color = InkSecondary
                        )
                        Text(
                            text = "${uiState.correctCount} 正确 · ${uiState.wrongCount} 错误",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (!uiState.isFinished && uiState.currentIndex < uiState.questions.size) {
                    val q = uiState.questions[uiState.currentIndex]
                    PracticeQuestionCard(
                        index = uiState.currentIndex,
                        total = uiState.questions.size,
                        question = q,
                        onAnswer = { idx -> viewModel.answerQuestion(idx) }
                    )
                } else {
                    NeonCard(glowColor = NeonGreen) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "本轮练习完成",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                            Text(
                                text = "正确 ${uiState.correctCount} 题，错误 ${uiState.wrongCount} 题。",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary
                            )
                        }
                    }
                }

                // 错题本
                if (uiState.wrongRecords.isNotEmpty()) {
                    Text(
                        text = "错题本",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(uiState.wrongRecords) { index, rec ->
                            WrongRecordItem(index + 1, rec)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeQuestionCard(
    index: Int,
    total: Int,
    question: PracticeQuestion,
    onAnswer: (Int) -> Unit
) {
    NeonCard(glowColor = NeonPurple) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "第 ${index + 1} / $total 题",
                style = MaterialTheme.typography.labelSmall,
                color = InkSecondary
            )
            Text(
                text = question.text,
                style = MaterialTheme.typography.bodyMedium,
                color = InkPrimary
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.options.forEachIndexed { i, opt ->
                    NeonButton(
                        text = opt,
                        onClick = { onAnswer(i) },
                        modifier = Modifier.fillMaxWidth(),
                        color = if (i == question.correctIndex) NeonGreen else NeonBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun WrongRecordItem(index: Int, record: LearningRecord) {
    NeonCard(glowColor = NeonRed) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "错题 $index",
                style = MaterialTheme.typography.labelSmall,
                color = NeonRed
            )
            Text(
                text = record.questionId ?: "（题目内容已记录）",
                style = MaterialTheme.typography.bodySmall,
                color = InkPrimary
            )
        }
    }
}

