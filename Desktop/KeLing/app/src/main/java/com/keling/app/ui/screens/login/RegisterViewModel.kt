package com.keling.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ========== 预留：短信验证码注册/登录/关联账号（防忘记密码） ==========
// 以下常量与接口在接入真实短信时使用：
// const val DEMO_VERIFICATION_CODE = "123456"
// fun sendVerificationCode(phone: String)  -> 调用后端/短信网关发送验证码
// fun verifyCodeForRegister(phone: String, code: String) -> 注册前校验
// fun verifyCodeForLogin(phone: String, code: String) -> 短信验证码登录
// fun verifyCodeForBindPhone(phone: String, code: String) -> 关联手机号以便找回密码
// ==========

/** 注册方式：学号+密码（当前） / 短信验证码（预留） */
enum class RegisterMode {
    STUDENT_ID,  // 学号+密码
    SMS_PHONE    // 手机号+短信验证码+密码（预留）
}

data class RegisterUiState(
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    /** 注册成功弹窗，用户点击确定后再跳转 */
    val showSuccessDialog: Boolean = false,
    val error: String? = null,
    val codeSent: Boolean = false,
    val registerMode: RegisterMode = RegisterMode.STUDENT_ID
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /** 学号+密码注册：两次密码一致才成功 */
    fun registerByStudentId(
        studentId: String,
        password: String,
        confirmPassword: String,
        realName: String
    ) {
        when {
            studentId.isBlank() -> _uiState.update { it.copy(error = "请输入学号") }
            password.length < 6 -> _uiState.update { it.copy(error = "密码至少6位") }
            password != confirmPassword -> _uiState.update { it.copy(error = "两次密码不一致，请重新输入") }
            else -> runRegisterByStudentId(studentId.trim(), password, realName.ifBlank { "学习者" })
        }
    }

    private fun runRegisterByStudentId(studentId: String, password: String, realName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = userRepository.registerByStudentId(studentId, password, realName)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false, showSuccessDialog = true, error = null)
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "注册失败")
                    }
                }
            )
        }
    }

    /** 用户点击「注册成功」弹窗的确定后调用，再触发跳转 */
    fun onSuccessDialogDismiss() {
        _uiState.update { it.copy(showSuccessDialog = false, registerSuccess = true) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // ========== 预留：短信验证码注册/登录/关联账号 ==========
    /** 发送验证码（预留：接入短信网关后在此调用后端发送） */
    fun sendVerificationCode(phone: String) {
        if (phone.isBlank()) {
            _uiState.update { it.copy(error = "请输入手机号") }
            return
        }
        if (phone.length != 11) {
            _uiState.update { it.copy(error = "请输入11位手机号") }
            return
        }
        // TODO: 调用 SmsRepository / 后端接口发送验证码
        _uiState.update { it.copy(codeSent = true, error = null) }
    }

    /** 使用手机号+验证码+密码注册（预留：接入短信后启用，需先 verifyCode 再 register） */
    fun registerByPhone(
        phone: String,
        code: String,
        password: String,
        confirmPassword: String,
        realName: String
    ) {
        when {
            phone.isBlank() -> _uiState.update { it.copy(error = "请输入手机号") }
            code.isBlank() -> _uiState.update { it.copy(error = "请输入验证码") }
            password.length < 6 -> _uiState.update { it.copy(error = "密码至少6位") }
            password != confirmPassword -> _uiState.update { it.copy(error = "两次密码不一致，请重新输入") }
            else -> {
                // TODO: 先校验验证码 SmsRepository.verifyCode(phone, code)，通过后再调用 userRepository.register(phone, password, realName)
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                    val result = userRepository.register(phone, password, realName)
                    result.fold(
                        onSuccess = {
                            _uiState.update {
                                it.copy(isLoading = false, showSuccessDialog = true, error = null)
                            }
                        },
                        onFailure = { e ->
                            _uiState.update {
                                it.copy(isLoading = false, error = e.message ?: "注册失败")
                            }
                        }
                    )
                }
            }
        }
    }

    /** 切换注册方式（学号 / 手机号短信，预留） */
    fun setRegisterMode(mode: RegisterMode) {
        _uiState.update { it.copy(registerMode = mode, error = null) }
    }
}
