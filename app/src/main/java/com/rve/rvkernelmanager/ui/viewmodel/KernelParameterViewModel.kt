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

    private val _zramSize = MutableStateFlow("")
    val zramSize: StateFlow<String> = _zramSize

    private val _hasZramSize = MutableStateFlow(false)
    val hasZramSize: StateFlow<Boolean> = _hasZramSize

    private val _zramCompAlgorithm = MutableStateFlow("")
    val zramCompAlgorithm: StateFlow<String> = _zramCompAlgorithm

    private val _hasZramCompAlgorithm = MutableStateFlow(false)
    val hasZramCompAlgorithm: StateFlow<Boolean> = _hasZramCompAlgorithm

    private val _availableZramCompAlgorithms = MutableStateFlow<List<String>>(emptyList())
    val availableZramCompAlgorithms: StateFlow<List<String>> = _availableZramCompAlgorithms

    private val _tcpCongestionAlgorithm = MutableStateFlow("")
    val tcpCongestionAlgorithm: StateFlow<String> = _tcpCongestionAlgorithm

    private val _hasTcpCongestionAlgorithm = MutableStateFlow(false)
    val hasTcpCongestionAlgorithm: StateFlow<Boolean> = _hasTcpCongestionAlgorithm

    private val _availableTcpCongestionAlgorithm = MutableStateFlow<List<String>>(emptyList())
    val availableTcpCongestionAlgorithm: StateFlow<List<String>> = _availableTcpCongestionAlgorithm

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

            _printk.value = Utils.readFile(KernelUtils.PRINTK)
            _hasPrintk.value = Utils.testFile(KernelUtils.PRINTK)

	    _zramSize.value = KernelUtils.getZramSize()
            _hasZramSize.value = Utils.testFile(KernelUtils.ZRAM_SIZE)

	    _zramCompAlgorithm.value = KernelUtils.getZramCompAlgorithm()
            _hasZramCompAlgorithm.value = Utils.testFile(KernelUtils.ZRAM_COMP_ALGORITHM)
            _availableZramCompAlgorithms.value = KernelUtils.getAvailableZramCompAlgorithms()

	    _swappiness.value = Utils.readFile(KernelUtils.SWAPPINESS)
            _hasSwappiness.value = Utils.testFile(KernelUtils.SWAPPINESS)

	    _tcpCongestionAlgorithm.value = KernelUtils.getTcpCongestionAlgorithm()
            _hasTcpCongestionAlgorithm.value = Utils.testFile(KernelUtils.TCP_CONGESTION_ALGORITHM)
            _availableTcpCongestionAlgorithm.value = KernelUtils.getAvailableTcpCongestionAlgorithm()
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

    fun updateZramSize(sizeInGb: Int) {
	val sizeInBytes = (sizeInGb * 1073741824L).toString()

        viewModelScope.launch(Dispatchers.IO) {
	    KernelUtils.swapoffZram()
	    KernelUtils.resetZram()
            Utils.writeFile(KernelUtils.ZRAM_SIZE, sizeInBytes)
	    KernelUtils.mkswapZram()
	    KernelUtils.swaponZram()
	    _zramSize.value = KernelUtils.getZramSize()
        }
    }

    fun updateZramCompAlgorithm(algorithm: String) {
	val currentSize = Utils.readFile(KernelUtils.ZRAM_SIZE)

        viewModelScope.launch(Dispatchers.IO) {
            KernelUtils.swapoffZram()
            KernelUtils.resetZram()
            KernelUtils.setZramCompAlgorithm(algorithm)
            Utils.writeFile(KernelUtils.ZRAM_SIZE, currentSize)
            KernelUtils.mkswapZram()
            KernelUtils.swaponZram()
            _zramCompAlgorithm.value = KernelUtils.getZramCompAlgorithm()
        }
    }

    fun updateTcpCongestionAlgorithm(algorithm: String) {
        viewModelScope.launch(Dispatchers.IO) {
            KernelUtils.setTcpCongestionAlgorithm(algorithm)
            _tcpCongestionAlgorithm.value = KernelUtils.getTcpCongestionAlgorithm()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
