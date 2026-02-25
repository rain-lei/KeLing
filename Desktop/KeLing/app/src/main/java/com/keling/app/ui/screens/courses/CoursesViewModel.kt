package com.keling.app.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.repository.CourseRepository
import com.keling.app.data.model.Course
import com.keling.app.data.model.ScheduleItem
import com.keling.app.data.model.WeekType
import com.keling.app.util.CourseStatus
import com.keling.app.util.currentMinutesOfDay
import com.keling.app.util.getCourseStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CoursesUiState(
    val todaySchedule: List<ScheduleItemUi> = emptyList(),
    val allCourses: List<CourseUi> = emptyList(),
    val weekSchedule: Map<Int, List<ScheduleItemUi>> = emptyMap(),
    val isLoading: Boolean = false
)

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState: StateFlow<CoursesUiState> = _uiState.asStateFlow()

    init {
        observeCourses()
        observeSchedules()
    }

    /**
     * 监听课程列表
     */
    private fun observeCourses() {
        viewModelScope.launch {
            courseRepository.getAllCourses().collect { courses ->
                val courseUi = courses.map { it.toUi() }
                _uiState.update { it.copy(allCourses = courseUi) }
            }
        }
    }

    /**
     * 监听今日课表与周课表，并计算课程状态
     */
    private fun observeSchedules() {
        // 今日课表
        viewModelScope.launch {
            courseRepository.getTodaySchedule().collect { items ->
                val nowMinutes = currentMinutesOfDay()
                val uiItems = items.map { it.toUi(nowMinutes) }
                _uiState.update { it.copy(todaySchedule = uiItems, isLoading = false) }
            }
        }

        // 周课表
        viewModelScope.launch {
            courseRepository.getWeekSchedule().collect { items ->
                val nowMinutes = currentMinutesOfDay()
                val grouped = items.groupBy { it.dayOfWeek }
                    .mapValues { (_, dayItems) -> dayItems.map { it.toUi(nowMinutes) } }
                _uiState.update { it.copy(weekSchedule = grouped) }
            }
        }
    }

    /**
     * 新增一条课表记录
     */
    fun addScheduleItem(
        courseName: String,
        teacherName: String,
        location: String,
        dayOfWeek: Int,
        startTime: String,
        endTime: String
    ) {
        viewModelScope.launch {
            val id = "schedule_${System.currentTimeMillis()}"
            val item = ScheduleItem(
                id = id,
                courseId = courseName, // 简单使用课程名作为标识（如有需要可后续关联课程表）
                courseName = courseName,
                teacherName = teacherName,
                dayOfWeek = dayOfWeek,
                startTime = startTime,
                endTime = endTime,
                location = location,
                weekStart = 1,
                weekEnd = 16,
                weekType = WeekType.ALL
            )
            courseRepository.upsertScheduleItem(item)
        }
    }

    /**
     * 删除一条课表记录
     */
    fun deleteScheduleItem(id: String) {
        viewModelScope.launch {
            courseRepository.deleteScheduleItemById(id)
        }
    }

    // 映射函数
    private fun Course.toUi(): CourseUi {
        return CourseUi(
            id = id,
            name = name,
            teacherName = teacherName,
            credits = credits,
            progress = progress
        )
    }

    private fun ScheduleItem.toUi(nowMinutes: Int): ScheduleItemUi {
        val status = getCourseStatus(startTime, endTime, nowMinutes)
        return ScheduleItemUi(
            id = id,
            courseId = courseId,
            courseName = courseName,
            teacherName = teacherName,
            location = location,
            dayOfWeek = dayOfWeek,
            startTime = startTime,
            endTime = endTime,
            status = status,
            isOngoing = status == CourseStatus.ONGOING
        )
    }
}
