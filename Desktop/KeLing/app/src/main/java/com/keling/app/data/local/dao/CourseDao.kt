package com.keling.app.data.local.dao

import androidx.room.*
import com.keling.app.data.model.Chapter
import com.keling.app.data.model.Course
import com.keling.app.data.model.Material
import com.keling.app.data.model.ScheduleItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    
    // Course
    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: String): Course?
    
    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getCourseByIdFlow(courseId: String): Flow<Course?>
    
    @Query("SELECT * FROM courses WHERE semester = :semester")
    fun getCoursesBySemester(semester: String): Flow<List<Course>>
    
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)
    
    @Update
    suspend fun updateCourse(course: Course)
    
    @Delete
    suspend fun deleteCourse(course: Course)
    
    // Schedule
    @Query("SELECT * FROM schedule_items WHERE dayOfWeek = :dayOfWeek ORDER BY startTime")
    fun getScheduleByDay(dayOfWeek: Int): Flow<List<ScheduleItem>>
    
    @Query("SELECT * FROM schedule_items ORDER BY dayOfWeek, startTime")
    fun getAllScheduleItems(): Flow<List<ScheduleItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItem(item: ScheduleItem)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItems(items: List<ScheduleItem>)
    
    @Query("DELETE FROM schedule_items")
    suspend fun deleteAllScheduleItems()
    
    @Query("DELETE FROM schedule_items WHERE id = :id")
    suspend fun deleteScheduleItemById(id: String)
    
    // Chapter
    @Query("SELECT * FROM chapters WHERE courseId = :courseId ORDER BY orderIndex")
    fun getChaptersByCourse(courseId: String): Flow<List<Chapter>>
    
    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    suspend fun getChapterById(chapterId: String): Chapter?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<Chapter>)
    
    @Update
    suspend fun updateChapter(chapter: Chapter)
    
    // Material
    @Query("SELECT * FROM materials WHERE courseId = :courseId")
    fun getMaterialsByCourse(courseId: String): Flow<List<Material>>
    
    @Query("SELECT * FROM materials WHERE chapterId = :chapterId")
    fun getMaterialsByChapter(chapterId: String): Flow<List<Material>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: Material)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<Material>)
}
