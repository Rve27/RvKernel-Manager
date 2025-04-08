package com.rve.rvkernelmanager.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.*
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

    private val _printk = MutableStateFlow("")
    val printk: StateFlow<String> = _printk

    private val _hasPrintk = MutableStateFlow(false)
    val hasPrintk: StateFlow<Boolean> = _hasPrintk

    private val _showPrintkDialog = MutableStateFlow(false)
    val showPrintkDialog: StateFlow<Boolean> = _showPrintkDialog

    private var cachedThermalSconfig: String? = null
    private var cachedSchedAutogroup: String? = null
    private var cachedSwappiness: String? = null
    private var cachedPrintk: String? = null

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
            val currentThermalSconfig = Utils.readFile(MiscUtils.THERMAL_SCONFIG)
            if (currentThermalSconfig != cachedThermalSconfig) {
                _thermalSconfig.value = currentThermalSconfig
                cachedThermalSconfig = currentThermalSconfig
            }
            _hasThermalSconfig.value = Utils.testFile(MiscUtils.THERMAL_SCONFIG)

            val currentSchedAutogroup = Utils.readFile(MiscUtils.SCHED_AUTOGROUP)
            if (currentSchedAutogroup != cachedSchedAutogroup) {
                _schedAutogroup.value = currentSchedAutogroup
                cachedSchedAutogroup = currentSchedAutogroup
            }
            _hasSchedAutogroup.value = Utils.testFile(MiscUtils.SCHED_AUTOGROUP)

            val currentSwappiness = Utils.readFile(MiscUtils.SWAPPINESS)
            if (currentSwappiness != cachedSwappiness) {
                _swappiness.value = currentSwappiness
                cachedSwappiness = currentSwappiness
            }
            _hasSwappiness.value = Utils.testFile(MiscUtils.SWAPPINESS)

	    val currentPrintk = Utils.readFile(MiscUtils.PRINTK)
            if (currentPrintk != cachedPrintk) {
                _printk.value = currentPrintk
                cachedPrintk = currentPrintk
            }
            _hasPrintk.value = Utils.testFile(MiscUtils.PRINTK)
        }
    }

    fun updateThermalSconfig(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = if (isChecked) "10" else "0"
            if (newValue != cachedThermalSconfig) {
                Utils.setPermissions(644, MiscUtils.THERMAL_SCONFIG)
                Utils.writeFile(MiscUtils.THERMAL_SCONFIG, newValue)
                Utils.setPermissions(444, MiscUtils.THERMAL_SCONFIG)
                _thermalSconfig.value = newValue
                cachedThermalSconfig = newValue
            }
        }
    }

    fun updateSchedAutogroup(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = if (isChecked) "1" else "0"
            if (newValue != cachedSchedAutogroup) {
                Utils.writeFile(MiscUtils.SCHED_AUTOGROUP, newValue)
                _schedAutogroup.value = newValue
                cachedSchedAutogroup = newValue
            }
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
            Utils.writeFile(MiscUtils.SWAPPINESS, newValue)
            _swappiness.value = newValue
        }
    }

    fun showPrintkDialog() {
        _showPrintkDialog.value = true
    }

    fun hidePrintkDialog() {
        _showPrintkDialog.value = false
    }

    fun updatePrintk(newValue: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(MiscUtils.PRINTK, newValue)
            _printk.value = newValue
        }
    }
}
