package com.keling.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.keling.app.data.model.CheckIn
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: CheckIn)

    @Query("SELECT COUNT(*) FROM check_ins WHERE userId = :userId AND dateKey = :dateKey")
    suspend fun getCheckInCount(userId: String, dateKey: String): Int

    @Query("SELECT COUNT(*) FROM check_ins WHERE userId = :userId AND dateKey = :dateKey")
    fun getCheckInCountFlow(userId: String, dateKey: String): Flow<Int>

    /** 获取用户所有签到日期（dateKey），按日期倒序 */
    @Query("SELECT dateKey FROM check_ins WHERE userId = :userId ORDER BY dateKey DESC")
    fun getCheckInDates(userId: String): Flow<List<String>>
}
