package com.keling.app.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 音效类型
 */
enum class SoundType {
    TASK_COMPLETE,      // 任务完成
    ACHIEVEMENT_UNLOCK, // 成就解锁
    BUTTON_CLICK,       // 按钮点击
    ERROR,              // 错误提示
    NOTIFICATION,       // 系统通知
    LEVEL_UP,           // 升级
    REWARD              // 获得奖励
}

/**
 * 震动模式
 */
enum class VibrationPattern {
    LIGHT,      // 轻触
    MEDIUM,     // 中等
    HEAVY,      // 强烈
    SUCCESS,    // 成功
    ERROR,      // 错误
    DOUBLE,     // 双击
    ACHIEVEMENT // 成就解锁
}

/**
 * 音效和震动管理器
 */
@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<SoundType, Int>()
    private var isInitialized = false
    private var isSoundEnabled = true
    private var isVibrationEnabled = true
    
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    /**
     * 初始化音效系统
     */
    fun initialize() {
        if (isInitialized) return
        
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
        
        // 注意：实际项目中需要添加音效资源文件
        // 这里只是定义了音效类型的占位
        // soundMap[SoundType.TASK_COMPLETE] = soundPool?.load(context, R.raw.task_complete, 1) ?: 0
        // soundMap[SoundType.ACHIEVEMENT_UNLOCK] = soundPool?.load(context, R.raw.achievement, 1) ?: 0
        
        isInitialized = true
    }
    
    /**
     * 播放音效
     */
    fun playSound(type: SoundType, volume: Float = 1.0f) {
        if (!isSoundEnabled || !isInitialized) return
        
        soundMap[type]?.let { soundId ->
            soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
        }
    }
    
    /**
     * 触发震动
     */
    fun vibrate(pattern: VibrationPattern) {
        if (!isVibrationEnabled) return
        
        val effect = when (pattern) {
            VibrationPattern.LIGHT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                } else {
                    VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
                }
            }
            VibrationPattern.MEDIUM -> {
                VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            }
            VibrationPattern.HEAVY -> {
                VibrationEffect.createOneShot(100, 255)
            }
            VibrationPattern.SUCCESS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                } else {
                    VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
                }
            }
            VibrationPattern.ERROR -> {
                VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 50), -1)
            }
            VibrationPattern.DOUBLE -> {
                VibrationEffect.createWaveform(longArrayOf(0, 30, 50, 30), -1)
            }
            VibrationPattern.ACHIEVEMENT -> {
                // 成就解锁的特殊震动模式
                VibrationEffect.createWaveform(
                    longArrayOf(0, 50, 30, 80, 30, 100),
                    intArrayOf(0, 100, 0, 150, 0, 255),
                    -1
                )
            }
        }
        
        vibrator.vibrate(effect)
    }
    
    /**
     * 任务完成反馈
     */
    fun onTaskComplete() {
        playSound(SoundType.TASK_COMPLETE)
        vibrate(VibrationPattern.SUCCESS)
    }
    
    /**
     * 成就解锁反馈
     */
    fun onAchievementUnlock() {
        playSound(SoundType.ACHIEVEMENT_UNLOCK)
        vibrate(VibrationPattern.ACHIEVEMENT)
    }
    
    /**
     * 按钮点击反馈
     */
    fun onButtonClick() {
        playSound(SoundType.BUTTON_CLICK, 0.5f)
        vibrate(VibrationPattern.LIGHT)
    }
    
    /**
     * 错误反馈
     */
    fun onError() {
        playSound(SoundType.ERROR)
        vibrate(VibrationPattern.ERROR)
    }
    
    /**
     * 升级反馈
     */
    fun onLevelUp() {
        playSound(SoundType.LEVEL_UP)
        vibrate(VibrationPattern.ACHIEVEMENT)
    }
    
    /**
     * 设置音效开关
     */
    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }
    
    /**
     * 设置震动开关
     */
    fun setVibrationEnabled(enabled: Boolean) {
        isVibrationEnabled = enabled
    }
    
    /**
     * 释放资源
     */
    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
        isInitialized = false
    }
}
