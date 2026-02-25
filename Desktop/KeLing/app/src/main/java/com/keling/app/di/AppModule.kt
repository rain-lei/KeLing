package com.keling.app.di

import com.google.gson.Gson
import com.keling.app.data.task.GradeTaskGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGradeTaskGenerator(gson: Gson): GradeTaskGenerator = GradeTaskGenerator(gson)
}
