package com.keling.app.util

import java.util.Calendar

/**
 * 课程状态
 */
enum class CourseStatus {
    NOT_STARTED, // 未开始
    ONGOING,     // 上课中
    FINISHED     // 已下课
}

/**
 * 将 HH:mm 格式的时间转换为从 00:00 起算的分钟数
 */
fun parseTimeToMinutes(time: String): Int {
    return try {
        val parts = time.split(":")
        if (parts.size != 2) return 0
        val hour = parts[0].toIntOrNull() ?: 0
        val minute = parts[1].toIntOrNull() ?: 0
        hour * 60 + minute
    } catch (_: Exception) {
        0
    }
}

/**
 * 获取当前时间（当天 00:00 起）的分钟数
 */
fun currentMinutesOfDay(): Int {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return hour * 60 + minute
}

/**
 * 根据开始/结束时间与当前时间判断课程状态
 */
fun getCourseStatus(
    startTime: String,
    endTime: String,
    nowMinutesOfDay: Int = currentMinutesOfDay()
): CourseStatus {
    val start = parseTimeToMinutes(startTime)
    val end = parseTimeToMinutes(endTime)

    return when {
        nowMinutesOfDay < start -> CourseStatus.NOT_STARTED
        nowMinutesOfDay in start..end -> CourseStatus.ONGOING
        else -> CourseStatus.FINISHED
    }
}

/**
 * 是否处于“上课中”状态
 */
fun isCourseOngoing(
    startTime: String,
    endTime: String,
    nowMinutesOfDay: Int = currentMinutesOfDay()
): Boolean {
    return getCourseStatus(startTime, endTime, nowMinutesOfDay) == CourseStatus.ONGOING
}

