/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rve.rvkernelmanager.ui.settings.SettingsPreference
import com.rve.rvkernelmanager.ui.theme.ThemeMode
import com.rve.rvkernelmanager.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsPreference = SettingsPreference.getInstance(application)

    val themeMode: StateFlow<ThemeMode> = settingsPreference.themeMode
    val pollingInterval: StateFlow<Long> = settingsPreference.pollingInterval

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

    fun loadSettingsData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _appVersion.value = Utils.getAppVersion(context)
        }
    }
}
