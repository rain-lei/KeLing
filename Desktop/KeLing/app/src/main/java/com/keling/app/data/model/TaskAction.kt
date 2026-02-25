package com.keling.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * 可执行任务的行为类型（决定如何执行与检测完成）
 */
enum class TaskActionType {
    /** 测验：选择题/判断题，自动判题 */
    QUIZ,
    /** 阅读：达到时长或页数后确认 */
    READING,
    /** 习题：完成指定题号，逐项勾选 */
    EXERCISE,
    /** 视频：观看达到时长后确认 */
    VIDEO,
    /** 背诵/默写：自检后确认完成 */
    MEMORIZATION
}

/**
 * 测验题目
 */
data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    @SerializedName("correct_index") val correctIndex: Int,
    val explanation: String? = null
)

/**
 * 测验类任务载荷
 */
data class QuizPayload(
    val questions: List<QuizQuestion>,
    /** 通过阈值 0~1，如 0.8 表示 80% 正确即通过 */
    val passRate: Float = 0.8f
)

/**
 * 阅读类任务载荷
 */
data class ReadingPayload(
    val durationMinutes: Int,
    val pageRange: String? = null,
    val bookOrChapter: String? = null
)

/**
 * 习题类任务载荷
 */
data class ExercisePayload(
    val subject: String,
    val chapter: String,
    /** 题号描述，如 "1-5" 或 "1,3,5,7" */
    val exerciseIds: String,
    val totalCount: Int
)

/**
 * 视频类任务载荷
 */
data class VideoPayload(
    val durationMinutes: Int,
    val title: String? = null,
    val url: String? = null
)

/**
 * 背诵/默写类任务载荷
 */
data class MemorizationPayload(
    val itemCount: Int,
    val itemDescription: String,
    val hint: String? = null
)

/**
 * 任务完成校验结果
 */
sealed class TaskCompletionResult {
    data class Success(val score: Float?, val feedback: String) : TaskCompletionResult()
    data class Failure(val reason: String) : TaskCompletionResult()
}
