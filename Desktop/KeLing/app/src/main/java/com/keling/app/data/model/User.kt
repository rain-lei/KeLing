package com.keling.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户角色类型（应用仅使用学生端）
 */
enum class UserRole {
    STUDENT,
    @Suppress("unused") TEACHER,
    @Suppress("unused") PARENT
}

/**
 * 隐私设置级别
 */
enum class PrivacyLevel {
    PUBLIC,     // 公开 - 全校可见
    FRIENDS,    // 仅好友 - 同班同学
    PRIVATE     // 私密 - 仅自己
}

/**
 * 用户实体
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val username: String,
    val realName: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val schoolId: String? = null,
    val classId: String? = null,
    val grade: String? = null,
    val email: String? = null,
    val phone: String? = null,
    /** 密码哈希（注册时设置，用于登录校验） */
    val passwordHash: String? = null,
    val privacyLevel: PrivacyLevel = PrivacyLevel.FRIENDS,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

/**
 * 用户画像数据
 */
data class UserProfile(
    val user: User,
    val level: Int = 1,
    val experience: Int = 0,
    val totalExperience: Int = 0,
    val streak: Int = 0,                    // 连续学习天数
    val totalStudyMinutes: Int = 0,         // 累计学习时长
    val taskCompletedCount: Int = 0,        // 完成任务数
    val achievementCount: Int = 0,          // 获得成就数
    val rankInClass: Int? = null,           // 班级排名
    val rankInSchool: Int? = null           // 学校排名
)

/**
 * 仪表盘数据
 */
data class DashboardData(
    val learningProgress: Float,            // 学习进度 0-1
    val taskCompletion: Int,                // 任务完成数
    val skillGrowth: Map<String, Float>,    // 能力成长值
    val achievements: List<Achievement>,     // 成就列表
    val todayStudyMinutes: Int,             // 今日学习时长
    val weeklyProgress: List<Float>         // 本周每日进度
)
