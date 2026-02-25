package com.keling.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 成就类别
 */
enum class AchievementCategory {
    LEARNING,       // 学习类
    TASK,           // 任务类
    SOCIAL,         // 社交类
    STREAK,         // 连续类
    SPECIAL,        // 特殊类
    HIDDEN          // 隐藏成就
}

/**
 * 成就稀有度
 */
enum class AchievementRarity {
    COMMON,         // 普通
    RARE,           // 稀有
    EPIC,           // 史诗
    LEGENDARY       // 传说
}

/**
 * 成就定义
 */
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: AchievementCategory,
    val rarity: AchievementRarity,
    val iconName: String,                   // 图标资源名
    val experienceReward: Int = 0,
    val coinReward: Int = 0,
    val conditionType: String,              // 条件类型
    val conditionValue: Int,                // 条件数值
    val order: Int = 0
)

/**
 * 用户成就记录
 */
@Entity(tableName = "user_achievements")
data class UserAchievement(
    @PrimaryKey
    val id: String,
    val achievementId: String,
    val userId: String,
    val unlockedAt: Long = System.currentTimeMillis(),
    val progress: Float = 0f,               // 未解锁时的进度
    val isUnlocked: Boolean = false
)

/**
 * 徽章
 */
@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val color: String,                      // 颜色代码
    val tier: Int = 1,                      // 等级 1-5
    val category: String
)

/**
 * 用户徽章
 */
@Entity(tableName = "user_badges")
data class UserBadge(
    @PrimaryKey
    val id: String,
    val badgeId: String,
    val userId: String,
    val obtainedAt: Long = System.currentTimeMillis(),
    val isEquipped: Boolean = false         // 是否装备展示
)
