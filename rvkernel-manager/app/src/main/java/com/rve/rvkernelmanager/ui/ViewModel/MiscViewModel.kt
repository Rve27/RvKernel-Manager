package com.rve.rvkernelmanager.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.rve.rvkernelmanager.utils.*

class MiscViewModel : ViewModel() {

    private val _thermalSconfig = MutableStateFlow("")
    val thermalSconfig: StateFlow<String> = _thermalSconfig

    private val _hasThermalSconfig = MutableStateFlow(false)
    val hasThermalSconfig: StateFlow<Boolean> = _hasThermalSconfig

    private var cachedThermalSconfig: String? = null

    private var pollingJob: Job? = null

    init {
        loadInitialData()
        startPolling()
    }

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                loadInitialData()
                delay(3000)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentThermalSconfig = readFile(THERMAL_SCONFIG_PATH)
            if (currentThermalSconfig != cachedThermalSconfig) {
                _thermalSconfig.value = currentThermalSconfig
                cachedThermalSconfig = currentThermalSconfig
            }

            _hasThermalSconfig.value = testFile(THERMAL_SCONFIG_PATH)
        }
    }

    fun updateThermalSconfig(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            setPermissions(644, THERMAL_SCONFIG_PATH)
            val newValue = if (isChecked) "10" else "0"
            writeFile(THERMAL_SCONFIG_PATH, newValue)
            setPermissions(444, THERMAL_SCONFIG_PATH)
            _thermalSconfig.value = readFile(THERMAL_SCONFIG_PATH)
            cachedThermalSconfig = _thermalSconfig.value
        }
    }
}
