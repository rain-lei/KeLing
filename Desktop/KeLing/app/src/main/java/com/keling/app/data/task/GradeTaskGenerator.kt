package com.keling.app.data.task

import com.google.gson.Gson
import com.keling.app.data.model.*

/**
 * 按年级生成具体、可执行、可检测完成的任务。
 * 年级格式：2024、2023、大一、大二 等。
 */
class GradeTaskGenerator(private val gson: Gson = Gson()) {

    fun generateForGrade(grade: String): List<Task> {
        val templates = getTemplatesForGrade(grade)
        val now = System.currentTimeMillis()
        val day = 24L * 60 * 60 * 1000
        return templates.mapIndexed { index, t ->
            Task(
                id = "task_${grade}_${t.subject}_${index}_${now}",
                title = t.title,
                description = t.description,
                type = t.type,
                difficulty = t.difficulty,
                status = TaskStatus.PENDING,
                courseId = t.courseId,
                chapterId = t.chapterId,
                experienceReward = t.experienceReward,
                coinReward = t.experienceReward / 2,
                deadline = now + t.deadlineDays * day,
                estimatedMinutes = t.estimatedMinutes,
                progress = 0f,
                targetGrade = grade,
                actionType = t.actionType.name,
                actionPayload = t.serializePayload(gson),
                order = index
            )
        }
    }

    private fun getTemplatesForGrade(grade: String): List<TaskTemplate> {
        val normalized = normalizeGrade(grade)
        return when {
            normalized in listOf("2024", "2023", "大一", "大二") -> templatesFreshman(normalized)
            normalized in listOf("2022", "大三") -> templatesJunior(normalized)
            else -> templatesFreshman(grade)
        }
    }

    private fun normalizeGrade(g: String): String = g.trim()

    @Suppress("UNUSED_PARAMETER")
    private fun templatesFreshman(grade: String): List<TaskTemplate> {
        return listOf(
            // 高数测验
            TaskTemplate(
                subject = "高等数学",
                chapter = "极限与连续",
                title = "高数·极限与连续 小测验",
                description = "完成 5 道选择题，正确率 ≥80% 即通过。",
                type = TaskType.PRACTICE,
                difficulty = TaskDifficulty.MEDIUM,
                courseId = "course_math",
                chapterId = "ch_limit",
                experienceReward = 50,
                estimatedMinutes = 15,
                deadlineDays = 2,
                actionType = TaskActionType.QUIZ,
                quizPayload = QuizPayload(
                    questions = listOf(
                        QuizQuestion("q1", "lim(x→0) sin(x)/x = ?", listOf("0", "1", "∞", "不存在"), 1, "重要极限"),
                        QuizQuestion("q2", "若 lim f(x)=A, lim g(x)=B 且 B≠0，则 lim f(x)/g(x) = ?", listOf("A+B", "A-B", "A/B", "AB"), 2),
                        QuizQuestion("q3", "函数在点 a 连续等价于 lim(x→a) f(x) = ?", listOf("f(a)", "0", "∞", "f(a+1)"), 0),
                        QuizQuestion("q4", "下列哪个函数在 x=0 处连续？", listOf("1/x", "|x|", "1/x²", "sgn(x) 在 0 无定义"), 1),
                        QuizQuestion("q5", "夹逼定理用于求哪类极限？", listOf("仅 0/0", "仅 ∞/∞", "不易直接求的极限", "仅数列极限"), 2)
                    ),
                    passRate = 0.8f
                )
            ),
            // 阅读
            TaskTemplate(
                subject = "高等数学",
                chapter = "导数与微分",
                title = "高数·导数与微分 教材阅读",
                description = "阅读指定章节 15 分钟，完成后点击「完成阅读」。",
                type = TaskType.REVIEW,
                difficulty = TaskDifficulty.EASY,
                courseId = "course_math",
                chapterId = "ch_derivative",
                experienceReward = 25,
                estimatedMinutes = 15,
                deadlineDays = 1,
                actionType = TaskActionType.READING,
                readingPayload = ReadingPayload(15, "§2.1–2.3", "高数上 导数与微分")
            ),
            // 习题
            TaskTemplate(
                subject = "线性代数",
                chapter = "行列式",
                title = "线代·行列式 课后习题",
                description = "完成课后习题第 1、2、3、4、5 题，逐题勾选完成。",
                type = TaskType.PRACTICE,
                difficulty = TaskDifficulty.MEDIUM,
                courseId = "course_linalg",
                chapterId = "ch_det",
                experienceReward = 40,
                estimatedMinutes = 25,
                deadlineDays = 2,
                actionType = TaskActionType.EXERCISE,
                exercisePayload = ExercisePayload("线性代数", "行列式", "1-5", 5)
            ),
            // 背诵
            TaskTemplate(
                subject = "大学英语",
                chapter = "四级词汇",
                title = "英语·今日词汇 背诵打卡",
                description = "背诵 20 个四级核心词汇，完成后自检并确认。",
                type = TaskType.DAILY,
                difficulty = TaskDifficulty.EASY,
                courseId = "course_english",
                chapterId = "ch_vocab",
                experienceReward = 20,
                estimatedMinutes = 20,
                deadlineDays = 1,
                actionType = TaskActionType.MEMORIZATION,
                memorizationPayload = MemorizationPayload(20, "四级核心词汇", "可先看例句再默记")
            ),
            // 视频
            TaskTemplate(
                subject = "程序设计基础",
                chapter = "循环结构",
                title = "程设·循环结构 微课学习",
                description = "观看「循环结构」微课至少 10 分钟，然后确认完成。",
                type = TaskType.PRACTICE,
                difficulty = TaskDifficulty.EASY,
                courseId = "course_cs",
                chapterId = "ch_loop",
                experienceReward = 30,
                estimatedMinutes = 12,
                deadlineDays = 1,
                actionType = TaskActionType.VIDEO,
                videoPayload = VideoPayload(10, "循环结构 for/while")
            ),
            // 第二组高数测验
            TaskTemplate(
                subject = "高等数学",
                chapter = "导数应用",
                title = "高数·导数应用 小测验",
                description = "完成 4 道选择题，正确率 ≥75% 即通过。",
                type = TaskType.PRACTICE,
                difficulty = TaskDifficulty.MEDIUM,
                courseId = "course_math",
                chapterId = "ch_derivative_app",
                experienceReward = 45,
                estimatedMinutes = 12,
                deadlineDays = 3,
                actionType = TaskActionType.QUIZ,
                quizPayload = QuizPayload(
                    questions = listOf(
                        QuizQuestion("q1", "函数 f 在区间 I 上 f'(x)>0 则 f 在 I 上？", listOf("单调减", "单调增", "常数", "不单调"), 1),
                        QuizQuestion("q2", "f'(a)=0 且 f''(a)<0 则 x=a 为？", listOf("极大值点", "极小值点", "拐点", "不能确定"), 0),
                        QuizQuestion("q3", "洛必达法则可用于求 0/0 型极限？", listOf("否", "是", "仅数列", "仅 x→0"), 1),
                        QuizQuestion("q4", "曲线 y=f(x) 凸性与 f'' 的关系？", listOf("f''>0 凸", "f''>0 凹", "无关", "仅 f'>0 时有关"), 1)
                    ),
                    passRate = 0.75f
                )
            )
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun templatesJunior(grade: String): List<TaskTemplate> {
        return listOf(
            TaskTemplate(
                subject = "数据结构",
                chapter = "树与二叉树",
                title = "数据结构·二叉树 小测验",
                description = "完成 5 道选择题，正确率 ≥80% 即通过。",
                type = TaskType.CHALLENGE,
                difficulty = TaskDifficulty.HARD,
                courseId = "course_ds",
                chapterId = "ch_tree",
                experienceReward = 60,
                estimatedMinutes = 15,
                deadlineDays = 2,
                actionType = TaskActionType.QUIZ,
                quizPayload = QuizPayload(
                    questions = listOf(
                        QuizQuestion("q1", "深度为 k 的满二叉树结点数？", listOf("2^k", "2^k-1", "2^(k+1)-1", "k^2"), 2),
                        QuizQuestion("q2", "完全二叉树适合用何种存储？", listOf("仅链式", "仅顺序", "顺序或链式", "散列"), 2),
                        QuizQuestion("q3", "先序遍历顺序？", listOf("左-根-右", "根-左-右", "左-右-根", "右-根-左"), 1),
                        QuizQuestion("q4", "中序+先序可唯一确定一棵二叉树？", listOf("否", "是", "仅满二叉树", "仅完全二叉树"), 1),
                        QuizQuestion("q5", "AVL 树平衡因子绝对值不超过？", listOf("0", "1", "2", "3"), 1)
                    ),
                    passRate = 0.8f
                )
            ),
            TaskTemplate(
                subject = "数据结构",
                chapter = "树与二叉树",
                title = "数据结构·二叉树 教材阅读",
                description = "阅读「树与二叉树」章节 20 分钟。",
                type = TaskType.REVIEW,
                difficulty = TaskDifficulty.MEDIUM,
                courseId = "course_ds",
                chapterId = "ch_tree",
                experienceReward = 35,
                estimatedMinutes = 20,
                deadlineDays = 1,
                actionType = TaskActionType.READING,
                readingPayload = ReadingPayload(20, "第5章", "数据结构 树与二叉树")
            )
        )
    }

    private data class TaskTemplate(
        val subject: String,
        val chapter: String,
        val title: String,
        val description: String,
        val type: TaskType,
        val difficulty: TaskDifficulty,
        val courseId: String,
        val chapterId: String,
        val experienceReward: Int,
        val estimatedMinutes: Int,
        val deadlineDays: Long,
        val actionType: TaskActionType,
        val quizPayload: QuizPayload? = null,
        val readingPayload: ReadingPayload? = null,
        val exercisePayload: ExercisePayload? = null,
        val videoPayload: VideoPayload? = null,
        val memorizationPayload: MemorizationPayload? = null
    ) {
        fun serializePayload(gson: Gson): String {
            val payload: Any = when (actionType) {
                TaskActionType.QUIZ -> checkNotNull(quizPayload)
                TaskActionType.READING -> checkNotNull(readingPayload)
                TaskActionType.EXERCISE -> checkNotNull(exercisePayload)
                TaskActionType.VIDEO -> checkNotNull(videoPayload)
                TaskActionType.MEMORIZATION -> checkNotNull(memorizationPayload)
            }
            return gson.toJson(payload)
        }
    }
}
