package com.keling.app.data.repository

import android.content.Context
import com.keling.app.data.local.dao.UserDao
import com.keling.app.data.model.DashboardData
import com.keling.app.data.model.User
import com.keling.app.data.model.UserProfile
import com.keling.app.data.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject

/** 未注册：登录时账号不存在或未设置密码，需先注册 */
const val ERROR_NOT_REGISTERED = "未注册，请先注册"

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun getUserById(userId: String): User?
    /** 仅当已注册且账号密码匹配时登录成功；未注册返回 failure(ERROR_NOT_REGISTERED) */
    suspend fun login(usernameOrPhone: String, password: String): Result<User>
    /** 使用手机号+密码注册（学生端），预留短信验证码时在调用前先校验验证码 */
    suspend fun register(phone: String, password: String, realName: String): Result<User>
    /** 使用学号+密码注册（学生端），无需短信 */
    suspend fun registerByStudentId(studentId: String, password: String, realName: String): Result<User>
    suspend fun logout()
    suspend fun updateUser(user: User)
    suspend fun getDashboardData(userId: String): DashboardData
    fun getUserProfile(userId: String): Flow<UserProfile?>
}

private const val PREFS_AUTH = "keling_auth"
private const val KEY_CURRENT_USER_ID = "current_user_id"

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao
) : UserRepository {

    private val prefs = context.getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE)

    private val currentUserId: String?
        get() = prefs.getString(KEY_CURRENT_USER_ID, null)

    private fun setCurrentUserId(id: String?) {
        prefs.edit().apply {
            if (id != null) putString(KEY_CURRENT_USER_ID, id)
            else remove(KEY_CURRENT_USER_ID)
            apply()
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return currentUserId?.let { userDao.getUserByIdFlow(it) }
            ?: kotlinx.coroutines.flow.flowOf(null)
    }

    override suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }

    override suspend fun login(usernameOrPhone: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByUsername(usernameOrPhone)
                ?: userDao.getUserByPhone(usernameOrPhone)
            when {
                user == null -> Result.failure(Exception(ERROR_NOT_REGISTERED))
                user.passwordHash == null -> Result.failure(Exception(ERROR_NOT_REGISTERED))
                user.passwordHash != hashPassword(password) -> Result.failure(Exception("密码错误"))
                else -> {
                    setCurrentUserId(user.id)
                    userDao.updateLastLogin(user.id, System.currentTimeMillis())
                    Result.success(user)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(phone: String, password: String, realName: String): Result<User> {
        return try {
            if (phone.isBlank()) return Result.failure(Exception("请输入手机号"))
            if (password.length < 6) return Result.failure(Exception("密码至少6位"))
            val existing = userDao.getUserByPhone(phone)
            if (existing != null) return Result.failure(Exception("该手机号已注册"))
            val user = User(
                id = "user_${System.currentTimeMillis()}",
                username = phone,
                realName = realName.ifBlank { "学习者" },
                role = UserRole.STUDENT,
                schoolId = null,
                classId = null,
                grade = null,
                email = null,
                phone = phone,
                passwordHash = hashPassword(password)
            )
            userDao.insertUser(user)
            setCurrentUserId(user.id)
            userDao.updateLastLogin(user.id, System.currentTimeMillis())
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerByStudentId(studentId: String, password: String, realName: String): Result<User> {
        return try {
            if (studentId.isBlank()) return Result.failure(Exception("请输入学号"))
            if (password.length < 6) return Result.failure(Exception("密码至少6位"))
            val existing = userDao.getUserByUsername(studentId.trim())
            if (existing != null) return Result.failure(Exception("该学号已注册"))
            val user = User(
                id = "user_${System.currentTimeMillis()}",
                username = studentId.trim(),
                realName = realName.ifBlank { "学习者" },
                role = UserRole.STUDENT,
                schoolId = null,
                classId = null,
                grade = null,
                email = null,
                phone = null,
                passwordHash = hashPassword(password)
            )
            userDao.insertUser(user)
            setCurrentUserId(user.id)
            userDao.updateLastLogin(user.id, System.currentTimeMillis())
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override suspend fun logout() {
        setCurrentUserId(null)
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    override suspend fun getDashboardData(userId: String): DashboardData {
        // 返回模拟数据，实际应从数据库计算
        return DashboardData(
            learningProgress = 0.68f,
            taskCompletion = 15,
            skillGrowth = mapOf(
                "数学" to 0.75f,
                "编程" to 0.82f,
                "英语" to 0.60f,
                "物理" to 0.55f
            ),
            achievements = emptyList(),
            todayStudyMinutes = 45,
            weeklyProgress = listOf(0.8f, 0.6f, 0.9f, 0.7f, 0.85f, 0.5f, 0.0f)
        )
    }

    override fun getUserProfile(userId: String): Flow<UserProfile?> {
        return userDao.getUserByIdFlow(userId).map { user ->
            user?.let {
                UserProfile(
                    user = it,
                    level = 12,
                    experience = 2450,
                    totalExperience = 15000,
                    streak = 7,
                    totalStudyMinutes = 1250,
                    taskCompletedCount = 45,
                    achievementCount = 12,
                    rankInClass = 5,
                    rankInSchool = 128
                )
            }
        }
    }

}
