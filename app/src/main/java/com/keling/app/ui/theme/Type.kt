/**
 * Type.kt
 * 字体排版系统 - 黑体加粗风格
 */

package com.keling.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.keling.app.R

/**
 * 立体阴影效果（用于大标题）
 * 创造深度感和精致感
 */
private fun titleShadow() = Shadow(
    color = Color.Black.copy(alpha = 0.15f),
    offset = Offset(1f, 1f),
    blurRadius = 2f
)

/**
 * 应用字体家族
 * 使用系统黑体（无衬线字体）
 */
private val HeiTiFontFamily = FontFamily.Default

/**
 * 应用字体排版系统
 * 黑体加粗风格
 */
val Typography = Typography(
    // ==================== 展示级标题 ====================
    /** 超大标题 - 用于启动页、重要页面主标题 */
    displayLarge = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp,
        shadow = titleShadow()
    ),

    /** 大型展示标题 - 用于特殊强调 */
    displayMedium = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.3).sp,
        shadow = titleShadow()
    ),

    // ==================== 标题级 ====================
    /** 大标题 - 用于页面主标题 */
    headlineLarge = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.2).sp
    ),

    /** 中标题 - 用于区块标题、卡片标题 */
    headlineMedium = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.1).sp
    ),

    /** 小标题 - 用于列表项标题 */
    headlineSmall = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),

    // ==================== 副标题级 ====================
    /** 大副标题 - 用于重要副标题 */
    titleLarge = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    /** 中副标题 - 默认副标题 */
    titleMedium = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.1.sp
    ),

    /** 小副标题 */
    titleSmall = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.1.sp
    ),

    // ==================== 正文级 ====================
    /** 大正文 - 主要内容 */
    bodyLarge = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    ),

    /** 中正文 - 默认正文 */
    bodyMedium = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),

    /** 小正文 - 辅助文字 */
    bodySmall = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.2.sp
    ),

    // ==================== 标签级 ====================
    /** 大标签 */
    labelLarge = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp
    ),

    /** 中标签 - 默认标签 */
    labelMedium = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),

    /** 小标签 */
    labelSmall = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp
    )
)

/**
 * 特殊用途的文字样式
 */
object SpecialTextStyle {
    /** 数字显示 - 用于计时器、统计数字 */
    val number = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        letterSpacing = (-1).sp
    )

    /** 引用文字 */
    val quote = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    )

    /** 代码文字 */
    val code = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    )
}

/**
 * 游戏化文字样式
 * 黑体加粗风格
 */
object GameTextStyle {
    /** 游戏大标题 - 带阴影效果 */
    val gameTitle = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.3).sp,
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.12f),
            offset = Offset(1.5f, 1.5f),
            blurRadius = 3f
        )
    )

    /** 游戏副标题 */
    val gameSubtitle = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.4.sp
    )

    /** 游戏卡片标题 */
    val cardTitle = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    )

    /** 游戏按钮文字 */
    val buttonText = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.4.sp
    )

    /** 游戏标签文字 */
    val tagText = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp
    )

    /** 游戏奖励数字 */
    val rewardNumber = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        letterSpacing = (-0.5).sp
    )

    /** 游戏统计数字 */
    val statNumber = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = (-0.5).sp
    )

    /** 游戏提示文字 */
    val hint = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    )

    /** 游戏描述文字 */
    val description = TextStyle(
        fontFamily = HeiTiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    )
}