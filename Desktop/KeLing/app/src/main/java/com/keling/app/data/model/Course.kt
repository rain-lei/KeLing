package com.keling.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 课程类型
 */
enum class CourseType {
    REQUIRED,       // 必修课
    ELECTIVE,       // 选修课
    PRACTICE,       // 实践课
    EXPERIMENT      // 实验课
}

/**
 * 课程实体
 */
@Entity(tableName = "courses")
data class Course(
    @PrimaryKey
    val id: String,
    val name: String,
    val code: String,                       // 课程代码
    val type: CourseType,
    val credits: Float,                     // 学分
    val teacherName: String,
    val teacherId: String? = null,
    val location: String? = null,           // 上课地点
    val description: String? = null,
    val coverImageUrl: String? = null,
    val semester: String,                   // 学期
    val progress: Float = 0f                // 学习进度 0-1
)

/**
 * 课表条目
 */
@Entity(tableName = "schedule_items")
data class ScheduleItem(
    @PrimaryKey
    val id: String,
    val courseId: String,
    val courseName: String,
    val teacherName: String,
    val dayOfWeek: Int,                     // 1-7 周一到周日
    val startTime: String,                  // HH:mm 格式
    val endTime: String,
    val location: String,
    val weekStart: Int,                     // 起始周
    val weekEnd: Int,                       // 结束周
    val weekType: WeekType = WeekType.ALL   // 单双周
)

/**
 * 周类型
 */
enum class WeekType {
    ALL,        // 每周
    ODD,        // 单周
    EVEN        // 双周
}

/**
 * 章节
 */
@Entity(tableName = "chapters")
data class Chapter(
    @PrimaryKey
    val id: String,
    val courseId: String,
    val title: String,
    val orderIndex: Int,
    val description: String? = null,
    val contentUrl: String? = null,
    val duration: Int = 0,                  // 预计学习时长(分钟)
    val isCompleted: Boolean = false,
    val progress: Float = 0f
)

/**
 * 教材资源
 */
@Entity(tableName = "materials")
data class Material(
    @PrimaryKey
    val id: String,
    val courseId: String,
    val chapterId: String? = null,
    val title: String,
    val type: MaterialType,
    val url: String,
    val size: Long = 0,                     // 文件大小(字节)
    val downloadedPath: String? = null,     // 本地下载路径
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 教材类型
 */
enum class MaterialType {
    PDF,
    VIDEO,
    AUDIO,
    IMAGE,
    DOCUMENT,
    PPT
}
