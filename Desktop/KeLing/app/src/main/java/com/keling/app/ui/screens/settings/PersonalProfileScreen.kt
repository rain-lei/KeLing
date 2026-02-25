package com.keling.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.ui.components.NeonButton
import com.keling.app.ui.components.NeonCard
import com.keling.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalProfileScreen(
    onBack: () -> Unit,
    viewModel: PersonalProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("保存成功", withDismissAction = true)
            viewModel.clearSaveSuccess()
        }
    }
    LaunchedEffect(uiState.saveError) {
        uiState.saveError?.let { msg ->
            snackbarHostState.showSnackbar(msg, withDismissAction = true)
            viewModel.clearSaveError()
        }
    }

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
                        text = "个人资料",
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

            if (uiState.user == null && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("未登录", color = InkSecondary)
                }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 头像占位
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(96.dp),
                        shape = CircleShape,
                        color = PaperSurface
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = InkMuted,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }

                NeonCard(glowColor = PaperBorder) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProfileTextField(
                            value = uiState.realName,
                            onValueChange = viewModel::updateRealName,
                            label = "昵称 / 真实姓名",
                            icon = Icons.Default.Person
                        )
                        ProfileTextField(
                            value = uiState.email,
                            onValueChange = viewModel::updateEmail,
                            label = "邮箱",
                            icon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email
                        )
                        ProfileTextField(
                            value = uiState.phone,
                            onValueChange = viewModel::updatePhone,
                            label = "手机号",
                            icon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone
                        )
                        ProfileTextField(
                            value = uiState.schoolId,
                            onValueChange = viewModel::updateSchoolId,
                            label = "学校",
                            icon = Icons.Default.School
                        )
                        ProfileTextField(
                            value = uiState.grade,
                            onValueChange = viewModel::updateGrade,
                            label = "年级",
                            icon = Icons.Default.Grade
                        )
                        ProfileTextField(
                            value = uiState.classId,
                            onValueChange = viewModel::updateClassId,
                            label = "班级",
                            icon = Icons.Default.Class
                        )
                    }
                }

                NeonButton(
                    text = if (uiState.isLoading) "保存中…" else "保存",
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = InkSecondary) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonBlue
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
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
