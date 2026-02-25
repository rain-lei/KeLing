package com.keling.app.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.keling.app.data.model.*

class Converters {
    private val gson = Gson()

    // UserRole
    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = UserRole.valueOf(value)

    // PrivacyLevel
    @TypeConverter
    fun fromPrivacyLevel(level: PrivacyLevel): String = level.name

    @TypeConverter
    fun toPrivacyLevel(value: String): PrivacyLevel = PrivacyLevel.valueOf(value)

    // CourseType
    @TypeConverter
    fun fromCourseType(type: CourseType): String = type.name

    @TypeConverter
    fun toCourseType(value: String): CourseType = CourseType.valueOf(value)

    // WeekType
    @TypeConverter
    fun fromWeekType(type: WeekType): String = type.name

    @TypeConverter
    fun toWeekType(value: String): WeekType = WeekType.valueOf(value)

    // MaterialType
    @TypeConverter
    fun fromMaterialType(type: MaterialType): String = type.name

    @TypeConverter
    fun toMaterialType(value: String): MaterialType = MaterialType.valueOf(value)

    // TaskType
    @TypeConverter
    fun fromTaskType(type: TaskType): String = type.name

    @TypeConverter
    fun toTaskType(value: String): TaskType = TaskType.valueOf(value)

    // TaskDifficulty
    @TypeConverter
    fun fromTaskDifficulty(difficulty: TaskDifficulty): String = difficulty.name

    @TypeConverter
    fun toTaskDifficulty(value: String): TaskDifficulty = TaskDifficulty.valueOf(value)

    // TaskStatus
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String = status.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    // AchievementCategory
    @TypeConverter
    fun fromAchievementCategory(category: AchievementCategory): String = category.name

    @TypeConverter
    fun toAchievementCategory(value: String): AchievementCategory = AchievementCategory.valueOf(value)

    // AchievementRarity
    @TypeConverter
    fun fromAchievementRarity(rarity: AchievementRarity): String = rarity.name

    @TypeConverter
    fun toAchievementRarity(value: String): AchievementRarity = AchievementRarity.valueOf(value)

    // RelationType
    @TypeConverter
    fun fromRelationType(type: RelationType): String = type.name

    @TypeConverter
    fun toRelationType(value: String): RelationType = RelationType.valueOf(value)

    // List<String>
    @TypeConverter
    fun fromStringList(list: List<String>): String = gson.toJson(list)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    // Map<String, Float>
    @TypeConverter
    fun fromFloatMap(map: Map<String, Float>): String = gson.toJson(map)

    @TypeConverter
    fun toFloatMap(value: String): Map<String, Float> {
        val type = object : TypeToken<Map<String, Float>>() {}.type
        return gson.fromJson(value, type) ?: emptyMap()
    }
}
