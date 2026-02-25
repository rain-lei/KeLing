package com.keling.app.data.model

/**
 * 预置的日常任务模板：围绕大学生健康学习生活的一天设计
 */
data class DailyTaskTemplate(
    val id: String,
    val title: String,
    val description: String,
    val exp: Int,
    val coin: Int = 0,
    val estimatedMinutes: Int = 20
)

/** 默认日常任务模板列表，供 TaskRepository 生成当日固定任务 */
val defaultDailyTemplates = listOf(
    DailyTaskTemplate(
        id = "daily_preview",
        title = "课程预习",
        description = "根据明日课表预习 1~2 门课程，粗略浏览教材或课件，标记不懂的地方。",
        exp = 40,
        estimatedMinutes = 30
    ),
    DailyTaskTemplate(
        id = "daily_review",
        title = "课程复习",
        description = "复习今天一门课的笔记，整理 3~5 条容易混淆的知识点。",
        exp = 40,
        estimatedMinutes = 30
    ),
    DailyTaskTemplate(
        id = "daily_homework",
        title = "完成作业",
        description = "选择一门目前压力最大的课程，高质量完成当日布置的作业或实验。",
        exp = 60,
        estimatedMinutes = 45
    ),
    DailyTaskTemplate(
        id = "daily_run",
        title = "校园跑步 / 快走",
        description = "在校园跑步或快走累计 30 分钟以上，注意热身与拉伸。",
        exp = 50,
        estimatedMinutes = 30
    ),
    DailyTaskTemplate(
        id = "daily_reading",
        title = "阅读 30 分钟",
        description = "阅读专业书籍或非虚构类图书 30 分钟，提高专注与信息获取能力。",
        exp = 30,
        estimatedMinutes = 30
    ),
    DailyTaskTemplate(
        id = "daily_sleep",
        title = "按时早睡",
        description = "在 23:30 前关灯准备睡觉，保证充足睡眠。",
        exp = 20,
        estimatedMinutes = 10
    ),
    DailyTaskTemplate(
        id = "daily_water",
        title = "健康饮水与三餐",
        description = "今天保证 3 次正餐与足量饮水，减少含糖饮料摄入。",
        exp = 20,
        estimatedMinutes = 10
    ),
    DailyTaskTemplate(
        id = "daily_club",
        title = "社团 / 兴趣活动",
        description = "参与一次社团活动或兴趣练习，如乐队排练、篮球、舞蹈等。",
        exp = 40,
        estimatedMinutes = 40
    )
)
