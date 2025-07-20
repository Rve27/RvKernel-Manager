package com.rve.rvkernelmanager.ui.viewmodel

import android.app.Application

import androidx.lifecycle.*

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import com.rve.rvkernelmanager.preference.*
import com.rve.rvkernelmanager.ui.theme.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val themePreference = ThemePreference.getInstance(application)
    private val socPreference = SoCPreference.getInstance(application)
    private val blurPreference = BlurPreference.getInstance(application)
    
    val themeMode: StateFlow<ThemeMode> = themePreference.themeMode
    val pollingInterval: StateFlow<Long> = socPreference.pollingInterval
    val blurEnabled: StateFlow<Boolean> = blurPreference.blurEnabled
    
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreference.setThemeMode(mode)
        }
    }

    fun setPollingInterval(interval: Long) {
        viewModelScope.launch {
            socPreference.setPollingInterval(interval)
        }
    }

    fun setBlurEnabled(enabled: Boolean) {
        viewModelScope.launch {
            blurPreference.setBlurEnabled(enabled)
        }
    }
}
