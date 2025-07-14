package com.rve.rvkernelmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.channels.Channel
import com.rve.rvkernelmanager.utils.Utils
import com.rve.rvkernelmanager.utils.MiscUtils

class MiscViewModel : ViewModel() {
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

    private val refreshRequests = Channel<Unit>(1)
    var isRefreshing by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
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
            _schedAutogroup.value = Utils.readFile(MiscUtils.SCHED_AUTOGROUP)
            _hasSchedAutogroup.value = Utils.testFile(MiscUtils.SCHED_AUTOGROUP)

            _swappiness.value = Utils.readFile(MiscUtils.SWAPPINESS)
            _hasSwappiness.value = Utils.testFile(MiscUtils.SWAPPINESS)

            _printk.value = Utils.readFile(MiscUtils.PRINTK)
            _hasPrintk.value = Utils.testFile(MiscUtils.PRINTK)
        }
    }

    fun updateSchedAutogroup(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = if (isChecked) "1" else "0"
            Utils.writeFile(MiscUtils.SCHED_AUTOGROUP, newValue)
            _schedAutogroup.value = newValue
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
