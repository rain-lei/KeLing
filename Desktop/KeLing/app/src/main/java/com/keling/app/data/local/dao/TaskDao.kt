package com.keling.app.data.local.dao

import androidx.room.*
import com.keling.app.data.model.Task
import com.keling.app.data.model.TaskProgress
import com.keling.app.data.model.TaskStatus
import com.keling.app.data.model.TaskType
import com.keling.app.data.model.TeamTask
import com.keling.app.data.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    
    // Task
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task?
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskByIdFlow(taskId: String): Flow<Task?>
    
    @Query("SELECT * FROM tasks WHERE (userId IS NULL OR userId = :userId) AND status = :status ORDER BY deadline ASC")
    fun getTasksByStatus(status: TaskStatus, userId: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE (userId IS NULL OR userId = :userId) AND type = :type ORDER BY deadline ASC")
    fun getTasksByType(type: TaskType, userId: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE (userId IS NULL OR userId = :userId) AND status IN ('PENDING', 'IN_PROGRESS') ORDER BY deadline ASC")
    fun getActiveTasks(userId: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE courseId = :courseId ORDER BY `order`")
    fun getTasksByCourse(courseId: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE (targetGrade IS NULL OR targetGrade = :grade) AND (userId IS NULL OR userId = :userId) AND status IN ('PENDING', 'IN_PROGRESS') ORDER BY deadline ASC")
    fun getActiveTasksForGrade(grade: String, userId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (targetGrade IS NULL OR targetGrade = :grade) AND (userId IS NULL OR userId = :userId) ORDER BY deadline ASC")
    fun getTasksForGrade(grade: String, userId: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE deadline < :timestamp AND status = 'PENDING'")
    suspend fun getExpiredTasks(timestamp: Long): List<Task>
    
    @Query("SELECT COUNT(*) FROM tasks WHERE (userId IS NULL OR userId = :userId) AND status = 'COMPLETED'")
    fun getCompletedTaskCount(userId: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("UPDATE tasks SET status = :status WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus)
    
    @Query("UPDATE tasks SET status = :status, completedAt = :completedAt WHERE id = :taskId")
    suspend fun completeTask(taskId: String, status: TaskStatus, completedAt: Long)
    
    // TaskProgress
    @Query("SELECT * FROM task_progress WHERE taskId = :taskId AND userId = :userId")
    suspend fun getTaskProgress(taskId: String, userId: String): TaskProgress?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskProgress(progress: TaskProgress)
    
    @Update
    suspend fun updateTaskProgress(progress: TaskProgress)
    
    // TeamTask
    @Query("SELECT * FROM team_tasks WHERE teamId = :teamId")
    fun getTeamTasksByTeam(teamId: String): Flow<List<TeamTask>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamTask(teamTask: TeamTask)

    // StudySession
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySession)

    @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM study_sessions WHERE userId = :userId AND dayKey = :dayKey")
    fun getStudyMinutesForDay(userId: String, dayKey: String): Flow<Int>
}
