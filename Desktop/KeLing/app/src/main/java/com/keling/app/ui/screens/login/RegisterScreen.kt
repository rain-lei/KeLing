package com.keling.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.ui.theme.*

/**
 * 注册页 · 学号+密码注册（学生端）
 * 预留：短信验证码注册/登录/关联账号（防忘记密码）的 UI 与状态结构
 */
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var studentId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var realName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) onRegisterSuccess()
    }

    if (uiState.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text("注册成功", fontWeight = FontWeight.Bold, color = InkPrimary)
            },
            text = {
                Text("您已成功注册，点击确定进入首页。", color = InkSecondary)
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onSuccessDialogDismiss() }) {
                    Text("确定", fontWeight = FontWeight.Bold, color = NeonBlue)
                }
            },
            containerColor = PaperSurface,
            titleContentColor = InkPrimary,
            textContentColor = InkSecondary
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = KelingSpacing.horizontalPage)
                .statusBarsPadding()
                .padding(top = 24.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "注册",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "学号 + 密码注册（学生端）",
                style = MaterialTheme.typography.bodyMedium,
                color = InkMuted
            )
            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = studentId,
                onValueChange = { studentId = it; viewModel.clearError() },
                label = { Text("学号", color = InkMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = InkMuted)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InkPrimary,
                    unfocusedBorderColor = InkSecondary.copy(alpha = 0.6f),
                    cursorColor = NeonBlue,
                    focusedTextColor = InkPrimary,
                    unfocusedTextColor = InkPrimary,
                    focusedContainerColor = PaperSurface,
                    unfocusedContainerColor = PaperSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearError() },
                label = { Text("密码（至少6位）", color = InkMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = InkMuted)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = InkMuted
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InkPrimary,
                    unfocusedBorderColor = InkSecondary.copy(alpha = 0.6f),
                    cursorColor = NeonBlue,
                    focusedTextColor = InkPrimary,
                    unfocusedTextColor = InkPrimary,
                    focusedContainerColor = PaperSurface,
                    unfocusedContainerColor = PaperSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; viewModel.clearError() },
                label = { Text("确认密码", color = InkMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = InkMuted)
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InkPrimary,
                    unfocusedBorderColor = InkSecondary.copy(alpha = 0.6f),
                    cursorColor = NeonBlue,
                    focusedTextColor = InkPrimary,
                    unfocusedTextColor = InkPrimary,
                    focusedContainerColor = PaperSurface,
                    unfocusedContainerColor = PaperSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = realName,
                onValueChange = { realName = it },
                label = { Text("昵称（选填）", color = InkMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = InkMuted)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InkPrimary,
                    unfocusedBorderColor = InkSecondary.copy(alpha = 0.6f),
                    cursorColor = NeonBlue,
                    focusedTextColor = InkPrimary,
                    unfocusedTextColor = InkPrimary,
                    focusedContainerColor = PaperSurface,
                    unfocusedContainerColor = PaperSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.registerByStudentId(studentId, password, confirmPassword, realName)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonBlue,
                    contentColor = androidx.compose.ui.graphics.Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = androidx.compose.ui.graphics.Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "注 册",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonRed,
                    textAlign = TextAlign.Center
                )
            }

            // ========== 预留：短信验证码注册/登录/关联账号（防忘记密码） ==========
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "后续可支持：短信验证码注册、短信登录、关联手机号找回密码",
                style = MaterialTheme.typography.labelSmall,
                color = InkMuted,
                textAlign = TextAlign.Center
            )
            // 预留结构：切换为「手机号+短信验证码」注册时显示手机号、验证码、获取验证码按钮
            // if (uiState.registerMode == RegisterMode.SMS_PHONE) { ... }
            // 预留：忘记密码 -> 通过短信验证码重置 / 关联手机号
            // ==========

            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = onBackToLogin) {
                Text(
                    text = "已有账号？去登录",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )
            }
        }
    }
}
