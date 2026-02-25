package com.keling.app.ui.screens.knowledge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.data.model.KnowledgePoint
import com.keling.app.data.model.KnowledgeRelation
import com.keling.app.data.model.RelationType
import com.keling.app.ui.components.NeonCard
import com.keling.app.ui.theme.*

data class KnowledgeGraphUiState(
    val courseName: String = "",
    val points: List<KnowledgePoint> = emptyList(),
    val relations: List<KnowledgeRelation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeGraphScreen(
    onBack: () -> Unit,
    onNavigateToPractice: (String) -> Unit,
    viewModel: KnowledgeGraphViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.courseName.isBlank()) "知识图谱" else "${uiState.courseName} · 知识图谱",
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
        },
        containerColor = PaperBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PaperBackground)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NeonBlue)
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "暂时无法加载知识图谱",
                            color = InkSecondary
                        )
                    }
                }
                uiState.points.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂时没有可用的知识图谱数据，稍后再试或多做题积累学习记录。",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "核心知识点",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(uiState.points) { point ->
                            KnowledgePointCard(
                                point = point,
                                relations = uiState.relations.filter {
                                    it.fromPointId == point.id || it.toPointId == point.id
                                },
                                onClick = { onNavigateToPractice(point.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KnowledgePointCard(
    point: KnowledgePoint,
    relations: List<KnowledgeRelation>,
    onClick: () -> Unit
) {
    val glow = when {
        point.importance >= 4 -> NeonPurple
        point.difficulty >= 4 -> NeonOrange
        else -> PaperBorder
    }
    NeonCard(glowColor = glow, onClick = onClick) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = NeonBlue,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = point.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = InkPrimary
                )
            }
            if (!point.description.isNullOrBlank()) {
                Text(
                    text = point.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text("难度 ${point.difficulty}/5", color = InkSecondary)
                    }
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text("重要度 ${point.importance}/5", color = InkSecondary)
                    }
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text("掌握度 ${(point.masteryLevel * 100).toInt()}%", color = InkSecondary)
                    }
                )
            }

            if (relations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PaperSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AltRoute,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "相关关系",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkSecondary
                        )
                    }
                    relations.forEach { rel ->
                        val direction = when (rel.relationType) {
                            RelationType.PREREQUISITE -> "前置关系"
                            RelationType.CONTAINS -> "包含关系"
                            RelationType.RELATED -> "相关"
                            RelationType.EXTENDS -> "拓展"
                        }
                        Text(
                            text = "· $direction (${rel.fromPointId} → ${rel.toPointId})",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkMuted
                        )
                    }
                }
            }
        }
    }
}

