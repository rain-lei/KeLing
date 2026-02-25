package com.keling.app.ui.screens.knowledge

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.local.dao.KnowledgeDao
import com.keling.app.data.model.KnowledgePoint
import com.keling.app.data.model.KnowledgeRelation
import com.keling.app.data.remote.KelingApiService
import com.keling.app.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KnowledgeGraphViewModel @Inject constructor(
    private val knowledgeDao: KnowledgeDao,
    private val kelingApiService: KelingApiService,
    private val courseRepository: CourseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: String = savedStateHandle["courseId"] ?: ""

    private val _uiState = MutableStateFlow(KnowledgeGraphUiState(isLoading = true))
    val uiState: StateFlow<KnowledgeGraphUiState> = _uiState.asStateFlow()

    init {
        loadKnowledgeGraph()
    }

    private fun loadKnowledgeGraph() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val course = courseRepository.getCourseById(courseId)
                val courseName = course?.name ?: courseId

                // 先尝试从后端 AI 接口拉取基于「大数据+课程名称」生成的知识图谱
                val response = try {
                    kelingApiService.getKnowledgeGraph(
                        token = "",
                        courseId = courseId
                    )
                } catch (_: Exception) {
                    null
                }

                val body = response?.body()
                if (response != null && response.isSuccessful && body != null && body.nodes.isNotEmpty()) {
                    // 将返回的知识点与关系持久化到本地数据库
                    knowledgeDao.insertKnowledgePoints(body.nodes.map {
                        // 确保 courseId 写入
                        it.copy(courseId = courseId)
                    })
                    knowledgeDao.insertRelations(body.relations)

                    _uiState.update {
                        it.copy(
                            courseName = courseName,
                            points = body.nodes,
                            relations = body.relations,
                            isLoading = false,
                            error = null
                        )
                    }
                    return@launch
                }

                // 若后端暂不可用，则尝试使用本地已缓存的知识点
                val localPoints = knowledgeDao.getKnowledgePointsByCourse(courseId).first()
                val localRelations = if (localPoints.isNotEmpty()) {
                    // 简单拉取所有涉及这些点的关系
                    val all = mutableListOf<KnowledgeRelation>()
                    localPoints.forEach { p ->
                        all += knowledgeDao.getRelationsFrom(p.id).first()
                        all += knowledgeDao.getRelationsTo(p.id).first()
                    }
                    all.distinctBy { it.id }
                } else {
                    emptyList()
                }

                val finalPoints = if (localPoints.isNotEmpty()) localPoints else createFallbackKnowledgePoints(courseName)

                _uiState.update {
                    it.copy(
                        courseName = courseName,
                        points = finalPoints,
                        relations = localRelations,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "加载知识图谱时出现问题，请稍后重试。"
                    )
                }
            }
        }
    }

    /**
     * 当后端和本地都没有数据时，根据课程名称生成一份「通用型」知识点草图。
     * 这里相当于在客户端做一个兜底的「实用知识图谱」结构。
     */
    private fun createFallbackKnowledgePoints(courseName: String): List<KnowledgePoint> {
        val baseId = courseName.ifBlank { "course" }
        return listOf(
            KnowledgePoint(
                id = "${baseId}_concepts",
                name = "基础概念与术语",
                description = "理解「$courseName」中最常见的核心概念和专业术语。",
                courseId = courseId,
                difficulty = 2,
                importance = 5,
                masteryLevel = 0.3f
            ),
            KnowledgePoint(
                id = "${baseId}_theorems",
                name = "关键定理 / 法则",
                description = "掌握本课程中的关键定理、公式或基本规律，并能在习题中运用。",
                courseId = courseId,
                difficulty = 3,
                importance = 5,
                masteryLevel = 0.2f
            ),
            KnowledgePoint(
                id = "${baseId}_methods",
                name = "典型解题 / 分析方法",
                description = "总结常用的解题套路、分析框架或实验步骤。",
                courseId = courseId,
                difficulty = 3,
                importance = 4,
                masteryLevel = 0.4f
            ),
            KnowledgePoint(
                id = "${baseId}_applications",
                name = "实际应用场景",
                description = "了解「$courseName」在真实工程或生活中的典型应用，形成知识迁移意识。",
                courseId = courseId,
                difficulty = 2,
                importance = 4,
                masteryLevel = 0.1f
            )
        )
    }
}

