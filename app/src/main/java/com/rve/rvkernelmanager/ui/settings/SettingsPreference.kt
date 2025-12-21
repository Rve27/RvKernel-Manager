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

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rve.rvkernelmanager.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsPreference(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(getThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _pollingInterval = MutableStateFlow(getPollingInterval())
    val pollingInterval: StateFlow<Long> = _pollingInterval.asStateFlow()

    companion object {
        private const val THEME_KEY = "theme_mode"

        private const val POLLING_INTERVAL_KEY = "soc_polling_interval"
        private const val DEFAULT_POLLING_INTERVAL = 3000L

        @Volatile
        private var INSTANCE: SettingsPreference? = null

        fun getInstance(context: Context): SettingsPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsPreference(context).also { INSTANCE = it }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit { putString(THEME_KEY, mode.name) }
        _themeMode.value = mode
    }

    private fun getThemeMode(): ThemeMode {
        val savedMode = prefs.getString(THEME_KEY, ThemeMode.SYSTEM_DEFAULT.name)
        return try {
            ThemeMode.valueOf(savedMode!!)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM_DEFAULT
        }
    }

    fun setPollingInterval(interval: Long) {
        prefs.edit { putLong(POLLING_INTERVAL_KEY, interval) }
        _pollingInterval.value = interval
    }

    private fun getPollingInterval(): Long {
        return prefs.getLong(POLLING_INTERVAL_KEY, DEFAULT_POLLING_INTERVAL)
    }
}
