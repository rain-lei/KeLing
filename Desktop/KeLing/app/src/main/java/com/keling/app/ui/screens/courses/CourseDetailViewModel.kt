package com.keling.app.ui.screens.courses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.remote.ChatRequest
import com.keling.app.data.model.Task
import com.keling.app.data.model.TaskDifficulty
import com.keling.app.data.model.TaskStatus
import com.keling.app.data.model.TaskType
import com.keling.app.data.repository.TaskRepository
import com.keling.app.data.remote.KelingApiService
import com.keling.app.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseDetailUiState(
    val isLoading: Boolean = true,
    val course: CourseDetailUi? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val kelingApiService: KelingApiService,
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: String = savedStateHandle["courseId"] ?: ""

    private val _uiState = MutableStateFlow(CourseDetailUiState())
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    init {
        loadCourseDetail()
    }

    private fun loadCourseDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // 先从本地课程表中查课程名称等基础信息
                val course = courseRepository.getCourseById(courseId)
                val courseName = course?.name ?: courseId
                val teacherName = course?.teacherName ?: "授课教师"
                val credits = course?.credits ?: 0f
                val progress = course?.progress ?: 0f

                // 构造发送给后端 AI 的提示词
                val prompt = buildString {
                    appendLine("你是一个教务与教学设计专家。")
                    appendLine("现在有一门课程，课程名称为：\"$courseName\"。")
                    appendLine("请你基于对该课程在大学中的典型教学内容和学习路径的了解，生成：")
                    appendLine("1. 一段 150~300 字的课程简介；")
                    appendLine("2. 3~5 个循序渐进的学习任务（每个任务一句话，包含动词和具体目标，例如“完成 XXX 章节习题”、“用代码实现 YYY 算法”等）。")
                    appendLine("只需给出简介和任务列表的纯文本即可，不要输出 JSON。")
                }

                // 调用课灵后端 AI 接口（如果后端未就绪，会走到 catch 或 isSuccessful=false 的分支）
                val response = try {
                    kelingApiService.sendChatMessage(
                        token = "",
                        request = ChatRequest(
                            message = prompt,
                            context = "course_detail"
                        )
                    )
                } catch (e: Exception) {
                    null
                }

                val aiReply = response?.body()?.reply?.trim().orEmpty()
                val aiTasks = response?.body()?.suggestions ?: emptyList()

                // 解析 AI 返回的任务，如果 suggestions 为空，就从 reply 中简单按行拆分后几行作为任务
                val tasks: List<ChapterUi> = if (aiTasks.isNotEmpty()) {
                    aiTasks.mapIndexed { index, text ->
                        ChapterUi(
                            id = "ai_task_${index + 1}",
                            title = text.trim(),
                            progress = 0f,
                            isCompleted = false
                        )
                    }
                } else {
                    // 简单从文本中拆出 3~5 行任务（如果存在）
                    val lines = aiReply.lines()
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .filter { it.any { ch -> ch.isDigit() || ch == '·' || ch == '-' } || it.length in 10..60 }
                        .take(5)

                    lines.mapIndexed { index, text ->
                        ChapterUi(
                            id = "ai_task_line_${index + 1}",
                            title = text,
                            progress = 0f,
                            isCompleted = false
                        )
                    }
                }

                val description = if (aiReply.isNotBlank()) {
                    aiReply
                } else {
                    // 后端 AI 未返回内容时的本地兜底简介
                    "本课程「$courseName」主要围绕本学科的基础概念、核心理论与典型应用展开，" +
                        "通过课堂讲授、例题分析与实践练习，帮助学生夯实理论基础、提升解决实际问题的能力。"
                }

                val detail = CourseDetailUi(
                    id = courseId,
                    name = courseName,
                    code = course?.code ?: "",
                    teacherName = teacherName,
                    credits = credits,
                    progress = progress,
                    description = description,
                    chapters = if (tasks.isNotEmpty()) tasks else defaultTasksForCourse(courseName)
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        course = detail,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "加载课程详情失败，请稍后重试。"
                    )
                }
            }
        }
    }

    /**
     * 将 AI 任务（章节）转化为一条挑战任务，写入任务中心与首页
     */
    fun addChapterTaskToToday(chapter: ChapterUi) {
        val course = _uiState.value.course ?: return
        viewModelScope.launch {
            val task = Task(
                id = "course_${course.id}_${chapter.id}_${System.currentTimeMillis()}",
                title = chapter.title.take(30),
                description = chapter.title,
                type = TaskType.CHALLENGE,
                difficulty = TaskDifficulty.MEDIUM,
                status = com.keling.app.data.model.TaskStatus.PENDING,
                courseId = course.id,
                chapterId = chapter.id,
                experienceReward = 60,
                coinReward = 30,
                estimatedMinutes = 40
            )
            taskRepository.saveTasks(listOf(task))
        }
    }

    /**
     * 当 AI 无可用结果时的本地兜底任务列表
     */
    private fun defaultTasksForCourse(courseName: String): List<ChapterUi> {
        return listOf(
            ChapterUi(
                id = "local_1",
                title = "了解课程「$courseName」的教学大纲与考核方式",
                progress = 0f,
                isCompleted = false
            ),
            ChapterUi(
                id = "local_2",
                title = "完成本课程第一章节的预习与配套习题",
                progress = 0f,
                isCompleted = false
            ),
            ChapterUi(
                id = "local_3",
                title = "整理一份本课程关键概念与公式/定理的知识清单",
                progress = 0f,
                isCompleted = false
            )
        )
    }
}


