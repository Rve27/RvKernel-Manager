/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package com.rve.rvkernelmanager.ui.soc

import android.app.Application

import androidx.lifecycle.*

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import com.rve.rvkernelmanager.ui.settings.SettingsPreference
import com.rve.rvkernelmanager.utils.*

class SoCViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsPreference = SettingsPreference.getInstance(application)

    data class CPUState(
        val minFreq: String,
        val maxFreq: String,
        val currentFreq: String,
        val gov: String,
        val availableFreq: List<String>,
        val availableGov: List<String>
    ) {
        companion object {
            val EMPTY = CPUState("N/A", "N/A", "N/A", "N/A", emptyList(), emptyList())
        }
    }

    data class GPUState(
        val minFreq: String,
        val maxFreq: String,
        val currentFreq: String,
        val gov: String,
	val defaultPwrlevel: String,
        val adrenoBoost: String,
        val gpuThrottling: String,
        val availableFreq: List<String>,
        val availableGov: List<String>
    ) {
        companion object {
            val EMPTY = GPUState("N/A", "N/A", "N/A", "N/A", "N/A", "N/A", "0", emptyList(), emptyList())
        }
    }

    sealed class ClusterConfig(
        val name: String,
        val minFreqPath: String,
        val maxFreqPath: String,
        val currentFreqPath: String,
        val govPath: String,
        val availableFreqPath: String,
        val availableGovPath: String,
        val availableBoostFreqPath: String? = null
    ) {
        object Little : ClusterConfig(
            name = "little",
            minFreqPath = SoCUtils.MIN_FREQ_CPU0,
            maxFreqPath = SoCUtils.MAX_FREQ_CPU0,
            currentFreqPath = SoCUtils.CURRENT_FREQ_CPU0,
            govPath = SoCUtils.GOV_CPU0,
            availableFreqPath = SoCUtils.AVAILABLE_FREQ_CPU0,
            availableGovPath = SoCUtils.AVAILABLE_GOV_CPU0
        )

        data class Big(val cpuIndex: Int) : ClusterConfig(
            name = "big",
            minFreqPath = if (cpuIndex == 4) SoCUtils.MIN_FREQ_CPU4 else SoCUtils.MIN_FREQ_CPU6,
            maxFreqPath = if (cpuIndex == 4) SoCUtils.MAX_FREQ_CPU4 else SoCUtils.MAX_FREQ_CPU6,
            currentFreqPath = if (cpuIndex == 4) SoCUtils.CURRENT_FREQ_CPU4 else SoCUtils.CURRENT_FREQ_CPU6,
            govPath = if (cpuIndex == 4) SoCUtils.GOV_CPU4 else SoCUtils.GOV_CPU6,
            availableFreqPath = if (cpuIndex == 4) SoCUtils.AVAILABLE_FREQ_CPU4 else SoCUtils.AVAILABLE_FREQ_CPU6,
            availableGovPath = if (cpuIndex == 4) SoCUtils.AVAILABLE_GOV_CPU4 else SoCUtils.AVAILABLE_GOV_CPU6,
            availableBoostFreqPath = if (cpuIndex == 4) SoCUtils.AVAILABLE_BOOST_CPU4 else SoCUtils.AVAILABLE_BOOST_CPU6
        )

        object Prime : ClusterConfig(
            name = "prime",
            minFreqPath = SoCUtils.MIN_FREQ_CPU7,
            maxFreqPath = SoCUtils.MAX_FREQ_CPU7,
            currentFreqPath = SoCUtils.CURRENT_FREQ_CPU7,
            govPath = SoCUtils.GOV_CPU7,
            availableFreqPath = SoCUtils.AVAILABLE_FREQ_CPU7,
            availableGovPath = SoCUtils.AVAILABLE_GOV_CPU7
        )
    }

    private val _cpu0State = MutableStateFlow(CPUState.EMPTY)
    val cpu0State: StateFlow<CPUState> = _cpu0State

    private val _cpuUsage = MutableStateFlow("N/A")
    val cpuUsage: StateFlow<String> = _cpuUsage

    private val _cpuTemp = MutableStateFlow("N/A")
    val cpuTemp: StateFlow<String> = _cpuTemp

    private val _hasCpuInputBoostMs = MutableStateFlow(false)
    val hasCpuInputBoostMs: StateFlow<Boolean> = _hasCpuInputBoostMs

    private val _cpuInputBoostMs = MutableStateFlow("N/A")
    val cpuInputBoostMs: StateFlow<String> = _cpuInputBoostMs

    private val _hasCpuSchedBoostOnInput = MutableStateFlow(false)
    val hasCpuSchedBoostOnInput: StateFlow<Boolean> = _hasCpuSchedBoostOnInput

    private val _cpuSchedBoostOnInput = MutableStateFlow("0")
    val cpuSchedBoostOnInput: StateFlow<String> = _cpuSchedBoostOnInput

    private val _bigClusterState = MutableStateFlow(CPUState.EMPTY)
    val bigClusterState: StateFlow<CPUState> = _bigClusterState

    private val _primeClusterState = MutableStateFlow(CPUState.EMPTY)
    val primeClusterState: StateFlow<CPUState> = _primeClusterState

    private val _gpuState = MutableStateFlow(GPUState.EMPTY)
    val gpuState: StateFlow<GPUState> = _gpuState

    private val _gpuTemp = MutableStateFlow("N/A")
    val gpuTemp: StateFlow<String> = _gpuTemp

    private val _gpuUsage = MutableStateFlow("N/A")
    val gpuUsage: StateFlow<String> = _gpuUsage

    private val _hasBigCluster = MutableStateFlow(false)
    val hasBigCluster: StateFlow<Boolean> = _hasBigCluster

    private val _hasPrimeCluster = MutableStateFlow(false)
    val hasPrimeCluster: StateFlow<Boolean> = _hasPrimeCluster

    private val _hasDefaultPwrlevel = MutableStateFlow(false)
    val hasDefaultPwrlevel: StateFlow<Boolean> = _hasDefaultPwrlevel

    private val _hasAdrenoBoost = MutableStateFlow(false)
    val hasAdrenoBoost: StateFlow<Boolean> = _hasAdrenoBoost

    private val _hasGPUThrottling = MutableStateFlow(false)
    val hasGPUThrottling: StateFlow<Boolean> = _hasGPUThrottling

    private val _activeStates = MutableStateFlow(
        mapOf(
            "monitor" to true,
            "little" to false,
            "big" to false,
            "prime" to false,
            "gpu" to false,
            "cpuBoost" to false
        )
    )
    private val activeStates: StateFlow<Map<String, Boolean>> = _activeStates

    private var job: Job? = null
    private var detectedBigClusterConfig: ClusterConfig.Big? = null

    init {
        loadSoCData()
    }

    fun setActiveState(state: String, active: Boolean) {
        val currentState = _activeStates.value.toMutableMap()
        currentState[state] = active
        _activeStates.value = currentState
    }

    fun startJob() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            settingsPreference.pollingInterval.collect { interval ->
                while (true) {
                    loadSoCData()
                    delay(interval)
                }
            }
        }
    }

    fun stopJob() {
        job?.cancel()
        job = null
    }

    private fun loadSoCData() {
        viewModelScope.launch(Dispatchers.IO) {
            val activeStates = _activeStates.value

	    detectedBigClusterConfig = detectBigClusterConfig()
	    _hasBigCluster.value = detectedBigClusterConfig != null

	    _hasPrimeCluster.value = Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU7)

	    _hasCpuInputBoostMs.value = Utils.testFile(SoCUtils.CPU_INPUT_BOOST_MS)

	    _hasCpuSchedBoostOnInput.value = Utils.testFile(SoCUtils.CPU_SCHED_BOOST_ON_INPUT)

            if (activeStates["monitor"] == true) {
                loadMonitorData()
            }

            if (activeStates["little"] == true) {
                loadLittleClusterData()
            }

            if (activeStates["big"] == true && _hasBigCluster.value) {
                loadBigClusterData()
            }

            if (activeStates["prime"] == true && _hasPrimeCluster.value) {
                loadPrimeClusterData()
            }

            if (activeStates["gpu"] == true) {
                loadGPUData()
            }

            if (activeStates["cpuBoost"] == true) {
                loadCPUBoostData()
            }
        }
    }

    private suspend fun loadLittleClusterData() {
        _cpu0State.value = loadClusterState(ClusterConfig.Little)
    }

    private suspend fun loadBigClusterData() {
	detectedBigClusterConfig = detectBigClusterConfig()
        detectedBigClusterConfig?.let { config ->
            _bigClusterState.value = loadClusterStateWithBoost(config)
        }
    }

    private suspend fun loadPrimeClusterData() {
        _primeClusterState.value = loadClusterState(ClusterConfig.Prime)
    }

    private suspend fun loadCPUBoostData() {
        if (_hasCpuInputBoostMs.value) {
            _cpuInputBoostMs.value = Utils.readFile(SoCUtils.CPU_INPUT_BOOST_MS)
        }
        if (_hasCpuSchedBoostOnInput.value) {
            _cpuSchedBoostOnInput.value = Utils.readFile(SoCUtils.CPU_SCHED_BOOST_ON_INPUT)
        }
    }

    private suspend fun loadGPUData() {
        val gpuState = GPUState(
            minFreq = Utils.readFile(SoCUtils.MIN_FREQ_GPU),
            maxFreq = Utils.readFile(SoCUtils.MAX_FREQ_GPU),
	    currentFreq = SoCUtils.readFreqGPU(SoCUtils.CURRENT_FREQ_GPU),
            gov = Utils.readFile(SoCUtils.GOV_GPU),
	    defaultPwrlevel = Utils.readFile(SoCUtils.DEFAULT_PWRLEVEL),
            adrenoBoost = Utils.readFile(SoCUtils.ADRENO_BOOST),
            gpuThrottling = Utils.readFile(SoCUtils.GPU_THROTTLING),
            availableFreq = SoCUtils.readAvailableFreqGPU(SoCUtils.AVAILABLE_FREQ_GPU),
            availableGov = SoCUtils.readAvailableGovGPU(SoCUtils.AVAILABLE_GOV_GPU)
        )
        _gpuState.value = gpuState

	_hasDefaultPwrlevel.value = Utils.testFile(SoCUtils.DEFAULT_PWRLEVEL)
        _hasAdrenoBoost.value = Utils.testFile(SoCUtils.ADRENO_BOOST)
        _hasGPUThrottling.value = Utils.testFile(SoCUtils.GPU_THROTTLING)
    }

    private suspend fun loadMonitorData() {
        _cpuUsage.value = SoCUtils.getCpuUsage()
        _cpuTemp.value = Utils.getTemp(SoCUtils.CPU_TEMP)
        _gpuTemp.value = Utils.getTemp(SoCUtils.GPU_TEMP)
        _gpuUsage.value = SoCUtils.getGpuUsage()

	_cpu0State.value = _cpu0State.value.copy(
            currentFreq = SoCUtils.readFreqCPU(ClusterConfig.Little.currentFreqPath)
        )

        if (_hasBigCluster.value) {
            detectedBigClusterConfig?.let { config ->
                _bigClusterState.value = _bigClusterState.value.copy(
                    currentFreq = SoCUtils.readFreqCPU(config.currentFreqPath)
                )
            }
        }

        if (_hasPrimeCluster.value) {
            _primeClusterState.value = _primeClusterState.value.copy(
                currentFreq = SoCUtils.readFreqCPU(ClusterConfig.Prime.currentFreqPath)
            )
        }

        _gpuState.value = _gpuState.value.copy(
            currentFreq = SoCUtils.readFreqGPU(SoCUtils.CURRENT_FREQ_GPU)
        )
    }

    private fun detectBigClusterConfig(): ClusterConfig.Big? {
        return when {
            Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU4) -> ClusterConfig.Big(4)
            Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU6) -> ClusterConfig.Big(6)
            else -> null
        }
    }

    private fun loadClusterState(config: ClusterConfig): CPUState {
        return CPUState(
            minFreq = SoCUtils.readFreqCPU(config.minFreqPath),
            maxFreq = SoCUtils.readFreqCPU(config.maxFreqPath),
            currentFreq = SoCUtils.readFreqCPU(config.currentFreqPath),
            gov = Utils.readFile(config.govPath),
            availableFreq = SoCUtils.readAvailableFreqCPU(config.availableFreqPath),
            availableGov = SoCUtils.readAvailableGovCPU(config.availableGovPath)
        )
    }

    private fun loadClusterStateWithBoost(config: ClusterConfig.Big): CPUState {
        val availableBoostPath = config.availableBoostFreqPath
        val availableFreq = if (availableBoostPath != null) {
            SoCUtils.readAvailableFreqBoost(config.availableFreqPath, availableBoostPath)
        } else {
            SoCUtils.readAvailableFreqCPU(config.availableFreqPath)
        }

        return CPUState(
            minFreq = SoCUtils.readFreqCPU(config.minFreqPath),
            maxFreq = SoCUtils.readFreqCPU(config.maxFreqPath),
            currentFreq = SoCUtils.readFreqCPU(config.currentFreqPath),
            gov = Utils.readFile(config.govPath),
            availableFreq = availableFreq,
            availableGov = SoCUtils.readAvailableGovCPU(config.availableGovPath)
        )
    }

    fun updateFreq(target: String, selectedFreq: String, cluster: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (cluster) {
                ClusterConfig.Little.name -> updateLittleClusterFreq(target, selectedFreq)
                ClusterConfig.Big(4).name, ClusterConfig.Big(6).name -> updateBigClusterFreq(target, selectedFreq)
                ClusterConfig.Prime.name -> updatePrimeClusterFreq(target, selectedFreq)
                "gpu" -> updateGPUFreq(target, selectedFreq)
            }
        }
    }

    private suspend fun updateLittleClusterFreq(target: String, selectedFreq: String) {
        val config = ClusterConfig.Little
        val path = if (target == "min") config.minFreqPath else config.maxFreqPath
        SoCUtils.writeFreqCPU(path, selectedFreq)
        
        _cpu0State.value = _cpu0State.value.copy(
            minFreq = SoCUtils.readFreqCPU(config.minFreqPath),
            maxFreq = SoCUtils.readFreqCPU(config.maxFreqPath)
        )
    }

    private suspend fun updateBigClusterFreq(target: String, selectedFreq: String) {
        val config = detectedBigClusterConfig ?: return
        val path = if (target == "min") config.minFreqPath else config.maxFreqPath
        SoCUtils.writeFreqCPU(path, selectedFreq)
        
        _bigClusterState.value = _bigClusterState.value.copy(
            minFreq = SoCUtils.readFreqCPU(config.minFreqPath),
            maxFreq = SoCUtils.readFreqCPU(config.maxFreqPath)
        )
    }

    private suspend fun updatePrimeClusterFreq(target: String, selectedFreq: String) {
        val config = ClusterConfig.Prime
        val path = if (target == "min") config.minFreqPath else config.maxFreqPath
        SoCUtils.writeFreqCPU(path, selectedFreq)
        
        _primeClusterState.value = _primeClusterState.value.copy(
            minFreq = SoCUtils.readFreqCPU(config.minFreqPath),
            maxFreq = SoCUtils.readFreqCPU(config.maxFreqPath)
        )
    }

    private suspend fun updateGPUFreq(target: String, selectedFreq: String) {
        val path = if (target == "min") SoCUtils.MIN_FREQ_GPU else SoCUtils.MAX_FREQ_GPU
        SoCUtils.writeFreqGPU(path, selectedFreq)
        
        _gpuState.value = _gpuState.value.copy(
            minFreq = Utils.readFile(SoCUtils.MIN_FREQ_GPU),
            maxFreq = Utils.readFile(SoCUtils.MAX_FREQ_GPU)
        )
    }

    fun updateGov(selectedGov: String, cluster: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val governorPath = getGovernorPath(cluster) ?: return@launch
            Utils.writeFile(governorPath, selectedGov)
            updateClusterGovernorState(cluster, governorPath)
        }
    }

    private fun getGovernorPath(cluster: String): String? {
        return when (cluster) {
            ClusterConfig.Little.name -> ClusterConfig.Little.govPath
            ClusterConfig.Big(4).name, ClusterConfig.Big(6).name -> detectedBigClusterConfig?.govPath
            ClusterConfig.Prime.name -> ClusterConfig.Prime.govPath
            "gpu" -> SoCUtils.GOV_GPU
            else -> null
        }
    }

    private suspend fun updateClusterGovernorState(cluster: String, governorPath: String) {
        val newGovernor = Utils.readFile(governorPath)
        when (cluster) {
            ClusterConfig.Little.name -> {
                _cpu0State.value = _cpu0State.value.copy(gov = newGovernor)
            }
            ClusterConfig.Big(4).name, ClusterConfig.Big(6).name -> {
                _bigClusterState.value = _bigClusterState.value.copy(gov = newGovernor)
            }
            ClusterConfig.Prime.name -> {
                _primeClusterState.value = _primeClusterState.value.copy(gov = newGovernor)
            }
            "gpu" -> {
                _gpuState.value = _gpuState.value.copy(gov = newGovernor)
            }
        }
    }

    fun updateCpuInputBoostMs(value: String) {
	viewModelScope.launch(Dispatchers.IO) {
	    Utils.writeFile(SoCUtils.CPU_INPUT_BOOST_MS, value)
	    _cpuInputBoostMs.value = Utils.readFile(SoCUtils.CPU_INPUT_BOOST_MS)
	}
    }

    fun updateCpuSchedBoostOnInput(isEnabled: Boolean) {
	viewModelScope.launch(Dispatchers.IO) {
	    val value = if (isEnabled) "1" else "0"

	    Utils.writeFile(SoCUtils.CPU_SCHED_BOOST_ON_INPUT, value)
	    _cpuSchedBoostOnInput.value = Utils.readFile(SoCUtils.CPU_SCHED_BOOST_ON_INPUT)
	}
    }

    fun updateDefaultPwrlevel(value: String) {
	viewModelScope.launch(Dispatchers.IO) {
	    Utils.writeFile(SoCUtils.DEFAULT_PWRLEVEL, value)
	    _gpuState.value = _gpuState.value.copy(
		defaultPwrlevel = Utils.readFile(SoCUtils.DEFAULT_PWRLEVEL)
	    )
	}
    }

    fun updateAdrenoBoost(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(SoCUtils.ADRENO_BOOST, value)
            _gpuState.value = _gpuState.value.copy(
                adrenoBoost = Utils.readFile(SoCUtils.ADRENO_BOOST)
            )
        }
    }

    fun updateGPUThrottling(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = if (isChecked) "1" else "0"
            Utils.writeFile(SoCUtils.GPU_THROTTLING, newValue)
            _gpuState.value = _gpuState.value.copy(
                gpuThrottling = Utils.readFile(SoCUtils.GPU_THROTTLING)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
	viewModelScope.cancel()
        stopJob()
    }
}
