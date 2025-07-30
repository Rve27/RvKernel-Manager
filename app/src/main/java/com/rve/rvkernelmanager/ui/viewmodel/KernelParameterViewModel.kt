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
    data class KernelParameters(
        val schedAutogroup: String = "N/A",
        val hasSchedAutogroup: Boolean = false,
        val swappiness: String = "N/A",
        val hasSwappiness: Boolean = false,
        val printk: String = "N/A",
        val hasPrintk: Boolean = false,
        val zramSize: String = "N/A",
        val hasZramSize: Boolean = false,
        val zramCompAlgorithm: String = "N/A",
        val hasZramCompAlgorithm: Boolean = false,
        val availableZramCompAlgorithms: List<String> = emptyList(),
        val tcpCongestionAlgorithm: String = "N/A",
        val hasTcpCongestionAlgorithm: Boolean = false,
        val availableTcpCongestionAlgorithm: List<String> = emptyList()
    )

    private val _kernelParameters = MutableStateFlow(KernelParameters())
    val kernelParameters: StateFlow<KernelParameters> = _kernelParameters

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
            _kernelParameters.value = KernelParameters(
                schedAutogroup = Utils.readFile(KernelUtils.SCHED_AUTOGROUP),
                hasSchedAutogroup = Utils.testFile(KernelUtils.SCHED_AUTOGROUP),
                printk = Utils.readFile(KernelUtils.PRINTK),
                hasPrintk = Utils.testFile(KernelUtils.PRINTK),
                zramSize = KernelUtils.getZramSize(),
                hasZramSize = Utils.testFile(KernelUtils.ZRAM_SIZE),
                zramCompAlgorithm = KernelUtils.getZramCompAlgorithm(),
                hasZramCompAlgorithm = Utils.testFile(KernelUtils.ZRAM_COMP_ALGORITHM),
                availableZramCompAlgorithms = KernelUtils.getAvailableZramCompAlgorithms(),
                swappiness = Utils.readFile(KernelUtils.SWAPPINESS),
                hasSwappiness = Utils.testFile(KernelUtils.SWAPPINESS),
                tcpCongestionAlgorithm = KernelUtils.getTcpCongestionAlgorithm(),
                hasTcpCongestionAlgorithm = Utils.testFile(KernelUtils.TCP_CONGESTION_ALGORITHM),
                availableTcpCongestionAlgorithm = KernelUtils.getAvailableTcpCongestionAlgorithm()
            )
        }
    }

    fun updateSchedAutogroup(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val value = if (isChecked) "1" else "0"
            Utils.writeFile(KernelUtils.SCHED_AUTOGROUP, value)
            _kernelParameters.value = _kernelParameters.value.copy(
                schedAutogroup = value
            )
        }
    }

    fun updateSwappiness(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(KernelUtils.SWAPPINESS, value)
            _kernelParameters.value = _kernelParameters.value.copy(
                swappiness = value
            )
        }
    }

    fun updatePrintk(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(KernelUtils.PRINTK, value)
            _kernelParameters.value = _kernelParameters.value.copy(
                printk = value
            )
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
            _kernelParameters.value = _kernelParameters.value.copy(
                zramSize = KernelUtils.getZramSize()
            )
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
            _kernelParameters.value = _kernelParameters.value.copy(
                zramCompAlgorithm = KernelUtils.getZramCompAlgorithm()
            )
        }
    }

    fun updateTcpCongestionAlgorithm(algorithm: String) {
        viewModelScope.launch(Dispatchers.IO) {
            KernelUtils.setTcpCongestionAlgorithm(algorithm)
            _kernelParameters.value = _kernelParameters.value.copy(
                tcpCongestionAlgorithm = KernelUtils.getTcpCongestionAlgorithm()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
