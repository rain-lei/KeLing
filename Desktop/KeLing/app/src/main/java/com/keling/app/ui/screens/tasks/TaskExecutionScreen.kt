package com.keling.app.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.data.model.*
import com.keling.app.ui.components.*
import com.keling.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskExecutionScreen(
    taskId: String,
    onBack: () -> Unit,
    onCompleted: () -> Unit = {},
    viewModel: TaskExecutionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(taskId) { viewModel.loadTask(taskId) }

    LaunchedEffect(uiState.completionResult) {
        val r = uiState.completionResult
        if (r is TaskCompletionResult.Success && (r.score == null || r.score >= 1f || r.feedback.contains("通过"))) {
            viewModel.clearCompletionResult()
            onCompleted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("执行任务", color = InkPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = InkPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        containerColor = PaperBackground
    ) { padding ->
        if (uiState.isLoading && uiState.task == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonBlue)
            }
            return@Scaffold
        }
        if (uiState.error != null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.error!!, color = NeonRed)
                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = onBack) { Text("返回", color = NeonBlue) }
                }
            }
            return@Scaffold
        }
        val task = uiState.task ?: return@Scaffold
        val actionType = task.actionType?.let { runCatching { TaskActionType.valueOf(it) }.getOrNull() }

        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            when (actionType) {
                TaskActionType.QUIZ -> QuizExecution(
                    task = task,
                    payload = uiState.quizPayload,
                    completionResult = uiState.completionResult,
                    onSubmit = viewModel::submitQuiz,
                    onDismissResult = viewModel::clearCompletionResult
                )
                TaskActionType.READING -> ReadingExecution(
                    task = task,
                    payload = uiState.readingPayload,
                    elapsedSeconds = uiState.readingElapsedSeconds,
                    onElapsedChange = viewModel::setReadingElapsedSeconds,
                    onConfirm = viewModel::submitReading
                )
                TaskActionType.VIDEO -> VideoExecution(
                    task = task,
                    payload = uiState.videoPayload,
                    elapsedSeconds = uiState.videoElapsedSeconds,
                    onElapsedChange = viewModel::setVideoElapsedSeconds,
                    onConfirm = viewModel::submitVideo
                )
                TaskActionType.EXERCISE -> ExerciseExecution(
                    task = task,
                    payload = uiState.exercisePayload,
                    checkedCount = uiState.exerciseCheckedCount,
                    completionResult = uiState.completionResult,
                    onCheckedChange = viewModel::setExerciseCheckedCount,
                    onSubmit = viewModel::submitExercise,
                    onDismissResult = viewModel::clearCompletionResult
                )
                TaskActionType.MEMORIZATION -> MemorizationExecution(
                    task = task,
                    payload = uiState.memorizationPayload,
                    onConfirm = viewModel::submitMemorization
                )
                null -> UnsupportedExecution(task = task, onBack = onBack)
            }
        }
    }
}

@Composable
private fun QuizExecution(
    task: Task,
    payload: QuizPayload?,
    completionResult: TaskCompletionResult?,
    onSubmit: (List<Int>) -> Unit,
    onDismissResult: () -> Unit
) {
    val questions = payload?.questions ?: emptyList()
    var currentIndex by remember { mutableStateOf(0) }
    var answers by remember { mutableStateOf<List<Int>>(emptyList()) }
    val answersPadded = answers + List((questions.size - answers.size).coerceAtLeast(0)) { -1 }

    LaunchedEffect(completionResult) {
        if (completionResult is TaskCompletionResult.Failure) {
            delay(2000)
            onDismissResult()
        }
    }

    if (questions.isEmpty()) {
        Text("无题目", color = InkSecondary)
        return
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            task.title,
            style = MaterialTheme.typography.titleMedium,
            color = InkPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text("第 ${currentIndex + 1} / ${questions.size} 题", style = MaterialTheme.typography.bodySmall, color = InkSecondary)
        Spacer(Modifier.height(16.dp))

        val q = questions[currentIndex]
        NeonCard(glowColor = NeonBlue) {
            Text(q.question, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = InkPrimary)
            Spacer(Modifier.height(12.dp))
            q.options.forEachIndexed { i, opt ->
                val selected = answersPadded.getOrNull(currentIndex) == i
                Surface(
                    onClick = {
                        val next = MutableList(questions.size) { idx ->
                            if (idx == currentIndex) i else answersPadded.getOrElse(idx) { -1 }
                        }
                        answers = next
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) NeonBlue.copy(alpha = 0.2f) else PaperSurfaceVariant
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = NeonBlue))
                        Spacer(Modifier.width(8.dp))
                        Text(opt, color = InkPrimary)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        completionResult?.let { r ->
            when (r) {
                is TaskCompletionResult.Success -> {
                    Text(r.feedback, color = NeonGreen)
                }
                is TaskCompletionResult.Failure -> {
                    Text(r.reason, color = NeonRed)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (currentIndex > 0) {
                OutlinedButton(onClick = { currentIndex-- }) { Text("上一题") }
            }
            if (currentIndex < questions.size - 1) {
                Button(
                    onClick = { currentIndex++ },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                ) { Text("下一题") }
            } else {
                Button(
                    onClick = { onSubmit(answersPadded.take(questions.size)) },
                    enabled = answersPadded.take(questions.size).all { it >= 0 },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) { Text("提交") }
            }
        }
    }
}

@Composable
private fun ReadingExecution(
    task: Task,
    payload: ReadingPayload?,
    elapsedSeconds: Int,
    onElapsedChange: (Int) -> Unit,
    onConfirm: () -> Unit
) {
    var elapsed by remember { mutableStateOf(elapsedSeconds) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsed += 1
            onElapsedChange(elapsed)
        }
    }
    val durationMinutes = payload?.durationMinutes ?: 10
    val targetSeconds = durationMinutes * 60
    val reached = elapsed >= targetSeconds

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = InkPrimary)
        Spacer(Modifier.height(8.dp))
        Text(task.description, style = MaterialTheme.typography.bodyMedium, color = InkSecondary)
        Spacer(Modifier.height(24.dp))
        NeonCard(glowColor = NeonPurple) {
            Text("阅读时长", style = MaterialTheme.typography.labelMedium, color = InkSecondary)
            Text("${elapsed / 60}:${"%02d".format(elapsed % 60)} / $durationMinutes:00", style = MaterialTheme.typography.headlineSmall, color = NeonPurple)
            if (payload?.pageRange != null) Text("建议范围：${payload.pageRange}", style = MaterialTheme.typography.bodySmall, color = InkMuted)
        }
        Spacer(Modifier.height(16.dp))
        NeonButton(
            text = if (reached) "确认完成阅读" else "请先阅读满 $durationMinutes 分钟",
            onClick = onConfirm,
            color = NeonGreen,
            enabled = reached,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun VideoExecution(
    task: Task,
    payload: VideoPayload?,
    elapsedSeconds: Int,
    onElapsedChange: (Int) -> Unit,
    onConfirm: () -> Unit
) {
    var elapsed by remember { mutableStateOf(elapsedSeconds) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsed += 1
            onElapsedChange(elapsed)
        }
    }
    val durationMinutes = payload?.durationMinutes ?: 10
    val targetSeconds = durationMinutes * 60
    val reached = elapsed >= targetSeconds

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = InkPrimary)
        Spacer(Modifier.height(8.dp))
        Text(task.description, style = MaterialTheme.typography.bodyMedium, color = InkSecondary)
        Spacer(Modifier.height(24.dp))
        NeonCard(glowColor = NeonBlue) {
            Text("观看时长", style = MaterialTheme.typography.labelMedium, color = InkSecondary)
            Text("${elapsed / 60}:${"%02d".format(elapsed % 60)} / $durationMinutes:00", style = MaterialTheme.typography.headlineSmall, color = NeonBlue)
        }
        Spacer(Modifier.height(16.dp))
        NeonButton(
            text = if (reached) "确认完成观看" else "请先观看满 $durationMinutes 分钟",
            onClick = onConfirm,
            color = NeonGreen,
            enabled = reached,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ExerciseExecution(
    task: Task,
    payload: ExercisePayload?,
    checkedCount: Int,
    completionResult: TaskCompletionResult?,
    onCheckedChange: (Int) -> Unit,
    onSubmit: (Int) -> Unit,
    onDismissResult: () -> Unit
) {
    val total = payload?.totalCount ?: 0
    LaunchedEffect(completionResult) {
        if (completionResult is TaskCompletionResult.Failure) {
            delay(2000)
            onDismissResult()
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = InkPrimary)
        Spacer(Modifier.height(8.dp))
        Text(task.description, style = MaterialTheme.typography.bodyMedium, color = InkSecondary)
        if (payload != null) {
            Spacer(Modifier.height(8.dp))
            Text("${payload.subject} · ${payload.chapter}，题号 ${payload.exerciseIds}", style = MaterialTheme.typography.bodySmall, color = InkMuted)
        }
        Spacer(Modifier.height(16.dp))
        NeonCard(glowColor = NeonOrange) {
            Text("已完成 $checkedCount / $total 题", style = MaterialTheme.typography.titleMedium, color = NeonOrange)
            Slider(
                value = checkedCount.toFloat().coerceIn(0f, total.toFloat()),
                onValueChange = { onCheckedChange(it.toInt()) },
                valueRange = 0f..total.toFloat(),
                steps = (total - 1).coerceAtLeast(0),
                colors = SliderDefaults.colors(thumbColor = NeonOrange, activeTrackColor = NeonOrange)
            )
        }
        completionResult?.let { r ->
            Spacer(Modifier.height(8.dp))
            when (r) {
                is TaskCompletionResult.Success -> Text(r.feedback, color = NeonGreen)
                is TaskCompletionResult.Failure -> Text(r.reason, color = NeonRed)
            }
        }
        Spacer(Modifier.height(16.dp))
        NeonButton(
            text = "提交进度",
            onClick = { onSubmit(checkedCount) },
            color = NeonGreen,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MemorizationExecution(
    task: Task,
    payload: MemorizationPayload?,
    onConfirm: () -> Unit
) {
    var confirmed by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = InkPrimary)
        Spacer(Modifier.height(8.dp))
        Text(task.description, style = MaterialTheme.typography.bodyMedium, color = InkSecondary)
        if (payload != null) {
            Spacer(Modifier.height(8.dp))
            Text("${payload.itemDescription} × ${payload.itemCount}", style = MaterialTheme.typography.bodySmall, color = InkMuted)
            payload.hint?.let { Text("提示：$it", style = MaterialTheme.typography.bodySmall, color = InkSecondary) }
        }
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = confirmed, onCheckedChange = { confirmed = it }, colors = CheckboxDefaults.colors(checkedColor = NeonGreen))
            Text("我已完成背诵并自检", color = InkPrimary)
        }
        Spacer(Modifier.height(16.dp))
        NeonButton(
            text = "确认完成",
            onClick = onConfirm,
            color = NeonGreen,
            enabled = confirmed,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun UnsupportedExecution(task: Task, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("该任务类型暂不支持在应用内执行", color = InkSecondary)
        Text(task.title, style = MaterialTheme.typography.titleSmall, color = InkPrimary)
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack) { Text("返回", color = NeonBlue) }
    }
}
