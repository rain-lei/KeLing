package com.keling.app.di

import android.content.Context
import androidx.room.Room
import com.keling.app.data.local.KelingDatabase
import com.keling.app.data.local.dao.CheckInDao
import com.keling.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): KelingDatabase {
        return Room.databaseBuilder(
            context,
            KelingDatabase::class.java,
            "keling_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: KelingDatabase): UserDao = database.userDao()

    @Provides
    fun provideCourseDao(database: KelingDatabase): CourseDao = database.courseDao()

    @Provides
    fun provideTaskDao(database: KelingDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideAchievementDao(database: KelingDatabase): AchievementDao = database.achievementDao()

    @Provides
    fun provideKnowledgeDao(database: KelingDatabase): KnowledgeDao = database.knowledgeDao()

    @Provides
    fun provideCheckInDao(database: KelingDatabase): CheckInDao = database.checkInDao()
}
