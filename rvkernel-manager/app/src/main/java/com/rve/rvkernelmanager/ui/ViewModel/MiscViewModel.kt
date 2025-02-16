package com.rve.rvkernelmanager.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.channels.Channel
import com.rve.rvkernelmanager.utils.Utils
import com.rve.rvkernelmanager.utils.MiscUtils

class MiscViewModel : ViewModel() {

    private val _thermalSconfig = MutableStateFlow("")
    val thermalSconfig: StateFlow<String> = _thermalSconfig

    private val _hasThermalSconfig = MutableStateFlow(false)
    val hasThermalSconfig: StateFlow<Boolean> = _hasThermalSconfig

    private val _schedAutogroup = MutableStateFlow("")
    val schedAutogroup: StateFlow<String> = _schedAutogroup

    private val _hasSchedAutogroup = MutableStateFlow(false)
    val hasSchedAutogroup: StateFlow<Boolean> = _hasSchedAutogroup

    private val _swappiness = MutableStateFlow("")
    val swappiness: StateFlow<String> = _swappiness

    private val _hasSwappiness = MutableStateFlow(false)
    val hasSwappiness: StateFlow<Boolean> = _hasSwappiness

    private val _showSwappinessDialog = MutableStateFlow(false)
    val showSwappinessDialog: StateFlow<Boolean> = _showSwappinessDialog

    private var cachedThermalSconfig: String? = null
    private var cachedSchedAutogroup: String? = null
    private var cachedSwappiness: String? = null

    private val refreshRequests = Channel<Unit>(1)
    var isRefreshing by mutableStateOf(false)
    private set

    init {
        viewModelScope.launch {
            for (r in refreshRequests) {
                isRefreshing = true
                try {
                    delay(1000)
                } finally {
                    isRefreshing = false
                }
            }
        }
    }

    fun refresh() {
        refreshRequests.trySend(Unit)
        loadMiscData()
    }

    fun loadMiscData() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentThermalSconfig = Utils.readFile(MiscUtils.THERMAL_SCONFIG_PATH)
            if (currentThermalSconfig != cachedThermalSconfig) {
                _thermalSconfig.value = currentThermalSconfig
                cachedThermalSconfig = currentThermalSconfig
            }
            _hasThermalSconfig.value = Utils.testFile(MiscUtils.THERMAL_SCONFIG_PATH)

            val currentSchedAutogroup = Utils.readFile(MiscUtils.SCHED_AUTOGROUP_PATH)
            if (currentSchedAutogroup != cachedSchedAutogroup) {
                _schedAutogroup.value = currentSchedAutogroup
                cachedSchedAutogroup = currentSchedAutogroup
            }
            _hasSchedAutogroup.value = Utils.testFile(MiscUtils.SCHED_AUTOGROUP_PATH)

	    val currentSwappiness = Utils.readFile(MiscUtils.SWAPPINESS_PATH)
            if (currentSwappiness != cachedSwappiness) {
                _swappiness.value = currentSwappiness
                cachedSwappiness = currentSwappiness
            }
            _hasSwappiness.value = Utils.testFile(MiscUtils.SWAPPINESS_PATH)
        }
    }

    fun updateThermalSconfig(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.setPermissions(644, MiscUtils.THERMAL_SCONFIG_PATH)
            val newValue = if (isChecked) "10" else "0"
            Utils.writeFile(MiscUtils.THERMAL_SCONFIG_PATH, newValue)
            Utils.setPermissions(444, MiscUtils.THERMAL_SCONFIG_PATH)
            _thermalSconfig.value = Utils.readFile(MiscUtils.THERMAL_SCONFIG_PATH)
            cachedThermalSconfig = _thermalSconfig.value
        }
    }

    fun updateSchedAutogroup(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = if (isChecked) "1" else "0"
            Utils.writeFile(MiscUtils.SCHED_AUTOGROUP_PATH, newValue)
            _schedAutogroup.value = Utils.readFile(MiscUtils.SCHED_AUTOGROUP_PATH)
            cachedSchedAutogroup = _schedAutogroup.value
        }
    }

    fun showSwappinessDialog() {
        _showSwappinessDialog.value = true
    }

    fun hideSwappinessDialog() {
        _showSwappinessDialog.value = false
    }

    fun updateSwappiness(newValue: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.setPermissions(644, MiscUtils.SWAPPINESS_PATH)
            Utils.writeFile(MiscUtils.SWAPPINESS_PATH, newValue)
            _swappiness.value = newValue
        }
    }
}
