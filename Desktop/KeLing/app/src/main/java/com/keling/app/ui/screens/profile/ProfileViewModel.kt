package com.keling.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.repository.CheckInRepository
import com.keling.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.OptIn
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "同学",
    val streak: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkInRepository: CheckInRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .flatMapLatest { user ->
                    val streakFlow = user?.id?.let { checkInRepository.getStreak(it) } ?: flowOf(0)
                    streakFlow.map { streak -> Pair(user?.realName ?: "同学", streak) }
                }
                .collect { (name, streak) ->
                    _uiState.update {
                        it.copy(userName = name, streak = streak)
                    }
                }
        }
    }
}
