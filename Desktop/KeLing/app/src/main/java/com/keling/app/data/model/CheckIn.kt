package com.keling.app.data.model

import androidx.room.Entity

/**
 * 签到记录：每个用户每天一条，用于计算连续学习天数
 */
@Entity(tableName = "check_ins", primaryKeys = ["userId", "dateKey"])
data class CheckIn(
    val userId: String,
    /** 日期键，如 2026-02-18 */
    val dateKey: String,
    val createdAt: Long = System.currentTimeMillis()
)
