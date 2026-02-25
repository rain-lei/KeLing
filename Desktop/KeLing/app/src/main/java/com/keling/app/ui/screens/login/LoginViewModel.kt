package com.keling.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.repository.ERROR_NOT_REGISTERED
import com.keling.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val rememberMe: Boolean = false,
    val error: String? = null,
    /** 未注册时弹出提示，引导去注册 */
    val showNotRegisteredDialog: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun login(username: String, password: String) {
        if (username.isBlank()) {
            _uiState.update { it.copy(error = "请输入用户名") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = userRepository.login(username, password)
            
            result.fold(
                onSuccess = { _ ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    val msg = exception.message ?: "登录失败"
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = msg,
                            showNotRegisteredDialog = (msg == ERROR_NOT_REGISTERED)
                        )
                    }
                }
            )
        }
    }
    
    fun setRememberMe(remember: Boolean) {
        _uiState.update { it.copy(rememberMe = remember) }
    }

    fun dismissNotRegisteredDialog() {
        _uiState.update { it.copy(showNotRegisteredDialog = false, error = null) }
    }
}
