package com.keling.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 知识点
 */
@Entity(tableName = "knowledge_points")
data class KnowledgePoint(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String? = null,
    val courseId: String,
    val chapterId: String? = null,
    val difficulty: Int = 1,                // 难度等级 1-5
    val importance: Int = 1,                // 重要程度 1-5
    val masteryLevel: Float = 0f            // 掌握程度 0-1
)

/**
 * 知识点关系
 */
@Entity(tableName = "knowledge_relations")
data class KnowledgeRelation(
    @PrimaryKey
    val id: String,
    val fromPointId: String,
    val toPointId: String,
    val relationType: RelationType,
    val weight: Float = 1f                  // 关系权重
)

/**
 * 关系类型
 */
enum class RelationType {
    PREREQUISITE,   // 前置知识
    CONTAINS,       // 包含
    RELATED,        // 相关
    EXTENDS         // 扩展
}

/**
 * 学情诊断报告
 */
data class LearningReport(
    val userId: String,
    val courseId: String,
    val accuracy: Float,                    // 正确率
    val speed: Float,                       // 做题速度(题/分钟)
    val knowledgeGaps: List<KnowledgePoint>,// 薄弱知识点
    val strongPoints: List<KnowledgePoint>, // 掌握良好的知识点
    val suggestions: List<String>,          // 学习建议
    val generatedAt: Long = System.currentTimeMillis()
)

/**
 * 学习记录
 */
@Entity(tableName = "learning_records")
data class LearningRecord(
    @PrimaryKey
    val id: String,
    val userId: String,
    val knowledgePointId: String,
    val questionId: String? = null,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int,
    val attemptCount: Int = 1,
    val recordedAt: Long = System.currentTimeMillis()
)
