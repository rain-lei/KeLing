package com.keling.app.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.model.TaskStatus
import com.keling.app.data.model.TaskType
import com.keling.app.data.remote.ChatRequest
import com.keling.app.data.remote.KelingApiService
import com.keling.app.data.repository.TaskRepository
import com.keling.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class AIAssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false
)

@HiltViewModel
class AIAssistantViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val kelingApiService: KelingApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AIAssistantUiState(
            messages = listOf(
                ChatMessage(
                    id = "welcome",
                    content = "你好！我是课灵，基于大型语言模型的 AI 学习助手（类似 GPT-4 一类的大模型）。\n\n我可以帮你：\n• 查询「今日任务」、学习进度\n• 针对具体学科问题做详细讲解\n• 结合你的课程和任务给出学习计划与建议\n\n直接告诉我你想做什么吧，比如：\n「今天我有什么任务？」 或 「给我讲讲微积分极限的本质」。",
                    isFromUser = false
                )
            )
        )
    )
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            content = text,
            isFromUser = true
        )
        _uiState.update { it.copy(messages = it.messages + userMsg, isTyping = true) }

        viewModelScope.launch {
            val reply = buildAssistantReply(text)
            val aiMsg = ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                content = reply,
                isFromUser = false
            )
            _uiState.update {
                it.copy(
                    messages = it.messages + aiMsg,
                    isTyping = false
                )
            }
        }
    }

    private suspend fun buildAssistantReply(query: String): String {
        val user = userRepository.getCurrentUser().first()
        val grade = user?.grade ?: "未知年级"

        val (startOfDay, endOfDay) = todayBounds()
        val sb = StringBuilder()

        // 如果用户在问「今日任务」「任务安排」之类，用真实任务数据做上下文
        val needTasksContext = query.contains("今日任务") ||
                (query.contains("今天") && query.contains("任务")) ||
                query.contains("学习计划")

        if (needTasksContext) {
            val gradeKey = user?.grade ?: "2024"
            val allTasks = taskRepository.getTasksForGrade(gradeKey).first()
            val todayTasks = allTasks.filter { task ->
                when (task.type) {
                    TaskType.DAILY ->
                        task.status != TaskStatus.COMPLETED &&
                                task.createdAt in startOfDay..endOfDay
                    TaskType.CHALLENGE ->
                        task.status != TaskStatus.COMPLETED
                    else -> false
                }
            }
            sb.appendLine("【当前用户信息】")
            sb.appendLine("姓名：${user?.realName ?: "同学"}，年级：$grade")
            sb.appendLine()
            sb.appendLine("【今日任务列表】")
            if (todayTasks.isEmpty()) {
                sb.appendLine("今天暂时没有记录在案的任务，你可以让我为你定制一些练习计划。")
            } else {
                todayTasks.forEachIndexed { index, t ->
                    val statusText = when (t.status) {
                        TaskStatus.COMPLETED -> "已完成"
                        TaskStatus.IN_PROGRESS -> "进行中"
                        TaskStatus.PENDING -> "待开始"
                        TaskStatus.EXPIRED -> "已过期"
                        TaskStatus.CANCELLED -> "已取消"
                    }
                    sb.appendLine("${index + 1}. [${t.type}] ${t.title}（状态：$statusText，预计${t.estimatedMinutes}分钟，经验值${t.experienceReward}）")
                }
            }
            sb.appendLine()
        }

        val systemPrompt = buildString {
            appendLine("你是「课灵」——面向大学生的学习助手，背后使用了中文能力很强的大规模语言模型（类似 GPT-4 一类的大模型）。")
            appendLine("请用简体中文回答，结构清晰、有条理，注重具体、可操作的建议。")
            appendLine("当用户询问今日任务或学习计划时，请优先结合我提供的【今日任务列表】做出合理安排和优先级建议。")
            appendLine("当用户提学科问题（如数学、物理、编程等），请像优秀家教一样分步骤讲解关键概念、推导过程和例题，必要时给出练习建议。")
            appendLine()
            if (sb.isNotEmpty()) {
                appendLine("以下是用户的当前学习上下文信息，请在回答时充分利用：")
                appendLine(sb.toString())
            }
            appendLine("用户问题：$query")
        }

        return try {
            val resp = kelingApiService.sendChatMessage(
                token = "",
                request = ChatRequest(
                    message = systemPrompt,
                    context = if (needTasksContext) "assistant_today_tasks" else "assistant_qa"
                )
            )
            resp.body()?.reply?.takeIf { it.isNotBlank() } ?: fallbackAnswer(query)
        } catch (e: Exception) {
            fallbackAnswer(query)
        }
    }

    private fun fallbackAnswer(query: String): String {
        return when {
            query.contains("任务") -> {
                "我没能访问到完整的任务数据，但你可以先打开首页的「今日任务」和「任务中心」，看看有哪些待完成的日常和挑战任务。\n\n" +
                        "一般建议的顺序是：\n1. 先完成时间紧、占分高的课程作业或项目\n2. 然后做 1~2 个难度适中的练习巩固知识点\n3. 最后预习明天要上的课程，保证有预期和问题清单。\n\n" +
                        "如果你愿意，可以把你今天的主要课程和任务发给我，我帮你排一个具体的时间表。"
            }
            query.contains("建议") -> {
                "给你一些通用但实用的学习建议：\n\n" +
                        "1. 每天预留固定的「深度学习时段」（例如晚饭后 1.5 小时），只做最重要的任务。\n" +
                        "2. 对每门课都维持「预习-上课-复习-练习」的闭环，而不是只在考试前突击。\n" +
                        "3. 把做错的题目记到错题本里，按知识点归类，每周至少复盘一次。\n\n" +
                        "你也可以告诉我你现在最想提升哪门课或哪类能力，我可以给出更有针对性的计划。"
            }
            else -> {
                "这是一个很好的问题。我目前无法访问外网，但会根据我掌握的通识知识给你一个尽量清晰的思路。\n\n" +
                        "你可以进一步补充：\n• 你现在处于什么年级、专业？\n• 这个问题大概是哪一门课里的？\n• 你已经理解到哪一步，卡在了哪里？\n\n提供这些信息后，我可以给你更精细的解析和例题。"
            }
        }
    }

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
}

