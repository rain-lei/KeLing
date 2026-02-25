package com.keling.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 古风敦煌配色 · 修仙手游风格
 * 墨色、宣纸绢、朱砂、金、石青石绿、赭石
 */

// 主色 - 朱砂（主要操作、强调）
val NeonBlue = Color(0xFFB83D34)
val NeonBlueDark = Color(0xFF9E3028)
val NeonBlueLight = Color(0xFFD45A4D)

// 次要色 - 金（成就/高亮/次级强调）
val NeonPurple = Color(0xFFC9A227)
val NeonPurpleDark = Color(0xFFA8861F)
val NeonPurpleLight = Color(0xFFE8C547)

// 辅助色 - 石青（信息/冷静）
val NeonPink = Color(0xFF2E5C8A)
val NeonPinkDark = Color(0xFF1E3A5F)
val NeonPinkLight = Color(0xFF4A7BA8)

// 辅助色 - 石绿（成功/完成）
val NeonGreen = Color(0xFF3D6B4F)
val NeonGreenDark = Color(0xFF2D5A45)
val NeonGreenLight = Color(0xFF5A8F6A)

// 警告色 - 赭石
val NeonOrange = Color(0xFFB8860B)
val NeonOrangeDark = Color(0xFF8B6914)
val NeonOrangeLight = Color(0xFFD4A82E)

// 错误色 - 深朱
val NeonRed = Color(0xFF9E3028)
val NeonRedDark = Color(0xFF7A261F)
val NeonRedLight = Color(0xFFC43D34)

// 金色（成就/奖励）- 与次要金区分更亮
val NeonGold = Color(0xFFD4AF37)
val NeonGoldDark = Color(0xFFAA8B2C)
val NeonGoldLight = Color(0xFFE8C547)

// 深色背景 - 墨色/深绢
val DarkBackground = Color(0xFF1A1614)
val DarkSurface = Color(0xFF252019)
val DarkSurfaceVariant = Color(0xFF2D2825)
val DarkCard = Color(0xFF2D2825)
val DarkBorder = Color(0xFF4A423C)

// 文字颜色 - 绢白/墨
val TextPrimary = Color(0xFFF0E9DC)
val TextSecondary = Color(0xFFB8A898)
val TextTertiary = Color(0xFF8B8178)
val TextDisabled = Color(0xFF6B6259)

// 渐变色 - 朱砂到金
val GradientStart = NeonBlue
val GradientMiddle = NeonPurple
val GradientEnd = NeonGold

// 发光/光晕（低透明度）
val GlowBlue = Color(0x40B83D34)
val GlowPurple = Color(0x40C9A227)
val GlowPink = Color(0x402E5C8A)
val GlowGreen = Color(0x403D6B4F)

// 经验值等级 - 古风金属
val ExpBronze = Color(0xFFB8860B)
val ExpSilver = Color(0xFFA0A0A0)
val ExpGold = Color(0xFFD4AF37)
val ExpPlatinum = Color(0xFFE0D8C8)
val ExpDiamond = Color(0xFF2E5C8A)

// 任务难度
val DifficultyEasy = NeonGreen
val DifficultyMedium = NeonOrange
val DifficultyHard = NeonRed
val DifficultyExpert = NeonPurple

// 全应用 · 浅色画卷（宣纸感）
val PaperBackground = Color(0xFFF8F4ED)
val PaperSurface = Color(0xFFF0E9DC)
val PaperSurfaceVariant = Color(0xFFE8E0D4)
val InkPrimary = Color(0xFF1A1614)
val InkSecondary = Color(0xFF4A423C)
val InkMuted = Color(0xFF8B8178)
/** 画卷细线/边框（墨色淡） */
val PaperBorder = Color(0xFF4A423C).copy(alpha = 0.35f)
