package com.rve.rvkernelmanager.ui.theme

import android.content.*

import kotlinx.coroutines.flow.*

class ThemePreference(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    private val _themeMode = MutableStateFlow(getThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()
    
    companion object {
        private const val THEME_KEY = "theme_mode"
        
        @Volatile
        private var INSTANCE: ThemePreference? = null
        
        fun getInstance(context: Context): ThemePreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemePreference(context).also { INSTANCE = it }
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
}
