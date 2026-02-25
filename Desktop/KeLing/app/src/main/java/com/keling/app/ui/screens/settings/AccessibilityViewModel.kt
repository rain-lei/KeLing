package com.keling.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keling.app.data.preferences.AccessibilityPreferencesRepository
import com.keling.app.data.preferences.AccessibilityState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccessibilityViewModel @Inject constructor(
    private val repository: AccessibilityPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccessibilityState())
    val uiState: StateFlow<AccessibilityState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.accessibilityState
                .catch { }
                .collect { _uiState.value = it }
        }
    }

    fun setFontSizeLevel(level: Int) {
        viewModelScope.launch { repository.setFontSizeLevel(level) }
    }

    fun setHighContrastMode(enabled: Boolean) {
        viewModelScope.launch { repository.setHighContrastMode(enabled) }
    }

    fun setReduceMotion(enabled: Boolean) {
        viewModelScope.launch { repository.setReduceMotion(enabled) }
    }

    fun setTtsEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setTtsEnabled(enabled) }
    }

    fun setTtsSpeechRate(rate: Float) {
        viewModelScope.launch { repository.setTtsSpeechRate(rate) }
    }

    fun setGestureControlEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setGestureControlEnabled(enabled) }
    }
}
