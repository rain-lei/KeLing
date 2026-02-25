package com.keling.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.model.Course
import com.keling.app.data.model.Task
import com.keling.app.data.model.TaskDifficulty
import com.keling.app.data.model.TaskStatus
import com.keling.app.data.model.TaskType
import com.keling.app.data.model.TaskActionType
import com.keling.app.data.remote.ChatRequest
import com.keling.app.data.remote.KelingApiService
import com.keling.app.data.repository.CheckInRepository
import com.keling.app.data.repository.CourseRepository
import com.keling.app.data.repository.TaskRepository
import com.keling.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Calendar

data class HomeUiState(
    val userName: String = "学习者",
    val level: Int = 1,
    val experience: Int = 0,
    val maxExperience: Int = 1000,
    val streak: Int = 0,
    val todayTaskCount: Int = 0,
    val completedTaskCount: Int = 0,
    val todayStudyMinutes: Int = 0,
    val todayTasks: List<Task> = emptyList(),
    val recentCourses: List<Course> = emptyList(),
    val skillGrowth: Map<String, Float> = emptyMap(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val courseRepository: CourseRepository,
    private val checkInRepository: CheckInRepository,
    private val kelingApiService: KelingApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val user = userRepository.getCurrentUser().first()
                val grade = user?.grade ?: "2024"
                val allCourses = courseRepository.getAllCourses().first()

                // 课程名称用于 AI 生成技能成长建议（仅用于雷达图，不再用 AI 任务覆盖本地任务）
                val courseNames = if (allCourses.isNotEmpty()) {
                    allCourses.joinToString("、") { it.name }
                } else {
                    "当前还没有添加课程"
                }

                val prompt = buildString {
                    appendLine("你是一个面向大学生的学习规划 AI 助手。")
                    appendLine("下面是这位学生当前的课程列表：$courseNames。")
                    appendLine("请你基于常见的大学学习节奏，给出一段不超过 100 字的总体鼓励描述，并给出各能力方向的成长水平（0.0~1.0），用于绘制雷达图。")
                    appendLine("请按如下纯文本格式返回：")
                    appendLine("第一部分：先给出一段不超过 100 字的总体鼓励性描述；")
                    appendLine("第二部分：最后单独一行给出技能成长，格式类似：技能成长: 数学=0.7, 编程=0.8, 英语=0.6, 物理=0.5, 逻辑=0.7")
                }

                val response = try {
                    kelingApiService.sendChatMessage(
                        token = "",
                        request = ChatRequest(
                            message = prompt,
                            context = "home_dashboard"
                        )
                    )
                } catch (_: Exception) {
                    null
                }

                val reply = response?.body()?.reply?.trim().orEmpty()
                val (_, skillGrowthFromAi) = parseHomeAiReply(reply)
                val skillGrowth = if (skillGrowthFromAi.isNotEmpty()) skillGrowthFromAi else defaultSkillGrowth()

                // 确保当日预置日常任务已生成
                taskRepository.ensureDailyTasksForToday(grade)

                // 组合任务流、学习时长流与签到连续天数流（与登录用户绑定）
                val tasksFlow = taskRepository.getTasksForGrade(grade)
                val minutesFlow = taskRepository.getTodayStudyMinutes()
                val streakFlow = user?.id?.let { checkInRepository.getStreak(it) } ?: flowOf(0)

                tasksFlow
                    .combine(minutesFlow) { t, m -> Pair(t, m) }
                    .combine(streakFlow) { (tasks, todayMinutes), streak ->
                        Triple(tasks, todayMinutes, streak)
                    }
                    .collect { (allTasks, todayMinutes, streak) ->
                        val (startOfDay, endOfDay) = todayBounds()
                        // 今日任务：当日生成的日常任务 + 所有未完成的挑战任务
                        val todayTasks = allTasks.filter { task ->
                            when (task.type) {
                                TaskType.DAILY -> task.status != TaskStatus.COMPLETED &&
                                        task.createdAt in startOfDay..endOfDay
                                TaskType.CHALLENGE -> task.status != TaskStatus.COMPLETED
                                else -> false
                            }
                        }
                        val completedToday = todayTasks.count { it.status == TaskStatus.COMPLETED }

                        _uiState.update {
                            it.copy(
                                userName = user?.realName ?: "同学",
                                level = 1,
                                experience = 0,
                                maxExperience = 1000,
                                streak = streak,
                                todayTaskCount = todayTasks.size,
                                completedTaskCount = completedToday,
                                todayStudyMinutes = todayMinutes,
                                todayTasks = todayTasks,
                                recentCourses = allCourses.take(3),
                                skillGrowth = skillGrowth,
                                isLoading = false
                            )
                        }
                    }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * 解析 AI 返回的首页文案，提取任务和技能成长。
     * 这里生成的任务统一设计为「阅读类任务」，具备明确的完成标准：阅读不少于指定分钟数。
     */
    private fun parseHomeAiReply(
        reply: String
    ): Pair<List<Task>, Map<String, Float>> {
        if (reply.isBlank()) return emptyList<Task>() to defaultSkillGrowth()

        val lines = reply.lines().map { it.trim() }.filter { it.isNotBlank() }
        val taskLines = lines.filter { it.firstOrNull()?.isDigit() == true && it.contains('.') }
        val tasks = taskLines.mapIndexed { index, line ->
            val content = line.substringAfter('.').trim().ifBlank { line }
            val title = content.take(20)
            val minutes = 25
            Task(
                id = "ai_home_task_${System.currentTimeMillis()}_$index",
                title = title,
                description = content,
                type = TaskType.DAILY,
                difficulty = TaskDifficulty.MEDIUM,
                status = TaskStatus.PENDING,
                experienceReward = 50,
                coinReward = 25,
                estimatedMinutes = minutes,
                actionType = TaskActionType.READING.name,
                actionPayload = """{"durationMinutes":$minutes,"bookOrChapter":"$title"}"""
            )
        }

        val skillLine = lines.lastOrNull { it.contains("技能成长") || it.contains("=") }
        val skills = mutableMapOf<String, Float>()
        if (skillLine != null) {
            val parts = skillLine.substringAfter(':', missingDelimiterValue = skillLine)
            parts.split(',').forEach { part ->
                val kv = part.split('=')
                if (kv.size == 2) {
                    val key = kv[0].trim()
                    val value = kv[1].trim().toFloatOrNull()
                    if (value != null) {
                        skills[key] = value.coerceIn(0f, 1f)
                    }
                }
            }
        }

        val finalSkills = if (skills.isNotEmpty()) skills else defaultSkillGrowth()
        return tasks to finalSkills
    }

    private fun defaultSkillGrowth(): Map<String, Float> = mapOf(
        "数学" to 0.6f,
        "编程" to 0.7f,
        "英语" to 0.5f,
        "物理" to 0.4f,
        "逻辑" to 0.65f
    )

    private fun todayBounds(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        val end = start + 24L * 60L * 60L * 1000L - 1L
        return start to end
    }

    private fun createFallbackTasks(courses: List<Course>): List<Task> {
        val courseName = courses.firstOrNull()?.name ?: "当前课程"
        val minutesEasy = 20
        val minutesMedium = 40
        return listOf(
            Task(
                id = "fallback_1",
                title = "预习课程内容",
                description = "预习「$courseName」本周将要学习的章节，标记不懂的知识点。",
                type = TaskType.REVIEW,
                difficulty = TaskDifficulty.EASY,
                status = TaskStatus.PENDING,
                experienceReward = 20,
                coinReward = 10,
                estimatedMinutes = minutesEasy,
                actionType = TaskActionType.READING.name,
                actionPayload = """{"durationMinutes":$minutesEasy,"bookOrChapter":"$courseName 预习"}"""
            ),
            Task(
                id = "fallback_2",
                title = "完成课后习题",
                description = "选择一门当前压力最大的课程，完成对应章节的课后习题。",
                type = TaskType.PRACTICE,
                difficulty = TaskDifficulty.MEDIUM,
                status = TaskStatus.PENDING,
                experienceReward = 50,
                coinReward = 25,
                estimatedMinutes = minutesMedium,
                actionType = TaskActionType.READING.name,
                actionPayload = """{"durationMinutes":$minutesMedium,"bookOrChapter":"$courseName 课后习题讲解"}"""
            )
        )
    }
}
