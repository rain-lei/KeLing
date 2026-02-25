package com.keling.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.data.model.PrivacyLevel
import com.keling.app.ui.components.NeonButton
import com.keling.app.ui.components.NeonCard
import com.keling.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    onBack: () -> Unit,
    viewModel: PrivacySettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("隐私设置已保存", withDismissAction = true)
            viewModel.clearSaveSuccess()
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
                        text = "隐私设置",
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
                Text(
                    text = "资料可见性",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary
                )
                NeonCard(glowColor = PaperBorder) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            PrivacyLevel.PUBLIC to "公开",
                            PrivacyLevel.FRIENDS to "仅好友 / 同班",
                            PrivacyLevel.PRIVATE to "私密"
                        ).forEach { (level, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setPrivacyLevel(level) }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = uiState.privacyLevel == level,
                                    onClick = { viewModel.setPrivacyLevel(level) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = NeonBlue,
                                        unselectedColor = InkMuted
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = InkPrimary
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "学习记录",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary
                )
                NeonCard(glowColor = PaperBorder) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "学习记录对他人可见",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary
                            )
                            Text(
                                text = "关闭后他人无法看到你的学习时长与完成情况",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary
                            )
                        }
                        Switch(
                            checked = uiState.studyRecordVisible,
                            onCheckedChange = { viewModel.setStudyRecordVisible(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonBlue,
                                checkedTrackColor = NeonBlue.copy(alpha = 0.3f),
                                uncheckedThumbColor = InkMuted,
                                uncheckedTrackColor = PaperBorder
                            )
                        )
                    }
                }

                NeonButton(
                    text = "保存",
                    onClick = { viewModel.save() },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
