package com.keling.app.data.remote

import com.keling.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 教务系统API接口
 */
interface EducationApiService {
    
    @GET("api/timetable")
    suspend fun getTimetable(
        @Query("student_id") studentId: String,
        @Query("semester") semester: String
    ): Response<TimetableResponse>
    
    @GET("api/courses")
    suspend fun getCourses(
        @Query("student_id") studentId: String
    ): Response<CoursesResponse>
    
    @GET("api/grades")
    suspend fun getGrades(
        @Query("student_id") studentId: String,
        @Query("semester") semester: String? = null
    ): Response<GradesResponse>
}

/**
 * 课灵后端API接口
 */
interface KelingApiService {
    
    // 用户相关
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
    
    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>
    
    @GET("user/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>
    
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<UserProfileResponse>
    
    // 任务相关
    @GET("tasks")
    suspend fun getTasks(
        @Header("Authorization") token: String,
        @Query("type") type: String? = null,
        @Query("status") status: String? = null
    ): Response<TasksResponse>
    
    @GET("tasks/{taskId}")
    suspend fun getTaskDetail(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String
    ): Response<TaskDetailResponse>
    
    @POST("tasks/{taskId}/complete")
    suspend fun completeTask(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String,
        @Body request: CompleteTaskRequest
    ): Response<CompleteTaskResponse>
    
    @POST("tasks/{taskId}/progress")
    suspend fun updateTaskProgress(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String,
        @Body request: UpdateProgressRequest
    ): Response<Unit>
    
    // 成就相关
    @GET("achievements")
    suspend fun getAchievements(
        @Header("Authorization") token: String
    ): Response<AchievementsResponse>
    
    @GET("achievements/user")
    suspend fun getUserAchievements(
        @Header("Authorization") token: String
    ): Response<UserAchievementsResponse>
    
    // 知识图谱
    @GET("knowledge-graph/{courseId}")
    suspend fun getKnowledgeGraph(
        @Header("Authorization") token: String,
        @Path("courseId") courseId: String
    ): Response<KnowledgeGraphResponse>
    
    // 学情分析
    @GET("learning-report")
    suspend fun getLearningReport(
        @Header("Authorization") token: String,
        @Query("courseId") courseId: String? = null
    ): Response<LearningReportResponse>
    
    // AI助手
    @POST("ai/chat")
    suspend fun sendChatMessage(
        @Header("Authorization") token: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>
    
    // 同步数据
    @POST("sync/education-system")
    suspend fun syncFromEducationSystem(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): Response<SyncResponse>
}

// Request/Response 数据类
data class LoginRequest(
    val username: String,
    val password: String,
    val role: String,
    val deviceId: String? = null
)

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val user: User,
    val expiresIn: Long
)

data class UpdateProfileRequest(
    val realName: String? = null,
    val avatarUrl: String? = null,
    val privacyLevel: String? = null
)

data class UserProfileResponse(
    val user: User,
    val profile: UserProfile
)

data class TimetableResponse(
    val scheduleItems: List<ScheduleItem>,
    val semester: String,
    val weekCount: Int
)

data class CoursesResponse(
    val courses: List<Course>
)

data class GradesResponse(
    val grades: List<GradeItem>
)

data class GradeItem(
    val courseId: String,
    val courseName: String,
    val score: Float,
    val credits: Float,
    val semester: String
)

data class TasksResponse(
    val tasks: List<Task>,
    val total: Int
)

data class TaskDetailResponse(
    val task: Task,
    val relatedMaterials: List<Material>
)

data class CompleteTaskRequest(
    val score: Float? = null,
    val timeSpentMinutes: Int? = null
)

data class CompleteTaskResponse(
    val experienceGained: Int,
    val coinsGained: Int,
    val newAchievements: List<Achievement>,
    val levelUp: Boolean,
    val newLevel: Int?
)

data class UpdateProgressRequest(
    val progress: Float,
    val timeSpentMinutes: Int
)

data class AchievementsResponse(
    val achievements: List<Achievement>
)

data class UserAchievementsResponse(
    val userAchievements: List<UserAchievement>,
    val totalCount: Int,
    val unlockedCount: Int
)

data class KnowledgeGraphResponse(
    val nodes: List<KnowledgePoint>,
    val relations: List<KnowledgeRelation>
)

data class LearningReportResponse(
    val report: LearningReport
)

data class ChatRequest(
    val message: String,
    val context: String? = null,
    val mode: String = "text" // text, voice
)

data class ChatResponse(
    val reply: String,
    val suggestions: List<String>? = null
)

data class SyncRequest(
    val studentId: String,
    val syncType: String = "all" // all, timetable, grades
)

data class SyncResponse(
    val success: Boolean,
    val message: String,
    val syncedAt: Long
)
