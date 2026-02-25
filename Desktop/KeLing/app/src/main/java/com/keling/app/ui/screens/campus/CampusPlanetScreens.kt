package com.keling.app.ui.screens.campus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.keling.app.ui.components.NeonButton
import com.keling.app.ui.components.NeonCard
import com.keling.app.ui.components.NeonOutlinedButton
import com.keling.app.ui.components.StarryBackground
import kotlinx.coroutines.launch
import com.keling.app.ui.theme.*

// ===== 云端校园星球入口 =====

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusPlanetScreen(
    onBack: () -> Unit,
    onNavigateToForum: () -> Unit,
    onNavigateToPractice: () -> Unit
    ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
    ) {
        StarryBackground(modifier = Modifier.fillMaxSize(), starCount = 60)

        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text("云端校园星球", color = InkPrimary)
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
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "选择你的校园星球",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkSecondary
                )

                // 论坛星球
                PlanetCard(
                    title = "论坛星球",
                    description = "加入话题小圈，与校友畅聊校园与成长。",
                    icon = Icons.Default.Forum,
                    glowColor = NeonPurple,
                    onClick = onNavigateToForum
                )

                // 实践星球
                PlanetCard(
                    title = "实践星球",
                    description = "挑战校园赏金榜，记录我的校园生活，获取积分与经验值。",
                    icon = Icons.Default.Public,
                    glowColor = NeonGreen,
                    onClick = onNavigateToPractice
                )
            }
        }
    }
}

@Composable
private fun PlanetCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    glowColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        glowColor = glowColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(PaperSurface)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = glowColor,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = InkMuted
            )
        }
    }
}

// ===== 论坛星球 =====

data class ForumTopic(
    val id: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val hot: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumPlanetScreen(
    onBack: () -> Unit
) {
    val topics = remember {
        listOf(
            ForumTopic("1", "考研 & 升学", "分享备考心得、择校经验。", 324, true),
            ForumTopic("2", "实习 & 校招", "投递分享、面经互助。", 268, true),
            ForumTopic("3", "二次元放松角", "动漫 / 游戏 / 同好闲聊。", 145, false),
            ForumTopic("4", "兴趣社团大厅", "乐队、舞社、电竞社都在这。", 189, false)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("论坛星球", color = InkPrimary) },
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
                .padding(16.dp)
        ) {
            Text(
                text = "选择一个话题小圈，和校友一起聊天、打卡、组队。",
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topics) { topic ->
                    ForumTopicCard(topic = topic)
                }
            }
        }
    }
}

@Composable
private fun ForumTopicCard(topic: ForumTopic) {
    NeonCard(glowColor = if (topic.hot) NeonPurple else PaperBorder) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = topic.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    )
                    Text(
                        text = topic.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = InkSecondary
                    )
                    Text(
                        text = "${topic.memberCount} 人在此星球",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonBlue
                    )
                }

                if (topic.hot) {
                    Text(
                        text = "HOT",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPink
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeonButton(
                    text = "加入小圈",
                    onClick = { /* TODO: 实际加入逻辑 */ },
                    modifier = Modifier.weight(1f)
                )
                NeonOutlinedButton(
                    text = "加好友",
                    onClick = { /* TODO: 打开用户列表，加好友 */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ===== 实践星球 =====

data class BountyTask(
    val id: String,
    val title: String,
    val description: String,
    val rewardPoints: Int,
    val rewardExp: Int
)

data class CampusEvent(
    val id: String,
    val title: String,
    val organizer: String,
    val rewardPoints: Int,
    val rewardExp: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticePlanetScreen(
    onBack: () -> Unit,
    viewModel: CampusTasksViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val bountyList = remember {
        listOf(
            BountyTask("b1", "校园开放日志愿讲解员", "为来访家长与同学介绍校园路线与学院情况。", 80, 120),
            BountyTask("b2", "图书馆整理协助", "配合馆员进行图书上架、整理分类。", 50, 80),
            BountyTask("b3", "编程马拉松赛事志愿者", "负责赛场秩序、签到与物资发放。", 100, 150)
        )
    }

    val campusEvents = remember {
        listOf(
            CampusEvent("e1", "校园科技文化节", "校团委", 120, 200),
            CampusEvent("e2", "学院新生迎新晚会", "计算机学院", 90, 150),
            CampusEvent("e3", "校运动会方阵", "体育部", 100, 160)
        )
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: 赏金榜, 1: 我的校园生活
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("实践星球", color = InkPrimary) },
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
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = PaperSurface,
                contentColor = NeonBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("校园赏金榜") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("我的校园生活") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedTab == 0) {
                    items(bountyList) { task ->
                        BountyTaskCard(
                            task = task,
                            onEnroll = {
                                scope.launch {
                                    viewModel.enrollBountyTask(task)
                                    snackbarHostState.showSnackbar("报名成功，已加入任务中心「挑战」", withDismissAction = true)
                                }
                            },
                            onComplete = {
                                scope.launch {
                                    viewModel.completeBountyTask(task)
                                    snackbarHostState.showSnackbar("已标记为完成，奖励经验与积分已结算", withDismissAction = true)
                                }
                            }
                        )
                    }
                } else {
                    items(campusEvents) { event ->
                        CampusEventCard(
                            event = event,
                            onEnroll = {
                                scope.launch {
                                    viewModel.enrollCampusEvent(event)
                                    snackbarHostState.showSnackbar("报名成功，已加入任务中心「挑战」", withDismissAction = true)
                                }
                            },
                            onComplete = {
                                scope.launch {
                                    viewModel.completeCampusEvent(event)
                                    snackbarHostState.showSnackbar("已标记为完成，奖励经验与积分已结算", withDismissAction = true)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BountyTaskCard(
    task: BountyTask,
    onEnroll: () -> Unit,
    onComplete: () -> Unit
) {
    NeonCard(glowColor = NeonGreen) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary
            )
            Text(
                text = "奖励：${task.rewardPoints} 积分 · ${task.rewardExp} 经验值",
                style = MaterialTheme.typography.labelSmall,
                color = NeonGreen
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeonOutlinedButton(
                    text = "报名",
                    onClick = onEnroll,
                    modifier = Modifier.weight(1f)
                )
                NeonButton(
                    text = "完成活动",
                    onClick = onComplete,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CampusEventCard(
    event: CampusEvent,
    onEnroll: () -> Unit,
    onComplete: () -> Unit
) {
    NeonCard(glowColor = NeonBlue) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )
            Text(
                text = "主办方：${event.organizer}",
                style = MaterialTheme.typography.bodySmall,
                color = InkSecondary
            )
            Text(
                text = "完成可获得 ${event.rewardPoints} 积分 · ${event.rewardExp} 经验值",
                style = MaterialTheme.typography.labelSmall,
                color = NeonBlue
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeonOutlinedButton(
                    text = "报名",
                    onClick = onEnroll,
                    modifier = Modifier.weight(1f)
                )
                NeonButton(
                    text = "完成活动",
                    onClick = onComplete,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

