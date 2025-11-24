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
    data class KernelProfile(
        val currentProfile: Int = 1,
        val hasProfilePowersave: Boolean = false,
        val hasProfileBalance: Boolean = false,
        val hasProfilePerformance: Boolean = false,
    )

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

    data class BoreScheduler(
        val hasBore: Boolean = false,
        val bore: String = "",
        val hasBurstSmoothnessLong: Boolean = false,
        val burstSmoothnessLong: String = "",
        val hasBurstSmoothnessShort: Boolean = false,
        val burstSmoothnessShort: String = "",
        val hasBurstForkAtavistic: Boolean = false,
        val burstForkAtavistic: String = "",
        val hasBurstPenaltyOffset: Boolean = false,
        val burstPenaltyOffset: String = "",
        val hasBurstPenaltyScale: Boolean = false,
        val burstPenaltyScale: String = "",
        val hasBurstCacheLifetime: Boolean = false,
        val burstCacheLifetime: String = "",
    )

    private val _kernelProfile = MutableStateFlow(KernelProfile())
    val kernelProfile: StateFlow<KernelProfile> = _kernelProfile

    private val _kernelParameters = MutableStateFlow(KernelParameters())
    val kernelParameters: StateFlow<KernelParameters> = _kernelParameters

    private val _uclamp = MutableStateFlow(Uclamp())
    val uclamp: StateFlow<Uclamp> = _uclamp

    private val _memory = MutableStateFlow(Memory())
    val memory: StateFlow<Memory> = _memory

    private val _boreScheduler = MutableStateFlow(BoreScheduler())
    val boreScheduler: StateFlow<BoreScheduler> = _boreScheduler

    private val refreshRequests = Channel<Unit>(1)
    var isRefreshing by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadKernelProfile()
            loadKernelParameter()
            loadUclamp()
            loadMemory()
            loadBoreScheduler()

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
        loadKernelProfile()
        loadKernelParameter()
        loadUclamp()
        loadMemory()
        loadBoreScheduler()
    }

    fun loadKernelProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!Utils.testFile(KernelUtils.KERNEL_PROFILE_PATH)) {
                KernelUtils.mkdirKernelProfilePath()
            }

            if (!Utils.testFile(KernelUtils.KERNEL_PROFILE_CURRENT)) {
                KernelUtils.createCurrentKernelProfileNode()
            }

            _kernelProfile.value = KernelProfile(
                currentProfile = KernelUtils.getKernelProfile(),
                hasProfilePowersave = Utils.testFile(KernelUtils.KERNEL_PROFILE_POWERSAVE),
                hasProfileBalance = Utils.testFile(KernelUtils.KERNEL_PROFILE_BALANCE),
                hasProfilePerformance = Utils.testFile(KernelUtils.KERNEL_PROFILE_PERFORMANCE),
            )
        }
    }

    fun loadKernelParameter() {
        viewModelScope.launch(Dispatchers.IO) {
            _kernelParameters.value = KernelParameters(
                schedAutogroup = Utils.readFile(KernelUtils.SCHED_AUTO_GROUP),
                hasSchedAutogroup = Utils.testFile(KernelUtils.SCHED_AUTO_GROUP),
                printk = Utils.readFile(KernelUtils.PRINTK),
                hasPrintk = Utils.testFile(KernelUtils.PRINTK),
                tcpCongestionAlgorithm = KernelUtils.getTcpCongestionAlgorithm(),
                hasTcpCongestionAlgorithm = Utils.testFile(KernelUtils.TCP_CONGESTION_ALGORITHM),
                availableTcpCongestionAlgorithm = KernelUtils.getAvailableTcpCongestionAlgorithm(),
            )
        }
    }

    fun loadUclamp() {
        viewModelScope.launch(Dispatchers.IO) {
            _uclamp.value = Uclamp(
                hasUclampMax = Utils.testFile(KernelUtils.SCHED_UTIL_CLAMP_MAX),
                uclampMax = Utils.readFile(KernelUtils.SCHED_UTIL_CLAMP_MAX),
                hasUclampMin = Utils.testFile(KernelUtils.SCHED_UTIL_CLAMP_MIN),
                uclampMin = Utils.readFile(KernelUtils.SCHED_UTIL_CLAMP_MIN),
                hasUclampMinRt = Utils.testFile(KernelUtils.SCHED_UTIL_CLAMP_MIN_RT_DEFAULT),
                uclampMinRt = Utils.readFile(KernelUtils.SCHED_UTIL_CLAMP_MIN_RT_DEFAULT),
            )
        }
    }

    fun loadMemory() {
        viewModelScope.launch(Dispatchers.IO) {
            _memory.value = Memory(
                zramSize = KernelUtils.getZramSize(),
                hasZramSize = Utils.testFile(KernelUtils.ZRAM_SIZE),
                zramCompAlgorithm = KernelUtils.getZramCompAlgorithm(),
                hasZramCompAlgorithm = Utils.testFile(KernelUtils.ZRAM_COMP_ALGORITHM),
                availableZramCompAlgorithms = KernelUtils.getAvailableZramCompAlgorithms(),
                swappiness = Utils.readFile(KernelUtils.SWAPPINESS),
                hasSwappiness = Utils.testFile(KernelUtils.SWAPPINESS),
                hasDirtyRatio = Utils.testFile(KernelUtils.DIRTY_RATIO),
                dirtyRatio = Utils.readFile(KernelUtils.DIRTY_RATIO),
            )
        }
    }

    fun loadBoreScheduler() {
        viewModelScope.launch(Dispatchers.IO) {
            _boreScheduler.value = BoreScheduler(
                hasBore = Utils.testFile(KernelUtils.BORE),
                bore = Utils.readFile(KernelUtils.BORE),
                hasBurstSmoothnessLong = Utils.testFile(KernelUtils.BURST_SMOOTHNESS_LONG),
                burstSmoothnessLong = Utils.readFile(KernelUtils.BURST_SMOOTHNESS_LONG),
                hasBurstSmoothnessShort = Utils.testFile(KernelUtils.BURST_SMOOTHNESS_SHORT),
                burstSmoothnessShort = Utils.readFile(KernelUtils.BURST_SMOOTHNESS_SHORT),
                hasBurstForkAtavistic = Utils.testFile(KernelUtils.BURST_FORK_ATAVISTIC),
                burstForkAtavistic = Utils.readFile(KernelUtils.BURST_FORK_ATAVISTIC),
                hasBurstPenaltyOffset = Utils.testFile(KernelUtils.BURST_PENALTY_OFFSET),
                burstPenaltyOffset = Utils.readFile(KernelUtils.BURST_PENALTY_OFFSET),
                hasBurstPenaltyScale = Utils.testFile(KernelUtils.BURST_PENALTY_SCALE),
                burstPenaltyScale = Utils.readFile(KernelUtils.BURST_PENALTY_SCALE),
                hasBurstCacheLifetime = Utils.testFile(KernelUtils.BURST_CACHE_LIFETIME),
                burstCacheLifetime = Utils.readFile(KernelUtils.BURST_CACHE_LIFETIME),
            )
        }
    }

    fun updateProfile(profile: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            KernelUtils.setKernelProfile(profile)
        }
    }

    fun updateSchedAutogroup(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val value = if (isChecked) "1" else "0"
            Utils.writeFile(KernelUtils.SCHED_AUTO_GROUP, value)
            _kernelParameters.value = _kernelParameters.value.copy(
                schedAutogroup = value,
            )
        }
    }

    fun updateSwappiness(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(KernelUtils.SWAPPINESS, value)
            _memory.value = _memory.value.copy(
                swappiness = value,
            )
        }
    }

    fun updatePrintk(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(KernelUtils.PRINTK, value)
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
            Utils.writeFile(KernelUtils.ZRAM_SIZE, sizeInBytes)
            KernelUtils.mkswapZram()
            KernelUtils.swaponZram()
            _memory.value = _memory.value.copy(
                zramSize = KernelUtils.getZramSize(),
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
            Utils.setPermissions(644, KernelUtils.DIRTY_RATIO)
            Utils.writeFile(KernelUtils.DIRTY_RATIO, value)
            _memory.value = _memory.value.copy(
                dirtyRatio = Utils.readFile(KernelUtils.DIRTY_RATIO),
            )
        }
    }

    fun updateBoreStatus(isEnabled: Boolean) {
        val value = if (isEnabled) "1" else "0"
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(KernelUtils.BORE, value)
            _boreScheduler.value = _boreScheduler.value.copy(
                bore = value,
            )
        }
    }

    fun updateBoreParameter(parameter: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val path = when (parameter) {
                "burst_smoothness_long" -> KernelUtils.BURST_SMOOTHNESS_LONG
                "burst_smoothness_short" -> KernelUtils.BURST_SMOOTHNESS_SHORT
                "burst_fork_atavistic" -> KernelUtils.BURST_FORK_ATAVISTIC
                "burst_penalty_offset" -> KernelUtils.BURST_PENALTY_OFFSET
                "burst_penalty_scale" -> KernelUtils.BURST_PENALTY_SCALE
                "burst_cache_lifetime" -> KernelUtils.BURST_CACHE_LIFETIME
                else -> null
            }
            path?.let {
                Utils.writeFile(it, value)
                _boreScheduler.value = when (parameter) {
                    "burst_smoothness_long" -> _boreScheduler.value.copy(burstSmoothnessLong = value)
                    "burst_smoothness_short" -> _boreScheduler.value.copy(burstSmoothnessShort = value)
                    "burst_fork_atavistic" -> _boreScheduler.value.copy(burstForkAtavistic = value)
                    "burst_penalty_offset" -> _boreScheduler.value.copy(burstPenaltyOffset = value)
                    "burst_penalty_scale" -> _boreScheduler.value.copy(burstPenaltyScale = value)
                    "burst_cache_lifetime" -> _boreScheduler.value.copy(burstCacheLifetime = value)
                    else -> _boreScheduler.value
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
