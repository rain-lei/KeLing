package com.keling.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.keling.app.ui.screens.achievements.AchievementsScreen
import com.keling.app.ui.screens.ai.AIAssistantScreen
import com.keling.app.ui.screens.campus.CampusPlanetScreen
import com.keling.app.ui.screens.campus.ForumPlanetScreen
import com.keling.app.ui.screens.campus.PracticePlanetScreen
import com.keling.app.ui.screens.courses.CourseDetailScreen
import com.keling.app.ui.screens.courses.CoursesScreen
import com.keling.app.ui.screens.knowledge.KnowledgeGraphScreen
import com.keling.app.ui.screens.knowledge.KnowledgePracticeScreen
import com.keling.app.ui.screens.checkin.CheckInScreen
import com.keling.app.ui.screens.home.HomeScreen
import com.keling.app.ui.screens.login.LoginScreen
import com.keling.app.ui.screens.login.RegisterScreen
import com.keling.app.ui.screens.profile.ProfileScreen
import com.keling.app.ui.screens.settings.AccessibilitySettingsScreen
import com.keling.app.ui.screens.settings.AccountSecurityScreen
import com.keling.app.ui.screens.settings.AboutScreen
import com.keling.app.ui.screens.settings.HelpFeedbackScreen
import com.keling.app.ui.screens.settings.NotificationSettingsScreen
import com.keling.app.ui.screens.settings.PersonalProfileScreen
import com.keling.app.ui.screens.settings.PrivacySettingsScreen
import com.keling.app.ui.screens.settings.SettingsScreen
import com.keling.app.ui.screens.settings.StorageManagementScreen
import com.keling.app.ui.screens.splash.SplashScreen
import com.keling.app.ui.screens.tasks.TaskDetailScreen
import com.keling.app.ui.screens.tasks.TaskExecutionScreen
import com.keling.app.ui.screens.tasks.TasksScreen
import com.keling.app.ui.screens.focus.FocusScreen
import com.keling.app.ui.theme.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun KelingNavHost(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val reduceMotion = LocalReduceMotion.current
    val animDuration = if (reduceMotion) 0 else 300

    // 判断是否显示底部导航栏
    val showBottomBar = bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        containerColor = PaperBackground,
        bottomBar = {
            if (showBottomBar) {
                KelingBottomBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(animDuration)) + slideInHorizontally(
                    initialOffsetX = { 100 },
                    animationSpec = tween(animDuration)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(animDuration))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(animDuration))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(animDuration)) + slideOutHorizontally(
                    targetOffsetX = { 100 },
                    animationSpec = tween(animDuration)
                )
            }
        ) {
            // 启动页
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.CheckIn.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // 登录页
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.CheckIn.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            // 签到页（当天登录/注册后若未签到则先进入，签到后进首页）
            composable(Screen.CheckIn.route) {
                CheckInScreen(
                    onDone = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.CheckIn.route) { inclusive = true }
                        }
                    }
                )
            }

            // 注册页
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.CheckIn.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onBackToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // 首页
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToTask = { taskId ->
                        navController.navigate(Screen.TaskDetail.createRoute(taskId))
                    },
                    onNavigateToCourses = {
                        navController.navigate(Screen.Courses.route)
                    },
                    onNavigateToCourseDetail = { courseId ->
                        navController.navigate(Screen.CourseDetail.createRoute(courseId))
                    },
                    onNavigateToFocus = {
                        navController.navigate(Screen.Focus.route)
                    },
                    onNavigateToAIAssistant = {
                        navController.navigate(Screen.AIAssistant.route)
                    },
                    onNavigateToCampusPlanet = {
                        navController.navigate(Screen.CampusPlanet.route)
                    }
                )
            }

            // 任务列表
            composable(Screen.Tasks.route) {
                TasksScreen(
                    onNavigateToTaskDetail = { taskId ->
                        navController.navigate(Screen.TaskDetail.createRoute(taskId))
                    }
                )
            }

            // 任务详情
            composable(
                route = Screen.TaskDetail.route,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                TaskDetailScreen(
                    taskId = taskId,
                    onBack = { navController.popBackStack() },
                    onStartExecution = {
                        navController.navigate(Screen.TaskExecution.createRoute(taskId))
                    }
                )
            }

            // 执行任务
            composable(
                route = Screen.TaskExecution.route,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                TaskExecutionScreen(
                    taskId = taskId,
                    onBack = { navController.popBackStack() },
                    onCompleted = { navController.popBackStack() }
                )
            }

            // 课程列表
            composable(Screen.Courses.route) {
                CoursesScreen(
                    onNavigateToCourseDetail = { courseId ->
                        navController.navigate(Screen.CourseDetail.createRoute(courseId))
                    }
                )
            }

            // 课程详情
            composable(
                route = Screen.CourseDetail.route,
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                CourseDetailScreen(
                    courseId = courseId,
                    onBack = { navController.popBackStack() },
                    onNavigateToKnowledgeGraph = {
                        navController.navigate(Screen.KnowledgeGraph.createRoute(courseId))
                    }
                )
            }

            // 知识图谱
            composable(
                route = Screen.KnowledgeGraph.route,
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                KnowledgeGraphScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToPractice = { pointId ->
                        navController.navigate(Screen.KnowledgePractice.createRoute(courseId, pointId))
                    }
                )
            }

            // 知识点练习
            composable(
                route = Screen.KnowledgePractice.route,
                arguments = listOf(
                    navArgument("courseId") { type = NavType.StringType },
                    navArgument("pointId") { type = NavType.StringType }
                )
            ) {
                KnowledgePracticeScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // 成就
            composable(Screen.Achievements.route) {
                AchievementsScreen()
            }

            // 云端校园星球
            composable(Screen.CampusPlanet.route) {
                CampusPlanetScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToForum = { navController.navigate(Screen.ForumPlanet.route) },
                    onNavigateToPractice = { navController.navigate(Screen.PracticePlanet.route) }
                )
            }

            // 论坛星球
            composable(Screen.ForumPlanet.route) {
                ForumPlanetScreen(onBack = { navController.popBackStack() })
            }

            // 实践星球
            composable(Screen.PracticePlanet.route) {
                PracticePlanetScreen(onBack = { navController.popBackStack() })
            }

            // 个人中心
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onNavigateToLearningReport = {
                        navController.navigate(Screen.LearningReport.route)
                    }
                )
            }

            // AI助手
            composable(Screen.AIAssistant.route) {
                AIAssistantScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // 专注学习
            composable(Screen.Focus.route) {
                FocusScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // 设置
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToProfile = { navController.navigate(Screen.PersonalProfile.route) },
                    onNavigateToSecurity = { navController.navigate(Screen.AccountSecurity.route) },
                    onNavigateToPrivacy = { navController.navigate(Screen.PrivacySettings.route) },
                    onNavigateToAccessibility = { navController.navigate(Screen.AccessibilitySettings.route) },
                    onNavigateToNotification = { navController.navigate(Screen.NotificationSettings.route) },
                    onNavigateToStorage = { navController.navigate(Screen.StorageManagement.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) },
                    onNavigateToHelp = { navController.navigate(Screen.HelpFeedback.route) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 个人资料
            composable(Screen.PersonalProfile.route) {
                PersonalProfileScreen(onBack = { navController.popBackStack() })
            }

            // 账户安全
            composable(Screen.AccountSecurity.route) {
                AccountSecurityScreen(onBack = { navController.popBackStack() })
            }

            // 隐私设置
            composable(Screen.PrivacySettings.route) {
                PrivacySettingsScreen(onBack = { navController.popBackStack() })
            }

            // 无障碍设置
            composable(Screen.AccessibilitySettings.route) {
                AccessibilitySettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // 通知设置
            composable(Screen.NotificationSettings.route) {
                NotificationSettingsScreen(onBack = { navController.popBackStack() })
            }

            // 存储管理
            composable(Screen.StorageManagement.route) {
                StorageManagementScreen(onBack = { navController.popBackStack() })
            }

            // 关于课灵
            composable(Screen.About.route) {
                AboutScreen(onBack = { navController.popBackStack() })
            }

            // 帮助与反馈
            composable(Screen.HelpFeedback.route) {
                HelpFeedbackScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun KelingBottomBar(
    navController: NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    NavigationBar(
        containerColor = PaperSurface,
        contentColor = InkPrimary
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) screen.selectedIcon!! else screen.unselectedIcon!!,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonBlue,
                    selectedTextColor = NeonBlue,
                    unselectedIconColor = InkSecondary,
                    unselectedTextColor = InkSecondary,
                    indicatorColor = NeonBlue.copy(alpha = 0.15f)
                )
            )
        }
    }
}
