package com.keling.app.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

/**
 * 古风·画卷感 留白与边距
 * 画边略宽，内容不顶满，形成卷轴边距感
 */
object KelingSpacing {
    /** 水平画边 - 用于页面内容左右留白 */
    val horizontalPage = 20.dp
    /** 垂直画边 - 用于区块上下留白 */
    val verticalPage = 16.dp
    /** 页面内容统一 padding（画边感） */
    val pagePadding = PaddingValues(
        horizontal = horizontalPage,
        vertical = verticalPage
    )
}
