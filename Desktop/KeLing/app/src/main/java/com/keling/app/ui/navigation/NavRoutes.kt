package com.keling.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 应用导航路由定义
 */
sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    // 登录相关
    object Splash : Screen("splash", "启动页")
    object Login : Screen("login", "登录")
    object Register : Screen("register", "注册")
    /** 签到页：当天成功登录后若未签到则先进入此页，签到后进入首页 */
    object CheckIn : Screen("check_in", "每日签到")

    // 主要Tab页面
    object Home : Screen(
        "home", "首页",
        Icons.Filled.Home, Icons.Outlined.Home
    )
    object Tasks : Screen(
        "tasks", "任务",
        Icons.Filled.Assignment, Icons.Outlined.Assignment
    )
    object Courses : Screen(
        "courses", "课程",
        Icons.Filled.MenuBook, Icons.Outlined.MenuBook
    )
    object Achievements : Screen(
        "achievements", "成就",
        Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents
    )
    object Profile : Screen(
        "profile", "我的",
        Icons.Filled.Person, Icons.Outlined.Person
    )
    
    // 详情页面
    object TaskDetail : Screen("task/{taskId}", "任务详情") {
        fun createRoute(taskId: String) = "task/$taskId"
    }
    object TaskExecution : Screen("task/{taskId}/execute", "执行任务") {
        fun createRoute(taskId: String) = "task/$taskId/execute"
    }
    object CourseDetail : Screen("course/{courseId}", "课程详情") {
        fun createRoute(courseId: String) = "course/$courseId"
    }
    object ChapterDetail : Screen("chapter/{chapterId}", "章节详情") {
        fun createRoute(chapterId: String) = "chapter/$chapterId"
    }
    object AchievementDetail : Screen("achievement/{achievementId}", "成就详情") {
        fun createRoute(achievementId: String) = "achievement/$achievementId"
    }
    
    // AI助手
    object AIAssistant : Screen("ai_assistant", "课灵助手")
    // 专注学习
    object Focus : Screen("focus", "专注学习")

    // 云端校园星球
    object CampusPlanet : Screen("campus_planet", "云端校园星球")
    object ForumPlanet : Screen("campus_forum", "论坛星球")
    object PracticePlanet : Screen("campus_practice", "实践星球")
    
    // 设置相关
    object Settings : Screen("settings", "设置")
    object PersonalProfile : Screen("settings/profile", "个人资料")
    object AccountSecurity : Screen("settings/security", "账户安全")
    object PrivacySettings : Screen("settings/privacy", "隐私设置")
    object AccessibilitySettings : Screen("settings/accessibility", "无障碍设置")
    object NotificationSettings : Screen("settings/notification", "通知设置")
    object StorageManagement : Screen("settings/storage", "存储管理")
    object About : Screen("settings/about", "关于课灵")
    object HelpFeedback : Screen("settings/help", "帮助与反馈")
    
    // 知识图谱
    object KnowledgeGraph : Screen("knowledge_graph/{courseId}", "知识图谱") {
        fun createRoute(courseId: String) = "knowledge_graph/$courseId"
    }
    // 知识点练习
    object KnowledgePractice : Screen("knowledge_practice/{courseId}/{pointId}", "知识点练习") {
        fun createRoute(courseId: String, pointId: String) = "knowledge_practice/$courseId/$pointId"
    }
    
    // 学情报告
    object LearningReport : Screen("learning_report", "学情报告")
}

/**
 * 底部导航栏项目
 */
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Tasks,
    Screen.Courses,
    Screen.Achievements,
    Screen.Profile
)
