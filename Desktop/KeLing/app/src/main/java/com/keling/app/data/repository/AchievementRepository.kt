package com.keling.app.data.repository

import com.keling.app.data.local.dao.AchievementDao
import com.keling.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

interface AchievementRepository {
    fun getAllAchievements(): Flow<List<Achievement>>
    fun getUserAchievements(userId: String): Flow<List<UserAchievement>>
    fun getAchievementsWithProgress(userId: String): Flow<List<AchievementWithProgress>>
    suspend fun checkAndUnlockAchievements(userId: String, event: AchievementEvent): List<Achievement>
    fun getUnlockedCount(userId: String): Flow<Int>
    fun getUserBadges(userId: String): Flow<List<UserBadge>>
}

data class AchievementWithProgress(
    val achievement: Achievement,
    val userProgress: UserAchievement?,
    val isUnlocked: Boolean
)

sealed class AchievementEvent {
    data class TaskCompleted(val count: Int) : AchievementEvent()
    data class StreakReached(val days: Int) : AchievementEvent()
    data class ExperienceGained(val total: Int) : AchievementEvent()
    data class CourseCompleted(val courseId: String) : AchievementEvent()
    data class StudyTimeReached(val minutes: Int) : AchievementEvent()
}

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao
) : AchievementRepository {

    init {
        // 初始化默认成就数据会在首次使用时调用
    }

    override fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements()
    }

    override fun getUserAchievements(userId: String): Flow<List<UserAchievement>> {
        return achievementDao.getUserAchievements(userId)
    }

    override fun getAchievementsWithProgress(userId: String): Flow<List<AchievementWithProgress>> {
        return combine(
            achievementDao.getAllAchievements(),
            achievementDao.getUserAchievements(userId)
        ) { achievements, userAchievements ->
            val userAchievementMap = userAchievements.associateBy { it.achievementId }
            achievements.map { achievement ->
                val userProgress = userAchievementMap[achievement.id]
                AchievementWithProgress(
                    achievement = achievement,
                    userProgress = userProgress,
                    isUnlocked = userProgress?.isUnlocked == true
                )
            }
        }
    }

    override suspend fun checkAndUnlockAchievements(
        userId: String,
        event: AchievementEvent
    ): List<Achievement> {
        val unlockedAchievements = mutableListOf<Achievement>()

        when (event) {
            is AchievementEvent.TaskCompleted -> {
                val thresholds = listOf(1 to "first_task", 10 to "task_10", 50 to "task_50", 100 to "task_100")
                thresholds.forEach { (threshold, achievementId) ->
                    if (event.count >= threshold) {
                        val achievement = achievementDao.getAchievementById(achievementId)
                        if (achievement != null) {
                            val existing = achievementDao.getUserAchievement(userId, achievementId)
                            if (existing?.isUnlocked != true) {
                                unlockAchievement(userId, achievementId)
                                unlockedAchievements.add(achievement)
                            }
                        }
                    }
                }
            }
            is AchievementEvent.StreakReached -> {
                val thresholds = listOf(7 to "streak_7", 30 to "streak_30", 100 to "streak_100")
                thresholds.forEach { (threshold, achievementId) ->
                    if (event.days >= threshold) {
                        val achievement = achievementDao.getAchievementById(achievementId)
                        if (achievement != null) {
                            val existing = achievementDao.getUserAchievement(userId, achievementId)
                            if (existing?.isUnlocked != true) {
                                unlockAchievement(userId, achievementId)
                                unlockedAchievements.add(achievement)
                            }
                        }
                    }
                }
            }
            is AchievementEvent.ExperienceGained -> {
                // 类似处理经验成就
            }
            is AchievementEvent.CourseCompleted -> {
                // 处理课程完成成就
            }
            is AchievementEvent.StudyTimeReached -> {
                // 处理学习时长成就
            }
        }

        return unlockedAchievements
    }

    override fun getUnlockedCount(userId: String): Flow<Int> {
        return achievementDao.getUnlockedAchievementCount(userId)
    }

    override fun getUserBadges(userId: String): Flow<List<UserBadge>> {
        return achievementDao.getUserBadges(userId)
    }

    private suspend fun unlockAchievement(userId: String, achievementId: String) {
        val userAchievement = UserAchievement(
            id = UUID.randomUUID().toString(),
            achievementId = achievementId,
            userId = userId,
            isUnlocked = true,
            progress = 1f,
            unlockedAt = System.currentTimeMillis()
        )
        achievementDao.insertUserAchievement(userAchievement)
    }

    companion object {
        fun getDefaultAchievements(): List<Achievement> {
            return listOf(
                Achievement(
                    id = "first_task",
                    name = "初出茅庐",
                    description = "完成第一个任务",
                    category = AchievementCategory.TASK,
                    rarity = AchievementRarity.COMMON,
                    iconName = "ic_achievement_first",
                    experienceReward = 50,
                    conditionType = "task_completed",
                    conditionValue = 1,
                    order = 1
                ),
                Achievement(
                    id = "task_10",
                    name = "任务新手",
                    description = "完成10个任务",
                    category = AchievementCategory.TASK,
                    rarity = AchievementRarity.COMMON,
                    iconName = "ic_achievement_task",
                    experienceReward = 100,
                    conditionType = "task_completed",
                    conditionValue = 10,
                    order = 2
                ),
                Achievement(
                    id = "task_50",
                    name = "任务达人",
                    description = "完成50个任务",
                    category = AchievementCategory.TASK,
                    rarity = AchievementRarity.RARE,
                    iconName = "ic_achievement_task",
                    experienceReward = 300,
                    conditionType = "task_completed",
                    conditionValue = 50,
                    order = 3
                ),
                Achievement(
                    id = "task_100",
                    name = "任务大师",
                    description = "完成100个任务",
                    category = AchievementCategory.TASK,
                    rarity = AchievementRarity.EPIC,
                    iconName = "ic_achievement_master",
                    experienceReward = 500,
                    conditionType = "task_completed",
                    conditionValue = 100,
                    order = 4
                ),
                Achievement(
                    id = "streak_7",
                    name = "坚持一周",
                    description = "连续学习7天",
                    category = AchievementCategory.STREAK,
                    rarity = AchievementRarity.COMMON,
                    iconName = "ic_achievement_streak",
                    experienceReward = 100,
                    conditionType = "streak_days",
                    conditionValue = 7,
                    order = 5
                ),
                Achievement(
                    id = "streak_30",
                    name = "月度坚持",
                    description = "连续学习30天",
                    category = AchievementCategory.STREAK,
                    rarity = AchievementRarity.RARE,
                    iconName = "ic_achievement_streak",
                    experienceReward = 500,
                    conditionType = "streak_days",
                    conditionValue = 30,
                    order = 6
                ),
                Achievement(
                    id = "streak_100",
                    name = "百日学霸",
                    description = "连续学习100天",
                    category = AchievementCategory.STREAK,
                    rarity = AchievementRarity.LEGENDARY,
                    iconName = "ic_achievement_legend",
                    experienceReward = 2000,
                    conditionType = "streak_days",
                    conditionValue = 100,
                    order = 7
                ),
                Achievement(
                    id = "math_master",
                    name = "函数大师",
                    description = "完成10个微积分任务",
                    category = AchievementCategory.LEARNING,
                    rarity = AchievementRarity.RARE,
                    iconName = "ic_achievement_math",
                    experienceReward = 300,
                    conditionType = "course_task",
                    conditionValue = 10,
                    order = 8
                )
            )
        }
    }
}
