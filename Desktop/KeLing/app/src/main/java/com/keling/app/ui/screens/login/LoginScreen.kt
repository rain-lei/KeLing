package com.keling.app.ui.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
 * 登录页 · 古风画卷感
 * 暖米纸底、墨色文字、朱砂点缀、衬线标题、留白与细线分隔
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onLoginSuccess()
    }

    if (uiState.showNotRegisteredDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissNotRegisteredDialog() },
            title = { Text("提示", fontWeight = FontWeight.Bold, color = InkPrimary) },
            text = { Text("您还未注册，请先注册后再登录。", color = InkSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissNotRegisteredDialog()
                    onNavigateToRegister()
                }) {
                    Text("去注册", fontWeight = FontWeight.Bold, color = NeonBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissNotRegisteredDialog() }) {
                    Text("取消", color = InkMuted)
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
                .padding(horizontal = KelingSpacing.horizontalPage)
                .statusBarsPadding()
                .padding(top = 48.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 标题区 · 留白 + 衬线
            Text(
                text = "课灵",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "专注学习，每日精进",
                style = MaterialTheme.typography.bodyLarge,
                color = InkMuted
            )
            // 细线分隔（用 Box 兼容旧版 Material3 无 HorizontalDivider）
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(top = 20.dp, bottom = 8.dp)
                    .height(1.dp)
                    .background(InkSecondary.copy(alpha = 0.4f))
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 输入框 · 手机号或学号、密码
            PaperTextField(
                value = username,
                onValueChange = { username = it },
                label = "手机号 / 学号",
                leadingIcon = Icons.Default.Person,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            PaperTextField(
                value = password,
                onValueChange = { password = it },
                label = "密码",
                leadingIcon = Icons.Default.Lock,
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 记住我 · 忘记密码
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.rememberMe,
                        onCheckedChange = { viewModel.setRememberMe(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = NeonBlue,
                            uncheckedColor = InkMuted
                        )
                    )
                    Text(
                        text = "记住我",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSecondary
                    )
                }
                Text(
                    text = "忘记密码？",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonBlue,
                    modifier = Modifier.clickable { }
                )
            }
            Spacer(modifier = Modifier.height(28.dp))

            // 主按钮 · 朱砂实心
            Button(
                onClick = { viewModel.login(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonBlue,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "登 录",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            // 次要按钮 · 朱砂描边
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonRed,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "还没有账号？",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "去注册",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun PaperTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = InkMuted) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = InkMuted
            )
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        shape = RoundedCornerShape(6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = InkPrimary,
            unfocusedBorderColor = InkSecondary.copy(alpha = 0.6f),
            focusedLabelColor = InkMuted,
            unfocusedLabelColor = InkMuted,
            cursorColor = NeonBlue,
            focusedTextColor = InkPrimary,
            unfocusedTextColor = InkPrimary,
            focusedContainerColor = PaperSurface,
            unfocusedContainerColor = PaperSurface
        ),
        modifier = modifier
    )
}
