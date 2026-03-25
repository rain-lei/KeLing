/**
 * Models.kt
 * 定义应用的所有数据结构
 * 使用Kotlin的data class，自动生成equals、hashCode、toString
 *
 * @Serializable用于JSON序列化，方便网络传输和本地存储
 */

package com.keling.app.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// ==================== 用户相关 ====================

/**
 * 用户数据
 *
 * @property id 用户唯一ID，用设备ID或随机生成
 * @property name 显示名称
 * @property level 等级，影响解锁功能
 * @property energy 能量值（⚡），完成任务获得，用于培育
 * @property crystals 知识结晶（💎），学习深度奖励，用于兑换
 * @property streakDays 连续学习天数
 * @property totalStudyMinutes 总学习时长（分钟）
 */
@Serializable
data class User(
    val id: String,
    val name: String = "星际园丁",
    val level: Int = 1,
    val exp: Int = 0,
    val energy: Int = 100,
    val crystals: Int = 10,
    val streakDays: Int = 0,
    val totalStudyMinutes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// ==================== 课程与知识 ====================

/**
 * 课程（知识星球）
 *
 * @property id 课程ID
 * @property name 课程名称
 * @property code 课程代码，如"MA101"
 * @property teacher 教师姓名
 * @property schedule 上课时间安排
 * @property location 上课地点
 * @property themeColor 星球主题色（存储颜色值）
 * @property masteryLevel 掌握度 0.0~1.0
 * @property plantStage 植物生长阶段 0-5
 */
@Serializable
data class Course(
    val id: String,
    val name: String,
    val code: String,
    val teacher: String,
    val schedule: List<ScheduleSlot> = emptyList(),
    val location: String = "",
    val themeColor: Long = 0xFFE8A87C, // 默认恒星橙
    val masteryLevel: Float = 0f,
    val plantStage: Int = 0, // 0=种子,1=萌芽,2=生长,3=开花,4=结果,5=繁茂
    /**
     * 星球形象索引：
     * -1 表示“随机星球”（显示时会根据 course.id 解析出一个稳定的随机形象）
     * 0..N-1 对应可选的星球图片
     */
    val planetStyleIndex: Int = -1,
    val lastStudiedAt: Long? = null,
    val totalStudyMinutes: Int = 0
)

/**
 * 上课时间段
 */
@Serializable
data class ScheduleSlot(
    val dayOfWeek: Int, // 1=周一, 7=周日
    val startHour: Int,
    val startMinute: Int,
    val durationMinutes: Int
)

/**
 * 知识点（知识图谱节点）
 *
 * @property id 节点ID
 * @property courseId 所属课程
 * @property name 知识点名称
 * @property description 详细描述
 * @property parentIds 前置知识点（必须先学这些）
 * @property childIds 后续知识点（学完可以学这些）
 * @property difficulty 难度 1-5
 * @property masteryLevel 掌握度 0.0~1.0
 * @property positionX 在图谱中的X坐标（0-1相对位置）
 * @property positionY 在图谱中的Y坐标
 */
@Serializable
data class KnowledgeNode(
    val id: String,
    val courseId: String,
    val name: String,
    val description: String = "",
    val parentIds: List<String> = emptyList(),
    val childIds: List<String> = emptyList(),
    val difficulty: Int = 3,
    val masteryLevel: Float = 0f,
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val isUnlocked: Boolean = false // 前置知识满足后才解锁
)

// ==================== 任务系统 ====================

/**
 * 学习任务
 *
 * @property id 任务ID
 * @property title 任务标题
 * @property description 任务描述
 * @property type 任务类型
 * @property courseId 关联课程（可选）
 * @property knowledgeNodeIds 关联知识点
 * @property status 当前状态
 * @property priority 优先级 1-5，5最紧急
 * @property estimatedMinutes 预计用时
 * @property actualMinutes 实际用时（完成后填写）
 * @property rewards 完成奖励
 */
@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val type: TaskType,
    val courseId: String? = null,
    val knowledgeNodeIds: List<String> = emptyList(),
    val status: TaskStatus = TaskStatus.PENDING,
    val priority: Int = 3,
    val estimatedMinutes: Int = 25,
    val actualMinutes: Int? = null,
    val rewards: Rewards = Rewards(),
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null,
    val completedAt: Long? = null
)

enum class TaskType {
    DAILY_CARE,      // 日常培育：短时长，维持知识
    DEEP_EXPLORATION, // 深度探索：长时长，突破难点
    REVIEW_RITUAL,   // 复习仪式：基于遗忘曲线
    BOUNTY,          // 赏金任务：校园活动
    RESCUE           // 星际救援：帮助同学
}

enum class TaskStatus {
    PENDING,     // 待完成
    IN_PROGRESS, // 进行中
    COMPLETED,   // 已完成
    ABANDONED    // 已放弃
}

@Serializable
data class Rewards(
    val energy: Int = 10,
    val crystals: Int = 5,
    val exp: Int = 20
)

// ==================== 笔记与AI ====================

/**
 * 知识笔记（由AI生成或用户创建）
 *
 * @property id 笔记ID
 * @property title 笔记标题
 * @property content 笔记内容（支持Markdown）
 * @property sourceType 来源类型
 * @property aiExplanation 如果是AI生成，原始解释
 * @property relatedNodeIds 关联的知识点
 * @property tags 标签，用于检索
 */
@Serializable
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val sourceType: NoteSource,
    val aiExplanation: String? = null,
    val relatedNodeIds: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val reviewCount: Int = 0, // 复习次数
    val lastReviewedAt: Long? = null
)

enum class NoteSource {
    AI_GENERATED,  // AI生成
    USER_CREATED,  // 用户手动创建
    CLASS_CAPTURE, // 课堂拍照/录音
    BOUNTY_REWARD  // 赏金奖励
}

// ==================== 赏金系统 ====================

/**
 * 赏金任务（校园活动）
 */
@Serializable
data class Bounty(
    val id: String,
    val title: String,
    val description: String,
    val type: BountyType,
    val difficulty: Int, // 1-5
    val rewards: Rewards,
    val deadline: Long? = null,
    val participantCount: Int = 0,
    val maxParticipants: Int? = null,
    val isTeamTask: Boolean = false,
    val tags: List<String> = emptyList()
)

enum class BountyType {
    ACADEMIC,    // 学术探索：讲座、论文
    SKILL,       // 技能试炼：编程、设计
    SOCIAL,      // 社交协作：组队、互助
    EVENT,       // 校园事件：比赛、志愿
    RESCUE       // 星际救援：紧急求助
}

// ==================== 成就系统 ====================

/**
 * 成就定义
 *
 * @property id 成就ID
 * @property name 成就名称
 * @property description 成就描述
 * @property icon 成就图标
 * @property category 成就类别
 * @property requirement 达成条件描述
 * @property rewardEnergy 能量奖励
 * @property rewardCrystals 结晶奖励
 * @property isUnlocked 是否已解锁
 * @property unlockedAt 解锁时间
 */
@Serializable
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val category: AchievementCategory,
    val requirement: String = "",
    val rewardEnergy: Int = 50,
    val rewardCrystals: Int = 30,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Int = 0,
    val maxProgress: Int = 1
)

enum class AchievementCategory {
    LEARNING,    // 学习成就：完成课程、任务等
    STREAK,      // 坚持成就：连续签到、学习天数
    EXPLORATION, // 探索成就：解锁知识、发现新内容
    SOCIAL,      // 社交成就：帮助他人、组队学习
    MASTERY      // 精通成就：完全掌握课程知识
}

// ==================== 签到系统 ====================

/**
 * 签到记录
 *
 * @property date 签到日期（格式：yyyyMMdd）
 * @property userId 用户ID
 * @property rewardReceived 已领取的奖励
 */
@Serializable
data class CheckInRecord(
    val date: String,
    val userId: String,
    val rewardReceived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 签到奖励配置
 */
@Serializable
data class CheckInReward(
    val day: Int,           // 第几天
    val energy: Int,        // 能量奖励
    val crystals: Int,      // 结晶奖励
    val isSpecial: Boolean = false, // 是否为特殊奖励日
    val specialReward: String? = null // 特殊奖励描述
)

// ==================== 学习报告 ====================

/**
 * 学习报告
 *
 * @property id 报告ID
 * @property userId 用户ID
 * @property startDate 统计开始时间
 * @property endDate 统计结束时间
 * @property totalStudyMinutes 学习总时长
 * @property completedTasks 完成任务数
 * @property coursesStudied 学习课程数
 * @property averageMastery 平均掌握度提升
 * @property aiInsight AI洞察分析
 * @property createdAt 创建时间
 */
@Serializable
data class StudyReport(
    val id: String,
    val userId: String,
    val startDate: Long,
    val endDate: Long,
    val totalStudyMinutes: Int = 0,
    val completedTasks: Int = 0,
    val coursesStudied: Int = 0,
    val averageMastery: Float = 0f,
    val streakDays: Int = 0,
    val aiInsight: String = "",
    val strongPoints: List<String> = emptyList(),
    val weakPoints: List<String> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

// ==================== 学习记录 ====================

/**
 * 学习记录条目
 *
 * @property id 记录ID
 * @property userId 用户ID
 * @property courseId 关联课程ID
 * @property taskId 关联任务ID（可选）
 * @property type 学习类型
 * @property durationMinutes 学习时长
 * @property createdAt 创建时间
 * @property notes 备注
 */
@Serializable
data class StudyRecord(
    val id: String,
    val userId: String,
    val courseId: String? = null,
    val taskId: String? = null,
    val type: StudyType,
    val durationMinutes: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

enum class StudyType {
    TASK_COMPLETION,  // 完成任务
    COURSE_STUDY,     // 课程学习
    REVIEW_SESSION,   // 复习回顾
    PRACTICE,         // 练习刷题
    AI_INTERACTION    // AI交互学习
}

// ==================== 工具函数 ====================

/**
 * JSON序列化配置
 * ignoreUnknownKeys = true 遇到未知字段不报错（向后兼容）
 * prettyPrint = true 格式化输出，方便调试
 */
val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

/**
 * 扩展函数：将对象转为JSON字符串
 */
inline fun <reified T> T.toJson(): String = json.encodeToString(this)

/**
 * 扩展函数：将JSON字符串转为对象
 */
inline fun <reified T> String.fromJson(): T = json.decodeFromString(this)

/**
 * 获取今日日期字符串（yyyyMMdd格式）
 */
fun getTodayDateString(): String {
    val cal = java.util.Calendar.getInstance()
    return String.format(
        "%04d%02d%02d",
        cal.get(java.util.Calendar.YEAR),
        cal.get(java.util.Calendar.MONTH) + 1,
        cal.get(java.util.Calendar.DAY_OF_MONTH)
    )
}

/**
 * 预定义成就列表
 */
val PREDEFINED_ACHIEVEMENTS = listOf(
    // 学习成就
    Achievement(
        id = "first_task",
        name = "初学者",
        description = "完成第一个学习任务",
        icon = "🌱",
        category = AchievementCategory.LEARNING,
        requirement = "完成1个任务",
        maxProgress = 1
    ),
    Achievement(
        id = "task_master_10",
        name = "任务达人",
        description = "累计完成10个学习任务",
        icon = "📋",
        category = AchievementCategory.LEARNING,
        requirement = "累计完成10个任务",
        rewardEnergy = 100,
        maxProgress = 10
    ),
    Achievement(
        id = "task_master_50",
        name = "任务大师",
        description = "累计完成50个学习任务",
        icon = "🏆",
        category = AchievementCategory.LEARNING,
        requirement = "累计完成50个任务",
        rewardEnergy = 300,
        rewardCrystals = 100,
        maxProgress = 50
    ),
    Achievement(
        id = "first_course",
        name = "星际探索者",
        description = "创建第一颗知识星球",
        icon = "🌍",
        category = AchievementCategory.EXPLORATION,
        requirement = "创建1个课程",
        maxProgress = 1
    ),
    Achievement(
        id = "mastery_80",
        name = "知识精通",
        description = "将任意课程掌握度提升到80%以上",
        icon = "✨",
        category = AchievementCategory.MASTERY,
        requirement = "单课程掌握度≥80%",
        rewardEnergy = 200,
        rewardCrystals = 50,
        maxProgress = 1
    ),
    // 坚持成就
    Achievement(
        id = "streak_3",
        name = "坚持三天",
        description = "连续学习3天",
        icon = "🔥",
        category = AchievementCategory.STREAK,
        requirement = "连续学习3天",
        rewardEnergy = 50,
        maxProgress = 3
    ),
    Achievement(
        id = "streak_7",
        name = "周周坚持",
        description = "连续学习7天",
        icon = "🌟",
        category = AchievementCategory.STREAK,
        requirement = "连续学习7天",
        rewardEnergy = 150,
        rewardCrystals = 30,
        maxProgress = 7
    ),
    Achievement(
        id = "streak_30",
        name = "月度之星",
        description = "连续学习30天",
        icon = "💫",
        category = AchievementCategory.STREAK,
        requirement = "连续学习30天",
        rewardEnergy = 500,
        rewardCrystals = 200,
        maxProgress = 30
    ),
    // 探索成就
    Achievement(
        id = "knowledge_10",
        name = "知识收集者",
        description = "解锁10个知识点",
        icon = "📚",
        category = AchievementCategory.EXPLORATION,
        requirement = "解锁10个知识节点",
        rewardEnergy = 80,
        maxProgress = 10
    ),
    Achievement(
        id = "notes_5",
        name = "笔记达人",
        description = "创建5篇学习笔记",
        icon = "📝",
        category = AchievementCategory.LEARNING,
        requirement = "创建5篇笔记",
        rewardEnergy = 100,
        maxProgress = 5
    )
)

/**
 * 签到奖励配置
 */
val CHECK_IN_REWARDS = listOf(
    CheckInReward(day = 1, energy = 10, crystals = 5),
    CheckInReward(day = 2, energy = 15, crystals = 8),
    CheckInReward(day = 3, energy = 20, crystals = 10),
    CheckInReward(day = 4, energy = 25, crystals = 12),
    CheckInReward(day = 5, energy = 30, crystals = 15),
    CheckInReward(day = 6, energy = 40, crystals = 20),
    CheckInReward(day = 7, energy = 50, crystals = 30, isSpecial = true, specialReward = "周奖励已解锁！")
)