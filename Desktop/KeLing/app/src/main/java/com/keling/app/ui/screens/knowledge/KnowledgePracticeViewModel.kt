package com.keling.app.ui.screens.knowledge

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.local.dao.KnowledgeDao
import com.keling.app.data.model.LearningRecord
import com.keling.app.data.model.KnowledgePoint
import com.keling.app.data.remote.KelingApiService
import com.keling.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class PracticeQuestion(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

data class KnowledgePracticeUiState(
    val point: KnowledgePoint? = null,
    val questions: List<PracticeQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val isFinished: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val wrongRecords: List<LearningRecord> = emptyList()
)

@HiltViewModel
class KnowledgePracticeViewModel @Inject constructor(
    private val knowledgeDao: KnowledgeDao,
    private val kelingApiService: KelingApiService,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: String = savedStateHandle["courseId"] ?: ""
    private val pointId: String = savedStateHandle["pointId"] ?: ""

    private val _uiState = MutableStateFlow(KnowledgePracticeUiState(isLoading = true))
    val uiState: StateFlow<KnowledgePracticeUiState> = _uiState.asStateFlow()

    init {
        loadPracticeData()
    }

    private fun loadPracticeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val point = knowledgeDao.getKnowledgePointById(pointId)
                // 这里可以调用后端 AI 接口生成与知识点相关的题目，
                // 当前版本使用本地兜底题目，保证功能可用。
                val questions = createLocalQuestionsForPoint(point)

                val userId = userRepository.getCurrentUser().first()?.id
                val wrongRecords = if (userId != null) {
                    knowledgeDao.getLearningRecordsForPoint(userId, pointId)
                        .first()
                        .filter { !it.isCorrect }
                } else emptyList()

                _uiState.update {
                    it.copy(
                        point = point,
                        questions = questions,
                        currentIndex = 0,
                        correctCount = 0,
                        wrongCount = 0,
                        isFinished = questions.isEmpty(),
                        isLoading = false,
                        wrongRecords = wrongRecords
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "加载练习任务失败，请稍后重试。"
                    )
                }
            }
        }
    }

    fun answerQuestion(optionIndex: Int) {
        val state = _uiState.value
        val questions = state.questions
        if (state.isFinished || state.currentIndex !in questions.indices) return

        val question = questions[state.currentIndex]
        val isCorrect = optionIndex == question.correctIndex

        viewModelScope.launch {
            val userId = userRepository.getCurrentUser().first()?.id
            if (userId != null) {
                // 将 question.text 作为 questionId 存入学习记录，方便错题本展示
                val record = LearningRecord(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    knowledgePointId = pointId,
                    questionId = question.text,
                    isCorrect = isCorrect,
                    timeSpentSeconds = 30   // 简化：默认每题 30 秒
                )
                knowledgeDao.insertLearningRecord(record)

                // 根据所有做题记录更新 masteryLevel
                val acc = knowledgeDao.getAccuracyForPoint(userId, pointId) ?: 0f
                knowledgeDao.updateMasteryLevel(pointId, acc)
            }

            // 更新 UI 进度
            val nextIndex = state.currentIndex + 1
            val finished = nextIndex >= questions.size
            val newCorrect = state.correctCount + if (isCorrect) 1 else 0
            val newWrong = state.wrongCount + if (isCorrect) 0 else 1

            // 刷新错题本
            val updatedWrongRecords = if (userId != null) {
                knowledgeDao.getLearningRecordsForPoint(userId, pointId)
                    .first()
                    .filter { !it.isCorrect }
            } else state.wrongRecords

            _uiState.update {
                it.copy(
                    currentIndex = nextIndex.coerceAtMost(questions.size),
                    correctCount = newCorrect,
                    wrongCount = newWrong,
                    isFinished = finished,
                    wrongRecords = updatedWrongRecords
                )
            }
        }
    }

    private fun createLocalQuestionsForPoint(point: KnowledgePoint?): List<PracticeQuestion> {
        val baseName = point?.name ?: "本知识点"
        val q1 = PracticeQuestion(
            id = "q1",
            text = "关于「$baseName」，以下说法哪一项最准确？",
            options = listOf(
                "完全与本课程无关的概念。",
                "只需要死记硬背，无需理解。",
                "是本课程的核心知识之一，需要理解含义与应用场景。",
                "只在期末考试中偶尔出现，不需要平时关注。"
            ),
            correctIndex = 2
        )
        val q2 = PracticeQuestion(
            id = "q2",
            text = "在学习「$baseName」时，下列做法中哪一种最有助于掌握？",
            options = listOf(
                "只看定义，不做任何例题或练习。",
                "尝试把该知识点应用到至少 1~2 道习题或实际案例中。",
                "只看同学的笔记，不自己动手整理。",
                "完全依赖考前突击复习。"
            ),
            correctIndex = 1
        )
        val q3 = PracticeQuestion(
            id = "q3",
            text = "如果你在「$baseName」相关题目中经常出错，最优先的改进方向是？",
            options = listOf(
                "减少做题次数，避免暴露问题。",
                "把错题抄一遍就算复习完成。",
                "分析每一道错题是概念不清、公式记错还是步骤混乱，并针对性补强。",
                "只做难题，不再看基础练习。"
            ),
            correctIndex = 2
        )
        return listOf(q1, q2, q3)
    }
}

