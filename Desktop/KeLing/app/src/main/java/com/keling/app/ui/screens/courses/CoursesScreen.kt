package com.keling.app.ui.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.ui.components.*
import com.keling.app.ui.theme.*
import com.keling.app.util.CourseStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = hiltViewModel(),
    onNavigateToCourseDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("今日课程", "全部课程", "课程表")
    var isEditMode by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperBackground)
            .statusBarsPadding()
    ) {
        // 顶部标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "课程管理",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = InkPrimary
            )

            Row {
                IconButton(onClick = { isEditMode = !isEditMode }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑课表",
                        tint = if (isEditMode) NeonGreen else NeonBlue
                    )
                }
                IconButton(onClick = { /* 同步课表 */ }) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "同步",
                        tint = NeonBlue
                    )
                }
            }
        }
        
        // 标签切换
        NeonTabRow(
            tabs = tabs,
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        if (isEditMode) {
            Text(
                text = "当前为编辑课表模式，可新增或删除课表条目",
                style = MaterialTheme.typography.bodySmall,
                color = NeonOrange,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        when (selectedTab) {
            0 -> TodayScheduleContent(
                scheduleItems = uiState.todaySchedule,
                onCourseClick = onNavigateToCourseDetail
            )
            1 -> AllCoursesContent(
                courses = uiState.allCourses,
                onCourseClick = onNavigateToCourseDetail
            )
            2 -> WeekScheduleContent(
                weekSchedule = uiState.weekSchedule,
                isEditMode = isEditMode,
                onAddSchedule = { courseName, teacherName, location, dayOfWeek, startTime, endTime ->
                    viewModel.addScheduleItem(
                        courseName = courseName,
                        teacherName = teacherName,
                        location = location,
                        dayOfWeek = dayOfWeek,
                        startTime = startTime,
                        endTime = endTime
                    )
                },
                onDeleteSchedule = { itemId ->
                    viewModel.deleteScheduleItem(itemId)
                }
            )
        }
    }
}

@Composable
private fun TodayScheduleContent(
    scheduleItems: List<ScheduleItemUi>,
    onCourseClick: (String) -> Unit
) {
    if (scheduleItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.EventAvailable,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "今天没有课程",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkSecondary
                )
                Text(
                    text = "好好休息或自主学习吧！",
                    style = MaterialTheme.typography.bodySmall,
                    color = InkMuted
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(scheduleItems) { item ->
                ScheduleCard(
                    item = item,
                    onClick = { onCourseClick(item.courseId) }
                )
            }
        }
    }
}

@Composable
private fun ScheduleCard(
    item: ScheduleItemUi,
    onClick: () -> Unit
) {
    val isOngoing = item.isOngoing
    val glowColor = if (isOngoing) NeonGreen else NeonBlue
    val statusText = when (item.status) {
        CourseStatus.NOT_STARTED -> "未开始"
        CourseStatus.ONGOING -> "进行中"
        CourseStatus.FINISHED -> "已结束"
    }
    val statusColor = when (item.status) {
        CourseStatus.NOT_STARTED -> NeonBlue
        CourseStatus.ONGOING -> NeonGreen
        CourseStatus.FINISHED -> InkMuted
    }
    
    NeonCard(
        glowColor = glowColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 时间
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = item.startTime,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = glowColor
                )
                Text(
                    text = item.endTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = InkSecondary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 分隔线
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(50.dp)
                    .background(glowColor)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 课程信息
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.courseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.teacherName} · ${item.location}",
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

@Composable
private fun AllCoursesContent(
    courses: List<CourseUi>,
    onCourseClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(courses) { course ->
            CourseCard(
                name = course.name,
                teacherName = course.teacherName,
                progress = course.progress,
                credits = course.credits,
                onClick = { onCourseClick(course.id) }
            )
        }
    }
}

@Composable
private fun WeekScheduleContent(
    weekSchedule: Map<Int, List<ScheduleItemUi>>,
    isEditMode: Boolean,
    onAddSchedule: (
        courseName: String,
        teacherName: String,
        location: String,
        dayOfWeek: Int,
        startTime: String,
        endTime: String
    ) -> Unit,
    onDeleteSchedule: (String) -> Unit
) {
    val dayNames = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    var showAddDialog by remember { mutableStateOf(false) }
    var newCourseName by remember { mutableStateOf("") }
    var newTeacherName by remember { mutableStateOf("") }
    var newLocation by remember { mutableStateOf("") }
    var newDayOfWeek by remember { mutableStateOf("1") }
    var newStartTime by remember { mutableStateOf("08:00") }
    var newEndTime by remember { mutableStateOf("09:40") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(text = "新增课表", color = InkPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newCourseName,
                        onValueChange = { newCourseName = it },
                        label = { Text("课程名称") }
                    )
                    OutlinedTextField(
                        value = newTeacherName,
                        onValueChange = { newTeacherName = it },
                        label = { Text("授课教师") }
                    )
                    OutlinedTextField(
                        value = newLocation,
                        onValueChange = { newLocation = it },
                        label = { Text("上课地点") }
                    )
                    OutlinedTextField(
                        value = newDayOfWeek,
                        onValueChange = { newDayOfWeek = it },
                        label = { Text("星期（1-7）") }
                    )
                    OutlinedTextField(
                        value = newStartTime,
                        onValueChange = { newStartTime = it },
                        label = { Text("开始时间（HH:mm）") }
                    )
                    OutlinedTextField(
                        value = newEndTime,
                        onValueChange = { newEndTime = it },
                        label = { Text("结束时间（HH:mm）") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val day = newDayOfWeek.toIntOrNull() ?: 1
                        if (newCourseName.isNotBlank()) {
                            onAddSchedule(
                                newCourseName.trim(),
                                newTeacherName.ifBlank { "未指定教师" },
                                newLocation.ifBlank { "未指定教室" },
                                day.coerceIn(1, 7),
                                newStartTime.ifBlank { "08:00" },
                                newEndTime.ifBlank { "09:40" }
                            )
                        }
                        showAddDialog = false
                        newCourseName = ""
                        newTeacherName = ""
                        newLocation = ""
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isEditMode) {
            item {
                NeonButton(
                    text = "新增课表",
                    onClick = { showAddDialog = true },
                    color = NeonBlue,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        items(7) { dayIndex ->
            val daySchedule = weekSchedule[dayIndex + 1] ?: emptyList()
            
            Column {
                Text(
                    text = dayNames[dayIndex],
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (daySchedule.isEmpty()) {
                    Text(
                        text = "无课程",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkMuted
                    )
                } else {
                    daySchedule.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.startTime}-${item.endTime}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeonBlue,
                                modifier = Modifier.width(90.dp)
                            )
                            Text(
                                text = item.courseName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = when (item.status) {
                                    CourseStatus.NOT_STARTED -> "未开始"
                                    CourseStatus.ONGOING -> "进行中"
                                    CourseStatus.FINISHED -> "已结束"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = when (item.status) {
                                    CourseStatus.NOT_STARTED -> NeonBlue
                                    CourseStatus.ONGOING -> NeonGreen
                                    CourseStatus.FINISHED -> InkMuted
                                },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            if (isEditMode) {
                                IconButton(onClick = { onDeleteSchedule(item.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "删除",
                                        tint = NeonOrange
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// UI 数据类
data class ScheduleItemUi(
    val id: String,
    val courseId: String,
    val courseName: String,
    val teacherName: String,
    val location: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
     val status: CourseStatus,
    val isOngoing: Boolean = false
)

data class CourseUi(
    val id: String,
    val name: String,
    val teacherName: String,
    val credits: Float,
    val progress: Float
)
