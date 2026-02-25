package com.keling.app.ui.screens.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.ui.theme.PaperBackground
import com.keling.app.ui.theme.NeonBlue
import com.keling.app.ui.theme.NeonGreen
import com.keling.app.ui.theme.InkPrimary
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.repository.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    fun saveFocusSession(durationMinutes: Int) {
        viewModelScope.launch {
            taskRepository.recordManualStudy(durationMinutes)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    onBack: () -> Unit,
    viewModel: FocusViewModel = hiltViewModel()
) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            elapsedSeconds += 1
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("专注学习", color = InkPrimary) },
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
        },
        containerColor = PaperBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .background(PaperBackground),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "专注计时",
                style = MaterialTheme.typography.titleMedium,
                color = InkPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = NeonBlue
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (!isRunning) {
                Button(
                    onClick = { isRunning = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("开始学习")
                }
            } else {
                Button(
                    onClick = {
                        isRunning = false
                        val totalMinutes = (elapsedSeconds / 60).coerceAtLeast(1)
                        viewModel.saveFocusSession(totalMinutes)
                        elapsedSeconds = 0
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("结束学习并保存时长")
                }
            }
        }
    }
}

