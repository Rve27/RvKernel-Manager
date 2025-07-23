package com.rve.rvkernelmanager.ui.viewmodel

import android.app.Application
import android.content.Context

import androidx.lifecycle.*

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import com.rve.rvkernelmanager.utils.Utils
import com.rve.rvkernelmanager.preference.SettingsPreference
import com.rve.rvkernelmanager.ui.theme.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsPreference = SettingsPreference.getInstance(application)
    
    val themeMode: StateFlow<ThemeMode> = settingsPreference.themeMode
    val pollingInterval: StateFlow<Long> = settingsPreference.pollingInterval
    val blurEnabled: StateFlow<Boolean> = settingsPreference.blurEnabled

    private val _appVersion = MutableStateFlow("Unknown")
    val appVersion: StateFlow<String> = _appVersion
    
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsPreference.setThemeMode(mode)
        }
    }

    fun setPollingInterval(interval: Long) {
        viewModelScope.launch {
            settingsPreference.setPollingInterval(interval)
        }
    }

    fun setBlurEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreference.setBlurEnabled(enabled)
        }
    }

    fun loadSettingsData(context: Context) {
	viewModelScope.launch(Dispatchers.IO) {
	    _appVersion.value = Utils.getAppVersion(context)
	}
    }
}
