package com.keling.app.data.repository

import com.keling.app.data.local.dao.CourseDao
import com.keling.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import javax.inject.Inject

interface CourseRepository {
    fun getAllCourses(): Flow<List<Course>>
    fun getCoursesBySemester(semester: String): Flow<List<Course>>
    suspend fun getCourseById(courseId: String): Course?
    fun getTodaySchedule(): Flow<List<ScheduleItem>>
    fun getWeekSchedule(): Flow<List<ScheduleItem>>
    fun getChaptersByCourse(courseId: String): Flow<List<Chapter>>
    fun getMaterialsByCourse(courseId: String): Flow<List<Material>>
    suspend fun syncFromEducationalSystem(studentId: String): Result<Unit>
    
    // 课表编辑
    suspend fun upsertScheduleItem(item: ScheduleItem)
    suspend fun deleteScheduleItemById(id: String)
}

class CourseRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao
) : CourseRepository {

    override fun getAllCourses(): Flow<List<Course>> {
        return courseDao.getAllCourses()
    }

    override fun getCoursesBySemester(semester: String): Flow<List<Course>> {
        return courseDao.getCoursesBySemester(semester)
    }

    override suspend fun getCourseById(courseId: String): Course? {
        return courseDao.getCourseById(courseId)
    }

    override fun getTodaySchedule(): Flow<List<ScheduleItem>> {
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        // 转换为周一=1的格式
        val adjustedDay = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
        return courseDao.getScheduleByDay(adjustedDay)
    }

    override fun getWeekSchedule(): Flow<List<ScheduleItem>> {
        return courseDao.getAllScheduleItems()
    }

    override fun getChaptersByCourse(courseId: String): Flow<List<Chapter>> {
        return courseDao.getChaptersByCourse(courseId)
    }

    override fun getMaterialsByCourse(courseId: String): Flow<List<Material>> {
        return courseDao.getMaterialsByCourse(courseId)
    }

    override suspend fun syncFromEducationalSystem(studentId: String): Result<Unit> {
        // 目前不再本地生成任何固定课程/课表数据
        // 实际的同步逻辑应由后端完成，这里仅保留接口以便后续接入
        return Result.success(Unit)
    }

    override suspend fun upsertScheduleItem(item: ScheduleItem) {
        courseDao.insertScheduleItem(item)
    }

    override suspend fun deleteScheduleItemById(id: String) {
        courseDao.deleteScheduleItemById(id)
    }

}
