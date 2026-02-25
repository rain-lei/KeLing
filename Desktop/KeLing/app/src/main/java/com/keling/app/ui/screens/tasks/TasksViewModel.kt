package com.keling.app.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.model.TaskStatus
import com.keling.app.data.model.TaskType
import com.keling.app.data.model.Task
import com.keling.app.data.repository.TaskRepository
import com.keling.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TasksUiState(
    val allTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val isLoading: Boolean = false,
    val grade: String = "2024"
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadTasks()
    }

    private fun loadTasks() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val grade = userRepository.getCurrentUser().first()?.grade ?: "2024"
            _uiState.update { it.copy(grade = grade) }
            val existing = taskRepository.getTasksForGrade(grade).first()
            if (existing.isEmpty()) {
                taskRepository.generateAndSaveTasksForGrade(grade)
            }
            taskRepository.getTasksForGrade(grade).collect { list ->
                _uiState.update {
                    it.copy(
                        allTasks = list,
                        filteredTasks = list,
                        totalTasks = list.size,
                        completedTasks = list.count { t -> t.status == TaskStatus.COMPLETED },
                        inProgressTasks = list.count { t -> t.status == TaskStatus.IN_PROGRESS },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun filterByType(type: TaskType?) {
        val allTasks = _uiState.value.allTasks
        val filtered = if (type == null) allTasks else allTasks.filter { it.type == type }
        _uiState.update { it.copy(filteredTasks = filtered) }
    }

    fun refresh() {
        loadTasks()
    }
}
