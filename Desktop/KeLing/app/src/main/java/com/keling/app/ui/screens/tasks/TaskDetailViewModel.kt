package com.keling.app.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.model.Task
import com.keling.app.data.model.TaskActionType
import com.keling.app.data.model.TaskType
import com.keling.app.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val canExecute: Boolean = false
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val task = taskRepository.getTaskById(taskId)
            if (task == null) {
                _uiState.update { it.copy(isLoading = false, error = "任务不存在") }
                return@launch
            }
            val canExecute = task.actionType?.let { runCatching { TaskActionType.valueOf(it) }.isSuccess } == true
                && task.status != com.keling.app.data.model.TaskStatus.COMPLETED
            _uiState.update {
                it.copy(task = task, isLoading = false, canExecute = canExecute)
            }
        }
    }

    /**
     * 日常任务计时完成后调用：按实际用时记录学习时长并标记任务完成
     */
    fun completeDailyTaskWithMinutes(minutes: Int) {
        val task = _uiState.value.task ?: return
        if (task.type != TaskType.DAILY) return
        viewModelScope.launch {
            if (minutes > 0) {
                taskRepository.recordManualStudy(minutes)
            }
            taskRepository.completeTask(task.id)
            // 重新加载，刷新 UI 状态
            loadTask(task.id)
        }
    }
}
