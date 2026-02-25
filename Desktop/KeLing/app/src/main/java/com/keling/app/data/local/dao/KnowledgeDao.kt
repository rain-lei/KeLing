package com.keling.app.data.local.dao

import androidx.room.*
import com.keling.app.data.model.KnowledgePoint
import com.keling.app.data.model.KnowledgeRelation
import com.keling.app.data.model.LearningRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeDao {
    
    // KnowledgePoint
    @Query("SELECT * FROM knowledge_points WHERE courseId = :courseId")
    fun getKnowledgePointsByCourse(courseId: String): Flow<List<KnowledgePoint>>
    
    @Query("SELECT * FROM knowledge_points WHERE id = :pointId")
    suspend fun getKnowledgePointById(pointId: String): KnowledgePoint?
    
    @Query("SELECT * FROM knowledge_points WHERE masteryLevel < :threshold ORDER BY masteryLevel ASC")
    fun getWeakKnowledgePoints(threshold: Float = 0.6f): Flow<List<KnowledgePoint>>
    
    @Query("SELECT * FROM knowledge_points WHERE masteryLevel >= :threshold ORDER BY masteryLevel DESC")
    fun getStrongKnowledgePoints(threshold: Float = 0.8f): Flow<List<KnowledgePoint>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledgePoint(point: KnowledgePoint)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledgePoints(points: List<KnowledgePoint>)
    
    @Update
    suspend fun updateKnowledgePoint(point: KnowledgePoint)
    
    @Query("UPDATE knowledge_points SET masteryLevel = :level WHERE id = :pointId")
    suspend fun updateMasteryLevel(pointId: String, level: Float)
    
    // KnowledgeRelation
    @Query("SELECT * FROM knowledge_relations WHERE fromPointId = :pointId")
    fun getRelationsFrom(pointId: String): Flow<List<KnowledgeRelation>>
    
    @Query("SELECT * FROM knowledge_relations WHERE toPointId = :pointId")
    fun getRelationsTo(pointId: String): Flow<List<KnowledgeRelation>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(relation: KnowledgeRelation)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelations(relations: List<KnowledgeRelation>)
    
    // LearningRecord
    @Query("SELECT * FROM learning_records WHERE userId = :userId ORDER BY recordedAt DESC")
    fun getLearningRecords(userId: String): Flow<List<LearningRecord>>
    
    @Query("SELECT * FROM learning_records WHERE userId = :userId AND knowledgePointId = :pointId ORDER BY recordedAt DESC")
    fun getLearningRecordsForPoint(userId: String, pointId: String): Flow<List<LearningRecord>>
    
    @Query("SELECT AVG(CASE WHEN isCorrect THEN 1.0 ELSE 0.0 END) FROM learning_records WHERE userId = :userId AND knowledgePointId = :pointId")
    suspend fun getAccuracyForPoint(userId: String, pointId: String): Float?
    
    @Query("SELECT AVG(CASE WHEN isCorrect THEN 1.0 ELSE 0.0 END) FROM learning_records WHERE userId = :userId")
    suspend fun getOverallAccuracy(userId: String): Float?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningRecord(record: LearningRecord)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearningRecords(records: List<LearningRecord>)
}
