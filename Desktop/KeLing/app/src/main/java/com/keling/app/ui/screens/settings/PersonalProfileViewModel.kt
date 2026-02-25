package com.keling.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class PersonalProfileUiState(
    val user: User? = null,
    val realName: String = "",
    val email: String = "",
    val phone: String = "",
    val grade: String = "",
    val schoolId: String = "",
    val classId: String = "",
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null
)

@HiltViewModel
class PersonalProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalProfileUiState())
    val uiState: StateFlow<PersonalProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = userRepository.getCurrentUser().first()
            user?.let {
                _uiState.update { state ->
                    state.copy(
                        user = it,
                        realName = it.realName,
                        email = it.email ?: "",
                        phone = it.phone ?: "",
                        grade = it.grade ?: "",
                        schoolId = it.schoolId ?: "",
                        classId = it.classId ?: "",
                        isLoading = false
                    )
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateRealName(value: String) {
        _uiState.update { it.copy(realName = value) }
    }

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun updatePhone(value: String) {
        _uiState.update { it.copy(phone = value) }
    }

    fun updateGrade(value: String) {
        _uiState.update { it.copy(grade = value) }
    }

    fun updateSchoolId(value: String) {
        _uiState.update { it.copy(schoolId = value) }
    }

    fun updateClassId(value: String) {
        _uiState.update { it.copy(classId = value) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val state = _uiState.value
            val user = state.user ?: return@launch
            _uiState.update { it.copy(isLoading = true, saveError = null) }
            try {
                userRepository.updateUser(
                    user.copy(
                        realName = state.realName.ifBlank { user.realName },
                        email = state.email.takeIf { it.isNotBlank() },
                        phone = state.phone.takeIf { it.isNotBlank() },
                        grade = state.grade.takeIf { it.isNotBlank() },
                        schoolId = state.schoolId.takeIf { it.isNotBlank() },
                        classId = state.classId.takeIf { it.isNotBlank() }
                    )
                )
                _uiState.update {
                    it.copy(isLoading = false, saveSuccess = true, saveError = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        saveSuccess = false,
                        saveError = e.message ?: "保存失败"
                    )
                }
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun clearSaveError() {
        _uiState.update { it.copy(saveError = null) }
    }
}
