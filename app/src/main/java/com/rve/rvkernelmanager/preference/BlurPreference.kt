package com.rve.rvkernelmanager.preference

import android.content.*
import kotlinx.coroutines.flow.*

class BlurPreference(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("blur_prefs", Context.MODE_PRIVATE)
    
    private val _blurEnabled = MutableStateFlow(getBlurEnabled())
    val blurEnabled: StateFlow<Boolean> = _blurEnabled.asStateFlow()
    
    companion object {
        private const val BLUR_ENABLED_KEY = "blur_enabled"
        private const val DEFAULT_BLUR_ENABLED = true
        
        @Volatile
        private var INSTANCE: BlurPreference? = null
        
        fun getInstance(context: Context): BlurPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BlurPreference(context).also { INSTANCE = it }
            }
        }
    }
    
    fun setBlurEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(BLUR_ENABLED_KEY, enabled).apply()
        _blurEnabled.value = enabled
    }
    
    private fun getBlurEnabled(): Boolean {
        return prefs.getBoolean(BLUR_ENABLED_KEY, DEFAULT_BLUR_ENABLED)
    }
}
