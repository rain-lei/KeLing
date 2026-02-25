package com.keling.app.ui.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.keling.app.ui.components.*
import com.keling.app.ui.theme.*
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("UNUSED_PARAMETER")
fun CourseDetailScreen(
    courseId: String,  // Nav 路由参数，ViewModel 从 savedStateHandle 获取
    onBack: () -> Unit,
    onNavigateToKnowledgeGraph: () -> Unit
) {
    val viewModel: CourseDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PaperBackground,
        topBar = {
            TopAppBar(
                title = { },
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
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "更多",
                            tint = InkSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaperBackground
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PaperBackground)
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NeonBlue)
                    }
                }

                uiState.course != null -> {
                    val course = uiState.course!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 课程头部信息
                        item {
                            CourseHeader(course = course)
                        }

                        // 学习进度
                        item {
                            NeonCard(glowColor = NeonBlue) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "学习进度",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = InkSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${(course.progress * 100).toInt()}%",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonBlue
                                        )
                                    }

                                    CircularNeonProgress(
                                        progress = course.progress,
                                        size = 80.dp,
                                        strokeWidth = 8.dp,
                                        showPercentage = false
                                    )
                                }
                            }
                        }

                        // 操作按钮
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                NeonButton(
                                    text = "继续学习",
                                    onClick = { },
                                    color = NeonGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                NeonOutlinedButton(
                                    text = "知识图谱",
                                onClick = onNavigateToKnowledgeGraph,
                                    color = NeonPurple,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // AI 生成的学习任务列表（复用章节展示）
                        item {
                            Text(
                                text = "AI 学习任务",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary
                            )
                        }

                        items(course.chapters) { chapter ->
                            ChapterItem(
                                chapter = chapter,
                                onAddToToday = {
                                    scope.launch {
                                        viewModel.addChapterTaskToToday(chapter)
                                        snackbarHostState.showSnackbar(
                                            "已加入今日任务与任务中心",
                                            withDismissAction = true
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "未找到课程详情",
                            color = InkSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseHeader(course: CourseDetailUi) {
    Column {
        // 课程封面渐变
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(NeonBlue, NeonPurple)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = course.name.first().toString(),
                style = MaterialTheme.typography.displayLarge,
                color = PaperBackground.copy(alpha = 0.3f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = course.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = InkPrimary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoChip(
                icon = Icons.Default.Person,
                text = course.teacherName
            )
            InfoChip(
                icon = Icons.Default.Star,
                text = "${course.credits}学分"
            )
            InfoChip(
                icon = Icons.Default.Code,
                text = course.code
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = course.description,
            style = MaterialTheme.typography.bodyMedium,
            color = InkSecondary
        )
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = InkMuted,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = InkSecondary
        )
    }
}

@Composable
private fun ChapterItem(
    chapter: ChapterUi,
    onAddToToday: () -> Unit
) {
    NeonCard(
        glowColor = if (chapter.isCompleted) NeonGreen else 
                    if (chapter.progress > 0) NeonOrange else PaperBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 完成状态图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (chapter.isCompleted) NeonGreen.copy(alpha = 0.2f)
                               else PaperSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (chapter.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "${(chapter.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (chapter.progress > 0) NeonOrange else InkMuted
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkPrimary
                )
                if (!chapter.isCompleted && chapter.progress > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    NeonProgressBar(
                        progress = chapter.progress,
                        color = NeonOrange,
                        height = 4.dp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = InkMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                NeonOutlinedButton(
                    text = "加入今日任务",
                    onClick = onAddToToday,
                    color = NeonBlue
                )
            }
        }
    }
}

// UI数据类
data class CourseDetailUi(
    val id: String,
    val name: String,
    val code: String,
    val teacherName: String,
    val credits: Float,
    val progress: Float,
    val description: String,
    val chapters: List<ChapterUi>
)

data class ChapterUi(
    val id: String,
    val title: String,
    val progress: Float,
    val isCompleted: Boolean
)
