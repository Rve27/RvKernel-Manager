package com.rve.rvkernelmanager.preference

import android.content.*

import kotlinx.coroutines.flow.*

import com.rve.rvkernelmanager.ui.theme.ThemeMode

class SettingsPreference(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(getThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _pollingInterval = MutableStateFlow(getPollingInterval())
    val pollingInterval: StateFlow<Long> = _pollingInterval.asStateFlow()
    
    private val _blurEnabled = MutableStateFlow(getBlurEnabled())
    val blurEnabled: StateFlow<Boolean> = _blurEnabled.asStateFlow()
    
    companion object {
	private const val THEME_KEY = "theme_mode"

        private const val POLLING_INTERVAL_KEY = "soc_polling_interval"
        private const val DEFAULT_POLLING_INTERVAL = 3000L
        
        private const val BLUR_ENABLED_KEY = "blur_enabled"
        private const val DEFAULT_BLUR_ENABLED = true
        
        @Volatile
        private var INSTANCE: SettingsPreference? = null
        
        fun getInstance(context: Context): SettingsPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsPreference(context).also { INSTANCE = it }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(THEME_KEY, mode.name).apply()
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
        prefs.edit().putLong(POLLING_INTERVAL_KEY, interval).apply()
        _pollingInterval.value = interval
    }
    
    private fun getPollingInterval(): Long {
        return prefs.getLong(POLLING_INTERVAL_KEY, DEFAULT_POLLING_INTERVAL)
    }
    
    fun setBlurEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(BLUR_ENABLED_KEY, enabled).apply()
        _blurEnabled.value = enabled
    }
    
    private fun getBlurEnabled(): Boolean {
        return prefs.getBoolean(BLUR_ENABLED_KEY, DEFAULT_BLUR_ENABLED)
    }
}
