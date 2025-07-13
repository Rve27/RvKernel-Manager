package com.rve.rvkernelmanager.ui.viewmodel

import android.app.Application

import androidx.lifecycle.*

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import com.rve.rvkernelmanager.preference.SoCPreference
import com.rve.rvkernelmanager.ui.theme.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val themePreference = ThemePreference.getInstance(application)
    private val socPreference = SoCPreference.getInstance(application)
    
    val themeMode: StateFlow<ThemeMode> = themePreference.themeMode
    val pollingInterval: StateFlow<Long> = socPreference.pollingInterval
    
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
}
