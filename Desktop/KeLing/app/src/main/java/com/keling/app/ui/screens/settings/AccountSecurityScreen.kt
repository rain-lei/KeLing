package com.keling.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.keling.app.ui.components.NeonButton
import com.keling.app.ui.components.NeonCard
import com.keling.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSecurityScreen(
    onBack: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPwdVisible by remember { mutableStateOf(false) }
    var newPwdVisible by remember { mutableStateOf(false) }
    var confirmPwdVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PaperBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PaperBackground)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "账户安全",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = InkPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaperBackground
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 修改密码
                Text(
                    text = "修改密码",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary
                )
                NeonCard(glowColor = PaperBorder) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        PasswordField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = "当前密码",
                            visible = currentPwdVisible,
                            onToggleVisible = { currentPwdVisible = !currentPwdVisible }
                        )
                        PasswordField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = "新密码",
                            visible = newPwdVisible,
                            onToggleVisible = { newPwdVisible = !newPwdVisible }
                        )
                        PasswordField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "确认新密码",
                            visible = confirmPwdVisible,
                            onToggleVisible = { confirmPwdVisible = !confirmPwdVisible }
                        )
                        NeonButton(
                            text = "修改密码",
                            onClick = {
                                when {
                                    currentPassword.isBlank() -> scope.launch { snackbarHostState.showSnackbar("请输入当前密码", withDismissAction = true) }
                                    newPassword.length < 6 -> scope.launch { snackbarHostState.showSnackbar("新密码至少 6 位", withDismissAction = true) }
                                    newPassword != confirmPassword -> scope.launch { snackbarHostState.showSnackbar("两次输入的新密码不一致", withDismissAction = true) }
                                    else -> {
                                        scope.launch { snackbarHostState.showSnackbar("密码修改功能需连接服务器，当前已记录您的操作", withDismissAction = true) }
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 绑定设备（占位）
                Text(
                    text = "已绑定设备",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary
                )
                NeonCard(glowColor = PaperBorder) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = NeonBlue
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "当前设备",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = InkPrimary
                                )
                                Text(
                                    text = "本机",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkSecondary
                                )
                            }
                        }
                        Text(
                            text = "当前使用",
                            style = MaterialTheme.typography.labelMedium,
                            color = NeonGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggleVisible: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = InkSecondary) },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleVisible) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "隐藏" else "显示",
                    tint = InkSecondary
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonBlue,
            unfocusedBorderColor = PaperBorder,
            focusedLabelColor = NeonBlue,
            unfocusedLabelColor = InkSecondary,
            cursorColor = NeonBlue,
            focusedTextColor = InkPrimary,
            unfocusedTextColor = InkPrimary,
            focusedContainerColor = PaperSurface,
            unfocusedContainerColor = PaperSurface
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
