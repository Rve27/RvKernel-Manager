package com.rve.rvkernelmanager.preference

import android.content.*
import kotlinx.coroutines.flow.*

class SoCPreference(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    
    private val _pollingInterval = MutableStateFlow(getPollingInterval())
    val pollingInterval: StateFlow<Long> = _pollingInterval.asStateFlow()
    
    companion object {
        private const val POLLING_INTERVAL_KEY = "soc_polling_interval"
        private const val DEFAULT_POLLING_INTERVAL = 3000L
        
        @Volatile
        private var INSTANCE: SoCPreference? = null
        
        fun getInstance(context: Context): SoCPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoCPreference(context).also { INSTANCE = it }
            }
        }
    }
    
    fun setPollingInterval(interval: Long) {
        prefs.edit().putLong(POLLING_INTERVAL_KEY, interval).apply()
        _pollingInterval.value = interval
    }
    
    private fun getPollingInterval(): Long {
        return prefs.getLong(POLLING_INTERVAL_KEY, DEFAULT_POLLING_INTERVAL)
    }
}
