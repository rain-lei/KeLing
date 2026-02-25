package com.keling.app.ui.screens.campus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.model.Task
import com.keling.app.data.model.TaskDifficulty
import com.keling.app.data.model.TaskStatus
import com.keling.app.data.model.TaskType
import com.keling.app.data.repository.TaskRepository
import com.keling.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CampusTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun enrollBountyTask(bounty: BountyTask) {
        viewModelScope.launch {
            val grade = userRepository.getCurrentUser().first()?.grade
            val task = Task(
                id = "bounty_${bounty.id}_${System.currentTimeMillis()}",
                title = bounty.title,
                description = bounty.description,
                type = TaskType.CHALLENGE,
                difficulty = TaskDifficulty.MEDIUM,
                status = TaskStatus.PENDING,
                targetGrade = grade,
                experienceReward = bounty.rewardExp,
                coinReward = bounty.rewardPoints,
                estimatedMinutes = 60
            )
            taskRepository.saveTasks(listOf(task))
        }
    }

    fun enrollCampusEvent(event: CampusEvent) {
        viewModelScope.launch {
            val grade = userRepository.getCurrentUser().first()?.grade
            val task = Task(
                id = "event_${event.id}_${System.currentTimeMillis()}",
                title = event.title,
                description = "【${event.organizer}】${event.title} 活动参与与总结。",
                type = TaskType.CHALLENGE,
                difficulty = TaskDifficulty.MEDIUM,
                status = TaskStatus.PENDING,
                targetGrade = grade,
                experienceReward = event.rewardExp,
                coinReward = event.rewardPoints,
                estimatedMinutes = 90
            )
            taskRepository.saveTasks(listOf(task))
        }
    }

    fun completeBountyTask(bounty: BountyTask) {
        viewModelScope.launch {
            taskRepository.completeChallengeTaskByTitle(bounty.title)
        }
    }

    fun completeCampusEvent(event: CampusEvent) {
        viewModelScope.launch {
            taskRepository.completeChallengeTaskByTitle(event.title)
        }
    }
}

