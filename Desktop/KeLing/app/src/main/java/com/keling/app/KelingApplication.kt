package com.keling.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KelingApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 初始化全局配置
    }
}
