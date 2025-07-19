/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package com.rve.rvkernelmanager.ui.viewmodel

import androidx.lifecycle.*
import androidx.compose.runtime.*

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.Channel

import com.rve.rvkernelmanager.utils.*

class KernelParameterViewModel : ViewModel() {
    private val _schedAutogroup = MutableStateFlow("")
    val schedAutogroup: StateFlow<String> = _schedAutogroup

    private val _hasSchedAutogroup = MutableStateFlow(false)
    val hasSchedAutogroup: StateFlow<Boolean> = _hasSchedAutogroup

    private val _swappiness = MutableStateFlow("")
    val swappiness: StateFlow<String> = _swappiness

    private val _hasSwappiness = MutableStateFlow(false)
    val hasSwappiness: StateFlow<Boolean> = _hasSwappiness

    private val _printk = MutableStateFlow("")
    val printk: StateFlow<String> = _printk

    private val _hasPrintk = MutableStateFlow(false)
    val hasPrintk: StateFlow<Boolean> = _hasPrintk

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
        loadKernelParameter()
    }

    fun loadKernelParameter() {
        viewModelScope.launch(Dispatchers.IO) {
            _schedAutogroup.value = Utils.readFile(KernelUtils.SCHED_AUTOGROUP)
            _hasSchedAutogroup.value = Utils.testFile(KernelUtils.SCHED_AUTOGROUP)

            _swappiness.value = Utils.readFile(KernelUtils.SWAPPINESS)
            _hasSwappiness.value = Utils.testFile(KernelUtils.SWAPPINESS)

            _printk.value = Utils.readFile(KernelUtils.PRINTK)
            _hasPrintk.value = Utils.testFile(KernelUtils.PRINTK)
        }
    }

    fun updateSchedAutogroup(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = if (isChecked) "1" else "0"
            Utils.writeFile(KernelUtils.SCHED_AUTOGROUP, newValue)
            _schedAutogroup.value = newValue
        }
    }

    fun updateSwappiness(newValue: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(KernelUtils.SWAPPINESS, newValue)
            _swappiness.value = newValue
        }
    }

    fun updatePrintk(newValue: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(KernelUtils.PRINTK, newValue)
            _printk.value = newValue
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
