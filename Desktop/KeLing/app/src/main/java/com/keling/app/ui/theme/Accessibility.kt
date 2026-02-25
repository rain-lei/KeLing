package com.keling.app.ui.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * 是否开启「减少动画」：为 true 时导航等动画应使用 0 时长或跳过动画。
 */
val LocalReduceMotion = compositionLocalOf { false }
