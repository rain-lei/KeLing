package com.keling.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.model.PrivacyLevel
import com.keling.app.data.model.User
import com.keling.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrivacySettingsUiState(
    val privacyLevel: PrivacyLevel = PrivacyLevel.FRIENDS,
    val studyRecordVisible: Boolean = true,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class PrivacySettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacySettingsUiState())
    val uiState: StateFlow<PrivacySettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().first()
            user?.let {
                _uiState.update { state ->
                    state.copy(
                        privacyLevel = it.privacyLevel,
                        studyRecordVisible = it.privacyLevel != PrivacyLevel.PRIVATE
                    )
                }
            }
        }
    }

    fun setPrivacyLevel(level: PrivacyLevel) {
        _uiState.update {
            it.copy(
                privacyLevel = level,
                studyRecordVisible = level != PrivacyLevel.PRIVATE
            )
        }
    }

    fun setStudyRecordVisible(visible: Boolean) {
        _uiState.update { it.copy(studyRecordVisible = visible) }
    }

    fun save() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().first() ?: return@launch
            val state = _uiState.value
            userRepository.updateUser(
                user.copy(privacyLevel = state.privacyLevel)
            )
            _uiState.update { it.copy(saveSuccess = true) }
        }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}
