package com.keling.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.keling.app.ui.screens.settings.AccessibilityViewModel

/**
 * 课灵应用主题 - 古风画卷 · 宣纸墨字朱砂
 * 暖米纸底、墨色文字、朱砂点缀、衬线题字、留白与细线
 */

private val KelingColorScheme = lightColorScheme(
    // 主色 - 朱砂
    primary = NeonBlue,
    onPrimary = Color.White,
    primaryContainer = NeonBlueLight.copy(alpha = 0.3f),
    onPrimaryContainer = InkPrimary,

    // 次要色 - 金
    secondary = NeonPurple,
    onSecondary = InkPrimary,
    secondaryContainer = NeonPurpleLight.copy(alpha = 0.3f),
    onSecondaryContainer = InkPrimary,

    // 第三色 - 石青
    tertiary = NeonPink,
    onTertiary = Color.White,
    tertiaryContainer = NeonPinkLight.copy(alpha = 0.25f),
    onTertiaryContainer = InkPrimary,

    // 背景 - 宣纸
    background = PaperBackground,
    onBackground = InkPrimary,

    // 表面
    surface = PaperSurface,
    onSurface = InkPrimary,
    surfaceVariant = PaperSurfaceVariant,
    onSurfaceVariant = InkSecondary,

    // 轮廓
    outline = PaperBorder,
    outlineVariant = PaperBorder,

    // 错误
    error = NeonRed,
    onError = Color.White,
    errorContainer = NeonRedLight.copy(alpha = 0.3f),
    onErrorContainer = InkPrimary,

    // 反转
    inverseSurface = InkPrimary,
    inverseOnSurface = PaperBackground,
    inversePrimary = NeonBlueLight,

    // 其他
    scrim = Color.Black.copy(alpha = 0.4f),
    surfaceTint = NeonBlue
)

/** 高对比度配色：更深墨字与更清晰轮廓，便于视障用户 */
private val HighContrastColorScheme = lightColorScheme(
    primary = NeonBlue,
    onPrimary = Color.White,
    primaryContainer = NeonBlueDark,
    onPrimaryContainer = InkPrimary,
    secondary = NeonPurple,
    onSecondary = InkPrimary,
    secondaryContainer = NeonPurpleDark,
    onSecondaryContainer = InkPrimary,
    tertiary = NeonPink,
    onTertiary = Color.White,
    tertiaryContainer = NeonPinkDark,
    onTertiaryContainer = InkPrimary,
    background = PaperBackground,
    onBackground = InkPrimary,
    surface = PaperSurface,
    onSurface = InkPrimary,
    surfaceVariant = PaperSurfaceVariant,
    onSurfaceVariant = InkSecondary,
    outline = InkPrimary.copy(alpha = 0.8f),
    outlineVariant = InkSecondary,
    error = NeonRed,
    onError = Color.White,
    errorContainer = NeonRedDark,
    onErrorContainer = InkPrimary,
    inverseSurface = InkPrimary,
    inverseOnSurface = PaperBackground,
    inversePrimary = NeonBlueDark,
    scrim = Color.Black.copy(alpha = 0.5f),
    surfaceTint = NeonBlue
)

@Composable
fun KelingTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = KelingColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PaperBackground.toArgb()
            window.navigationBarColor = PaperSurface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            AccessibilityProvider(content = content)
        }
    )
}

@Composable
private fun AccessibilityProvider(
    content: @Composable () -> Unit
) {
    val vm: AccessibilityViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    val density = LocalDensity.current
    val customDensity = Density(density.density, fontScale = state.fontScaleMultiplier())

    CompositionLocalProvider(
        LocalReduceMotion provides state.reduceMotion
    ) {
        CompositionLocalProvider(
            androidx.compose.ui.platform.LocalDensity provides customDensity
        ) {
            if (state.highContrastMode) {
                MaterialTheme(
                    colorScheme = HighContrastColorScheme,
                    typography = Typography,
                    content = content
                )
            } else {
                content()
            }
        }
    }
}
