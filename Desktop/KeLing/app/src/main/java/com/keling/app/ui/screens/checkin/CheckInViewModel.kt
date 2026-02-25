package com.keling.app.ui.screens.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.repository.AchievementEvent
import com.keling.app.data.repository.CheckInRepository
import com.keling.app.data.repository.UserRepository
import com.keling.app.data.repository.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckInUiState(
    val userId: String? = null,
    val alreadyCheckedIn: Boolean = false,
    val currentStreak: Int = 0,
    val isCheckingIn: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkInRepository: CheckInRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().first()
            if (user == null) {
                _uiState.update { it.copy(alreadyCheckedIn = true) }
                return@launch
            }
            val checkedIn = checkInRepository.isCheckedInToday(user.id)
            val streak = checkInRepository.getStreak(user.id).first()
            _uiState.update {
                it.copy(
                    userId = user.id,
                    alreadyCheckedIn = checkedIn,
                    currentStreak = streak
                )
            }
        }
    }

    /** 执行签到，成功后调用 onDone（由 UI 导航到首页） */
    fun performCheckIn(onDone: () -> Unit) {
        val userId = _uiState.value.userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingIn = true, error = null) }
            try {
                checkInRepository.checkIn(userId)
                val newStreak = checkInRepository.getStreak(userId).first()
                achievementRepository.checkAndUnlockAchievements(
                    userId,
                    AchievementEvent.StreakReached(newStreak)
                )
                _uiState.update {
                    it.copy(isCheckingIn = false, currentStreak = newStreak)
                }
                onDone()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isCheckingIn = false, error = e.message)
                }
            }
        }
    }
}
