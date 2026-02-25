package com.keling.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.accessibilityDataStore: DataStore<Preferences> by preferencesDataStore(name = "accessibility_prefs")

private val KEY_FONT_SIZE_LEVEL = intPreferencesKey("font_size_level")
private val KEY_HIGH_CONTRAST = booleanPreferencesKey("high_contrast_mode")
private val KEY_REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
private val KEY_TTS_ENABLED = booleanPreferencesKey("tts_enabled")
private val KEY_TTS_SPEECH_RATE = floatPreferencesKey("tts_speech_rate")
private val KEY_GESTURE_CONTROL = booleanPreferencesKey("gesture_control_enabled")

data class AccessibilityState(
    val fontSizeLevel: Int = 1,
    val highContrastMode: Boolean = false,
    val reduceMotion: Boolean = false,
    val ttsEnabled: Boolean = false,
    val ttsSpeechRate: Float = 1f,
    val gestureControlEnabled: Boolean = false
) {
    fun fontScaleMultiplier(): Float = when (fontSizeLevel) {
        0 -> 0.85f
        1 -> 1f
        2 -> 1.25f
        3 -> 1.5f
        else -> 1f
    }
}

@Singleton
class AccessibilityPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.accessibilityDataStore

    val accessibilityState: Flow<AccessibilityState> = dataStore.data.map { prefs ->
        AccessibilityState(
            fontSizeLevel = prefs[KEY_FONT_SIZE_LEVEL] ?: 1,
            highContrastMode = prefs[KEY_HIGH_CONTRAST] ?: false,
            reduceMotion = prefs[KEY_REDUCE_MOTION] ?: false,
            ttsEnabled = prefs[KEY_TTS_ENABLED] ?: false,
            ttsSpeechRate = prefs[KEY_TTS_SPEECH_RATE] ?: 1f,
            gestureControlEnabled = prefs[KEY_GESTURE_CONTROL] ?: false
        )
    }

    suspend fun setFontSizeLevel(level: Int) {
        dataStore.edit { it[KEY_FONT_SIZE_LEVEL] = level.coerceIn(0, 3) }
    }

    suspend fun setHighContrastMode(enabled: Boolean) {
        dataStore.edit { it[KEY_HIGH_CONTRAST] = enabled }
    }

    suspend fun setReduceMotion(enabled: Boolean) {
        dataStore.edit { it[KEY_REDUCE_MOTION] = enabled }
    }

    suspend fun setTtsEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_TTS_ENABLED] = enabled }
    }

    suspend fun setTtsSpeechRate(rate: Float) {
        dataStore.edit { it[KEY_TTS_SPEECH_RATE] = rate.coerceIn(0.5f, 2f) }
    }

    suspend fun setGestureControlEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_GESTURE_CONTROL] = enabled }
    }
}
