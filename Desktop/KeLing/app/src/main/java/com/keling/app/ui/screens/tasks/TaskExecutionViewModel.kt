package com.keling.app.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.model.*
import com.keling.app.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskExecutionUiState(
    val task: Task? = null,
    val quizPayload: QuizPayload? = null,
    val readingPayload: ReadingPayload? = null,
    val exercisePayload: ExercisePayload? = null,
    val videoPayload: VideoPayload? = null,
    val memorizationPayload: MemorizationPayload? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val completionResult: TaskCompletionResult? = null,
    val exerciseCheckedCount: Int = 0,
    val readingElapsedSeconds: Int = 0,
    val videoElapsedSeconds: Int = 0
)

@HiltViewModel
class TaskExecutionViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskExecutionUiState())
    val uiState: StateFlow<TaskExecutionUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val task = taskRepository.getTaskById(taskId)
            if (task == null) {
                _uiState.update { it.copy(isLoading = false, error = "任务不存在") }
                return@launch
            }
            val quiz = taskRepository.parseQuizPayload(task)
            val reading = taskRepository.parseReadingPayload(task)
            val exercise = taskRepository.parseExercisePayload(task)
            val video = taskRepository.parseVideoPayload(task)
            val memorization = taskRepository.parseMemorizationPayload(task)
            _uiState.update {
                it.copy(
                    task = task,
                    quizPayload = quiz,
                    readingPayload = reading,
                    exercisePayload = exercise,
                    videoPayload = video,
                    memorizationPayload = memorization,
                    isLoading = false,
                    exerciseCheckedCount = (task.progress * (exercise?.totalCount ?: 1)).toInt()
                )
            }
        }
    }

    fun submitQuiz(answers: List<Int>) {
        val t = _uiState.value.task ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, completionResult = null) }
            val result = taskRepository.submitQuizCompletion(t.id, answers)
            _uiState.update {
                it.copy(isLoading = false, completionResult = result)
            }
        }
    }

    fun submitReading() {
        val t = _uiState.value.task ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, completionResult = null) }
            val result = taskRepository.submitReadingCompletion(t.id)
            _uiState.update {
                it.copy(isLoading = false, completionResult = result)
            }
        }
    }

    fun submitVideo() {
        val t = _uiState.value.task ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, completionResult = null) }
            val result = taskRepository.submitVideoCompletion(t.id)
            _uiState.update {
                it.copy(isLoading = false, completionResult = result)
            }
        }
    }

    fun submitMemorization() {
        val t = _uiState.value.task ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, completionResult = null) }
            val result = taskRepository.submitMemorizationCompletion(t.id)
            _uiState.update {
                it.copy(isLoading = false, completionResult = result)
            }
        }
    }

    fun submitExercise(checkedCount: Int) {
        val t = _uiState.value.task ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, completionResult = null) }
            val result = taskRepository.submitExerciseCompletion(t.id, checkedCount)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    completionResult = result,
                    exerciseCheckedCount = checkedCount,
                    task = taskRepository.getTaskById(t.id) ?: it.task
                )
            }
        }
    }

    fun setExerciseCheckedCount(count: Int) {
        _uiState.update { it.copy(exerciseCheckedCount = count) }
    }

    fun setReadingElapsedSeconds(seconds: Int) {
        _uiState.update { it.copy(readingElapsedSeconds = seconds) }
    }

    fun setVideoElapsedSeconds(seconds: Int) {
        _uiState.update { it.copy(videoElapsedSeconds = seconds) }
    }

    fun clearCompletionResult() {
        _uiState.update { it.copy(completionResult = null) }
    }
}
