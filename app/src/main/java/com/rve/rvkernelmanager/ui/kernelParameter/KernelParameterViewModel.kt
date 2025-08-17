/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.ui.kernelParameter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rve.rvkernelmanager.utils.KernelUtils
import com.rve.rvkernelmanager.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KernelParameterViewModel : ViewModel() {
    data class KernelParameters(
        val schedAutogroup: String = "N/A",
        val hasSchedAutogroup: Boolean = false,
        val printk: String = "N/A",
        val hasPrintk: Boolean = false,
        val tcpCongestionAlgorithm: String = "N/A",
        val hasTcpCongestionAlgorithm: Boolean = false,
        val availableTcpCongestionAlgorithm: List<String> = emptyList(),
    )

    data class Uclamp(
        val hasUclampMax: Boolean = false,
        val uclampMax: String = "N/A",
        val hasUclampMin: Boolean = false,
        val uclampMin: String = "N/A",
        val hasUclampMinRt: Boolean = false,
        val uclampMinRt: String = "N/A",
    )

    data class Memory(
        val zramSize: String = "N/A",
        val hasZramSize: Boolean = false,
        val zramCompAlgorithm: String = "N/A",
        val hasZramCompAlgorithm: Boolean = false,
        val availableZramCompAlgorithms: List<String> = emptyList(),
        val swappiness: String = "N/A",
        val hasSwappiness: Boolean = false,
        val hasDirtyRatio: Boolean = false,
        val dirtyRatio: String = "N/A",
    )

    private val _kernelParameters = MutableStateFlow(KernelParameters())
    val kernelParameters: StateFlow<KernelParameters> = _kernelParameters

    private val _uclamp = MutableStateFlow(Uclamp())
    val uclamp: StateFlow<Uclamp> = _uclamp

    private val _memory = MutableStateFlow(Memory())
    val memory: StateFlow<Memory> = _memory

    private val refreshRequests = Channel<Unit>(1)
    var isRefreshing by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadKernelParameter()
            loadUclamp()
            loadMemory()

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
        loadUclamp()
        loadMemory()
    }

    fun loadKernelParameter() {
        viewModelScope.launch(Dispatchers.IO) {
            _kernelParameters.value = KernelParameters(
                schedAutogroup = Utils.readFile(KernelUtils.SchedAutoGroup),
                hasSchedAutogroup = Utils.testFile(KernelUtils.SchedAutoGroup),
                printk = Utils.readFile(KernelUtils.Printk),
                hasPrintk = Utils.testFile(KernelUtils.Printk),
                tcpCongestionAlgorithm = KernelUtils.getTcpCongestionAlgorithm(),
                hasTcpCongestionAlgorithm = Utils.testFile(KernelUtils.TcpCongestionAlgorithm),
                availableTcpCongestionAlgorithm = KernelUtils.getAvailableTcpCongestionAlgorithm(),
            )
        }
    }

    fun loadUclamp() {
        viewModelScope.launch(Dispatchers.IO) {
            _uclamp.value = Uclamp(
                hasUclampMax = Utils.testFile(KernelUtils.SchedUtilClampMax),
                uclampMax = Utils.readFile(KernelUtils.SchedUtilClampMax),
                hasUclampMin = Utils.testFile(KernelUtils.SchedUtilClampMin),
                uclampMin = Utils.readFile(KernelUtils.SchedUtilClampMin),
                hasUclampMinRt = Utils.testFile(KernelUtils.SchedUtilClampMinRtDefault),
                uclampMinRt = Utils.readFile(KernelUtils.SchedUtilClampMinRtDefault),
            )
        }
    }

    fun loadMemory() {
        viewModelScope.launch(Dispatchers.IO) {
            _memory.value = Memory(
                zramSize = KernelUtils.getZramSize(),
                hasZramSize = Utils.testFile(KernelUtils.ZramSize),
                zramCompAlgorithm = KernelUtils.getZramCompAlgorithm(),
                hasZramCompAlgorithm = Utils.testFile(KernelUtils.ZramCompAlgorithm),
                availableZramCompAlgorithms = KernelUtils.getAvailableZramCompAlgorithms(),
                swappiness = Utils.readFile(KernelUtils.Swappiness),
                hasSwappiness = Utils.testFile(KernelUtils.Swappiness),
                hasDirtyRatio = Utils.testFile(KernelUtils.DirtyRatio),
                dirtyRatio = Utils.readFile(KernelUtils.DirtyRatio),
            )
        }
    }

    fun updateSchedAutogroup(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val value = if (isChecked) "1" else "0"
            Utils.writeFile(KernelUtils.SchedAutoGroup, value)
            _kernelParameters.value = _kernelParameters.value.copy(
                schedAutogroup = value,
            )
        }
    }

    fun updateSwappiness(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(KernelUtils.Swappiness, value)
            _memory.value = _memory.value.copy(
                swappiness = value,
            )
        }
    }

    fun updatePrintk(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(KernelUtils.Printk, value)
            _kernelParameters.value = _kernelParameters.value.copy(
                printk = value,
            )
        }
    }

    fun updateUclamp(target: String, path: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(path, value)
            _uclamp.value = when (target) {
                "max" -> _uclamp.value.copy(uclampMax = value)
                "min" -> _uclamp.value.copy(uclampMin = value)
                "min_rt" -> _uclamp.value.copy(uclampMinRt = value)
                else -> _uclamp.value
            }
        }
    }

    fun updateZramSize(sizeInGb: Int) {
        val sizeInBytes = (sizeInGb * 1073741824L).toString()
        viewModelScope.launch(Dispatchers.IO) {
            KernelUtils.swapoffZram()
            KernelUtils.resetZram()
            Utils.writeFile(KernelUtils.ZramSize, sizeInBytes)
            KernelUtils.mkswapZram()
            KernelUtils.swaponZram()
            _memory.value = _memory.value.copy(
                zramSize = KernelUtils.getZramSize(),
            )
        }
    }

    fun updateZramCompAlgorithm(algorithm: String) {
        val currentSize = Utils.readFile(KernelUtils.ZramSize)
        viewModelScope.launch(Dispatchers.IO) {
            KernelUtils.swapoffZram()
            KernelUtils.resetZram()
            KernelUtils.setZramCompAlgorithm(algorithm)
            Utils.writeFile(KernelUtils.ZramSize, currentSize)
            KernelUtils.mkswapZram()
            KernelUtils.swaponZram()
            _memory.value = _memory.value.copy(
                zramCompAlgorithm = KernelUtils.getZramCompAlgorithm(),
            )
        }
    }

    fun updateTcpCongestionAlgorithm(algorithm: String) {
        viewModelScope.launch(Dispatchers.IO) {
            KernelUtils.setTcpCongestionAlgorithm(algorithm)
            _kernelParameters.value = _kernelParameters.value.copy(
                tcpCongestionAlgorithm = KernelUtils.getTcpCongestionAlgorithm(),
            )
        }
    }

    fun updateDirtyRatio(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.setPermissions(644, KernelUtils.DirtyRatio)
            Utils.writeFile(KernelUtils.DirtyRatio, value)
            _memory.value = _memory.value.copy(
                dirtyRatio = Utils.readFile(KernelUtils.DirtyRatio),
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
