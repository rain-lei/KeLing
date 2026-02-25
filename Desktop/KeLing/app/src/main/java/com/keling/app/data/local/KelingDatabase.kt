package com.keling.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.keling.app.data.local.dao.*
import com.keling.app.data.model.CheckIn
import com.keling.app.data.model.*

@Database(
    entities = [
        User::class,
        Course::class,
        ScheduleItem::class,
        Chapter::class,
        Material::class,
        Task::class,
        TeamTask::class,
        TaskProgress::class,
        StudySession::class,
        Achievement::class,
        UserAchievement::class,
        Badge::class,
        UserBadge::class,
        KnowledgePoint::class,
        KnowledgeRelation::class,
        LearningRecord::class,
        CheckIn::class
    ],
    version = 5,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class KelingDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun taskDao(): TaskDao
    abstract fun achievementDao(): AchievementDao
    abstract fun knowledgeDao(): KnowledgeDao
    abstract fun checkInDao(): CheckInDao
}
