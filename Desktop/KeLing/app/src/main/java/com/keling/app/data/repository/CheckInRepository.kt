package com.keling.app.data.repository

import com.keling.app.data.local.dao.CheckInDao
import com.keling.app.data.model.CheckIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

interface CheckInRepository {
    /** 今日是否已签到 */
    suspend fun isCheckedInToday(userId: String): Boolean
    fun isCheckedInTodayFlow(userId: String): Flow<Boolean>
    /** 执行签到，若今日已签则无操作 */
    suspend fun checkIn(userId: String)
    /** 连续签到天数（从今天往前数连续有签到的天数） */
    fun getStreak(userId: String): Flow<Int>
}

class CheckInRepositoryImpl @Inject constructor(
    private val checkInDao: CheckInDao
) : CheckInRepository {

    override suspend fun isCheckedInToday(userId: String): Boolean {
        val today = currentDateKey()
        return checkInDao.getCheckInCount(userId, today) > 0
    }

    override fun isCheckedInTodayFlow(userId: String): Flow<Boolean> {
        val today = currentDateKey()
        return checkInDao.getCheckInCountFlow(userId, today).map { it > 0 }
    }

    override suspend fun checkIn(userId: String) {
        val today = currentDateKey()
        if (checkInDao.getCheckInCount(userId, today) > 0) return
        checkInDao.insert(CheckIn(userId = userId, dateKey = today))
    }

    override fun getStreak(userId: String): Flow<Int> {
        return checkInDao.getCheckInDates(userId).map { dates ->
            val set = dates.toSet()
            var current = currentDateKey()
            var count = 0
            while (set.contains(current)) {
                count++
                current = previousDateKey(current)
            }
            count
        }
    }

    private fun currentDateKey(): String {
        val cal = Calendar.getInstance()
        return formatDateKey(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    private fun previousDateKey(dateKey: String): String {
        val parts = dateKey.split("-")
        if (parts.size != 3) return dateKey
        val y = parts[0].toIntOrNull() ?: return dateKey
        val m = parts[1].toIntOrNull() ?: return dateKey
        val d = parts[2].toIntOrNull() ?: return dateKey
        val cal = Calendar.getInstance()
        cal.set(y, m - 1, d)
        cal.add(Calendar.DAY_OF_MONTH, -1)
        return formatDateKey(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    private fun formatDateKey(year: Int, month: Int, day: Int): String =
        String.format("%04d-%02d-%02d", year, month, day)
}
