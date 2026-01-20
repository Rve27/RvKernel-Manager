/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
}
