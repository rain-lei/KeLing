package com.keling.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 任务类型
 */
enum class TaskType {
    DAILY,          // 日常任务
    WEEKLY,         // 周任务
    CHALLENGE,      // 挑战任务
    TEAM,           // 组队任务
    REVIEW,         // 复习任务
    PRACTICE        // 练习任务
}

/**
 * 任务难度
 */
enum class TaskDifficulty {
    EASY,           // 简单
    MEDIUM,         // 中等
    HARD,           // 困难
    EXPERT          // 专家
}

/**
 * 任务状态
 */
enum class TaskStatus {
    PENDING,        // 待完成
    IN_PROGRESS,    // 进行中
    COMPLETED,      // 已完成
    EXPIRED,        // 已过期
    CANCELLED       // 已取消
}

/**
 * 任务实体
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey
    val id: String,
    /** 所属用户 ID，与登录用户绑定 */
    val userId: String? = null,
    val title: String,
    val description: String,
    val type: TaskType,
    val difficulty: TaskDifficulty,
    val status: TaskStatus = TaskStatus.PENDING,
    val courseId: String? = null,
    val chapterId: String? = null,
    val experienceReward: Int = 0,          // 经验值奖励
    val coinReward: Int = 0,                // 金币奖励
    val deadline: Long? = null,             // 截止时间
    val estimatedMinutes: Int = 0,          // 预计耗时
    val progress: Float = 0f,               // 完成进度 0-1
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val parentTaskId: String? = null,       // 父任务ID(用于子任务)
    val order: Int = 0,                     // 任务顺序
    val targetGrade: String? = null,        // 目标年级，如 "2024" "大一"
    val actionType: String? = null,         // 可执行类型：QUIZ/READING/EXERCISE/VIDEO/MEMORIZATION
    val actionPayload: String? = null       // JSON 载荷，见 TaskAction 各 Payload
)

/**
 * 任务节点(用于三维任务地图)
 */
data class TaskNode(
    val task: Task,
    val x: Float,
    val y: Float,
    val z: Float,
    val connections: List<String>,          // 连接的其他任务ID
    val isUnlocked: Boolean = true,
    val prerequisites: List<String> = emptyList()
)

/**
 * 组队任务
 */
@Entity(tableName = "team_tasks")
data class TeamTask(
    @PrimaryKey
    val id: String,
    val taskId: String,
    val teamId: String,
    val memberIds: List<String>,
    val leaderUserId: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 任务进度记录
 */
@Entity(tableName = "task_progress")
data class TaskProgress(
    @PrimaryKey
    val id: String,
    val taskId: String,
    val userId: String,
    val progress: Float,
    val timeSpentMinutes: Int = 0,
    val lastUpdatedAt: Long = System.currentTimeMillis()
)

/**
 * 学习时长记录（用于首页“学习时长”统计）
 */
@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey
    val id: String,
    /** 用户 ID，与登录用户绑定 */
    val userId: String,
    /** 当天标识，例如 2026-01-28，便于按天汇总 */
    val dayKey: String,
    /** 来源：FOCUS（专注学习）、TASK（完成任务）等 */
    val source: String,
    val taskId: String? = null,
    val durationMinutes: Int,
    val createdAt: Long = System.currentTimeMillis()
)
