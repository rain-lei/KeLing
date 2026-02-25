package com.keling.app.data.local.dao

import androidx.room.*
import com.keling.app.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    
    // Achievement
    @Query("SELECT * FROM achievements ORDER BY category, `order`")
    fun getAllAchievements(): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE category = :category ORDER BY `order`")
    fun getAchievementsByCategory(category: AchievementCategory): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE id = :achievementId")
    suspend fun getAchievementById(achievementId: String): Achievement?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)
    
    // UserAchievement
    @Query("SELECT * FROM user_achievements WHERE userId = :userId")
    fun getUserAchievements(userId: String): Flow<List<UserAchievement>>
    
    @Query("SELECT * FROM user_achievements WHERE userId = :userId AND isUnlocked = 1")
    fun getUnlockedAchievements(userId: String): Flow<List<UserAchievement>>
    
    @Query("SELECT * FROM user_achievements WHERE userId = :userId AND achievementId = :achievementId")
    suspend fun getUserAchievement(userId: String, achievementId: String): UserAchievement?
    
    @Query("SELECT COUNT(*) FROM user_achievements WHERE userId = :userId AND isUnlocked = 1")
    fun getUnlockedAchievementCount(userId: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAchievement(userAchievement: UserAchievement)
    
    @Update
    suspend fun updateUserAchievement(userAchievement: UserAchievement)
    
    // Badge
    @Query("SELECT * FROM badges ORDER BY tier, category")
    fun getAllBadges(): Flow<List<Badge>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<Badge>)
    
    // UserBadge
    @Query("SELECT * FROM user_badges WHERE userId = :userId")
    fun getUserBadges(userId: String): Flow<List<UserBadge>>
    
    @Query("SELECT * FROM user_badges WHERE userId = :userId AND isEquipped = 1")
    fun getEquippedBadges(userId: String): Flow<List<UserBadge>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserBadge(userBadge: UserBadge)
    
    @Query("UPDATE user_badges SET isEquipped = :isEquipped WHERE id = :id")
    suspend fun updateBadgeEquipped(id: String, isEquipped: Boolean)
}
