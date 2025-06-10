package com.rve.rvkernelmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.cancel
import com.rve.rvkernelmanager.utils.Utils
import com.rve.rvkernelmanager.utils.SoCUtils

class SoCViewModel : ViewModel() {

    data class CPUState(
        val minFreq: String,
        val maxFreq: String,
	val currentFreq: String,
        val gov: String,
        val availableFreq: List<String>,
        val availableGov: List<String>
    )

    data class GPUState(
        val minFreq: String,
        val maxFreq: String,
	val currentFreq: String,
        val gov: String,
        val adrenoBoost: String,
        val gpuThrottling: String,
        val availableFreq: List<String>,
        val availableGov: List<String>
    )

    private val _cpu0State = MutableStateFlow(CPUState("", "", "", "", emptyList(), emptyList()))
    val cpu0State: StateFlow<CPUState> = _cpu0State

    private val _cpuUsage = MutableStateFlow("")
    val cpuUsage: StateFlow<String> = _cpuUsage

    private val _bigClusterState = MutableStateFlow(CPUState("", "", "", "", emptyList(), emptyList()))
    val bigClusterState: StateFlow<CPUState> = _bigClusterState

    private val _primeClusterState = MutableStateFlow(CPUState("", "", "", "", emptyList(), emptyList()))
    val primeClusterState: StateFlow<CPUState> = _primeClusterState

    private val _gpuState = MutableStateFlow(GPUState("", "", "", "", "", "", emptyList(), emptyList()))
    val gpuState: StateFlow<GPUState> = _gpuState

    private val _hasBigCluster = MutableStateFlow(false)
    val hasBigCluster: StateFlow<Boolean> = _hasBigCluster

    private val _hasPrimeCluster = MutableStateFlow(false)
    val hasPrimeCluster: StateFlow<Boolean> = _hasPrimeCluster

    private val _hasAdrenoBoost = MutableStateFlow(false)
    val hasAdrenoBoost: StateFlow<Boolean> = _hasAdrenoBoost

    private val _hasGPUThrottling = MutableStateFlow(false)
    val hasGPUThrottling: StateFlow<Boolean> = _hasGPUThrottling

    private var pollingJob: Job? = null

    init {
        loadInitialData()
    }

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                loadInitialData()
                delay(1000)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentMinFreqCPU0 = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU0)
            val currentMaxFreqCPU0 = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU0)
	    val currentFreqCPU0 = SoCUtils.readFreqCPU(SoCUtils.CURRENT_FREQ_CPU0)
            val currentGovCPU0 = Utils.readFile(SoCUtils.GOV_CPU0)
            val currentAvailableFreqCPU0 = SoCUtils.readAvailableFreqCPU(SoCUtils.AVAILABLE_FREQ_CPU0)
            val currentAvailableGovCPU0 = SoCUtils.readAvailableGovCPU(SoCUtils.AVAILABLE_GOV_CPU0)

            _cpu0State.value = CPUState(
                currentMinFreqCPU0,
                currentMaxFreqCPU0,
		currentFreqCPU0,
                currentGovCPU0,
                currentAvailableFreqCPU0,
                currentAvailableGovCPU0
            )

	    _cpuUsage.value = SoCUtils.getCpuUsage()

            val bigClusterPath = if (Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU4)) {
                SoCUtils.AVAILABLE_FREQ_CPU4
            } else if (Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU6)) {
                SoCUtils.AVAILABLE_FREQ_CPU6
            } else {
                null
            }
            _hasBigCluster.value = bigClusterPath != null

            if (_hasBigCluster.value) {
                val basePath = when (bigClusterPath) {
                    SoCUtils.AVAILABLE_FREQ_CPU4 -> "cpu4"
                    SoCUtils.AVAILABLE_FREQ_CPU6 -> "cpu6"
                    else -> null
                }

                if (basePath != null) {
                    val minFreqPath = when (basePath) {
                        "cpu4" -> SoCUtils.MIN_FREQ_CPU4
                        "cpu6" -> SoCUtils.MIN_FREQ_CPU6
                        else -> null
                    }
                    val maxFreqPath = when (basePath) {
                        "cpu4" -> SoCUtils.MAX_FREQ_CPU4
                        "cpu6" -> SoCUtils.MAX_FREQ_CPU6
                        else -> null
                    }
		    val currentFreqPath = when (basePath) {
			"cpu4" -> SoCUtils.CURRENT_FREQ_CPU4
			"cpu6" -> SoCUtils.CURRENT_FREQ_CPU6
			else -> null
		    }
                    val govPath = when (basePath) {
                        "cpu4" -> SoCUtils.GOV_CPU4
                        "cpu6" -> SoCUtils.GOV_CPU6
                        else -> null
                    }
                    val availableFreqPath = when (basePath) {
                        "cpu4" -> SoCUtils.AVAILABLE_FREQ_CPU4
                        "cpu6" -> SoCUtils.AVAILABLE_FREQ_CPU6
                        else -> null
                    }
                    val availableBoostFreqPath = when (basePath) {
                        "cpu4" -> SoCUtils.AVAILABLE_BOOST_CPU4
                        "cpu6" -> SoCUtils.AVAILABLE_BOOST_CPU6
                        else -> null
                    }
                    val availableGovPath = when (basePath) {
                        "cpu4" -> SoCUtils.AVAILABLE_GOV_CPU4
                        "cpu6" -> SoCUtils.AVAILABLE_GOV_CPU6
                        else -> null
                    }

                    val currentMinFreq = SoCUtils.readFreqCPU(minFreqPath!!)
                    val currentMaxFreq = SoCUtils.readFreqCPU(maxFreqPath!!)
		    val currentFreq = SoCUtils.readFreqCPU(currentFreqPath!!)
                    val currentGov = Utils.readFile(govPath!!)
                    val currentAvailableFreq = SoCUtils.readAvailableFreqBoost(availableFreqPath!!, availableBoostFreqPath!!)
                    val currentAvailableGov = SoCUtils.readAvailableGovCPU(availableGovPath!!)

                    _bigClusterState.value = CPUState(
                        currentMinFreq,
                        currentMaxFreq,
			currentFreq,
                        currentGov,
                        currentAvailableFreq,
                        currentAvailableGov
                    )
                }
            }

            _hasPrimeCluster.value = Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU7)
            if (_hasPrimeCluster.value) {
                val currentMinFreqCPU7 = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU7)
                val currentMaxFreqCPU7 = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU7)
		val currentFreqCPU7 = SoCUtils.readFreqCPU(SoCUtils.CURRENT_FREQ_CPU7)
                val currentGovCPU7 = Utils.readFile(SoCUtils.GOV_CPU7)
                val currentAvailableFreqCPU7 = SoCUtils.readAvailableFreqCPU(SoCUtils.AVAILABLE_FREQ_CPU7)
                val currentAvailableGovCPU7 = SoCUtils.readAvailableGovCPU(SoCUtils.AVAILABLE_GOV_CPU7)

                _primeClusterState.value = CPUState(
                    currentMinFreqCPU7,
                    currentMaxFreqCPU7,
		    currentFreqCPU7,
                    currentGovCPU7,
                    currentAvailableFreqCPU7,
                    currentAvailableGovCPU7
                )
            }

            val currentMinFreqGPU = Utils.readFile(SoCUtils.MIN_FREQ_GPU)
            val currentMaxFreqGPU = Utils.readFile(SoCUtils.MAX_FREQ_GPU)
	    val currentFreqGPU = SoCUtils.readFreqGPU(SoCUtils.CURRENT_FREQ_GPU)
            val currentGovGPU = Utils.readFile(SoCUtils.GOV_GPU)
            val currentAdrenoBoost = Utils.readFile(SoCUtils.ADRENO_BOOST)
            val currentGpuThrottling = Utils.readFile(SoCUtils.GPU_THROTTLING)
            val currentAvailableFreqGPU = SoCUtils.readAvailableFreqGPU(SoCUtils.AVAILABLE_FREQ_GPU)
            val currentAvailableGovGPU = SoCUtils.readAvailableGovGPU(SoCUtils.AVAILABLE_GOV_GPU)

            _gpuState.value = GPUState(
                currentMinFreqGPU,
                currentMaxFreqGPU,
		currentFreqGPU,
                currentGovGPU,
                currentAdrenoBoost,
                currentGpuThrottling,
                currentAvailableFreqGPU,
                currentAvailableGovGPU
            )

            _hasAdrenoBoost.value = Utils.testFile(SoCUtils.ADRENO_BOOST)
            _hasGPUThrottling.value = Utils.testFile(SoCUtils.GPU_THROTTLING)
        }
    }

    fun updateFreq(target: String, selectedFreq: String, cluster: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (cluster) {
                "little" -> {
                    val path = if (target == "min") SoCUtils.MIN_FREQ_CPU0 else SoCUtils.MAX_FREQ_CPU0
                    SoCUtils.writeFreqCPU(path, selectedFreq)
                    _cpu0State.value = _cpu0State.value.copy(
                        minFreq = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU0),
                        maxFreq = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU0)
                    )
                }
                "big" -> {
                    val minPath = if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.MIN_FREQ_CPU4)) {
                        SoCUtils.MIN_FREQ_CPU4
                    } else if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.MIN_FREQ_CPU6)) {
                        SoCUtils.MIN_FREQ_CPU6
                    } else {
                        null
                    }
                    val maxPath = if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.MAX_FREQ_CPU4)) {
                        SoCUtils.MAX_FREQ_CPU4
                    } else if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.MAX_FREQ_CPU6)) {
                        SoCUtils.MAX_FREQ_CPU6
                    } else {
                        null
                    }

                    if (minPath != null && maxPath != null) {
                        val path = if (target == "min") minPath else maxPath
                        SoCUtils.writeFreqCPU(path, selectedFreq)

                        _bigClusterState.value = _bigClusterState.value.copy(
                            minFreq = SoCUtils.readFreqCPU(minPath),
                            maxFreq = SoCUtils.readFreqCPU(maxPath)
                        )
                    }
                }
                "prime" -> {
                    val path = if (target == "min") SoCUtils.MIN_FREQ_CPU7 else SoCUtils.MAX_FREQ_CPU7
                    SoCUtils.writeFreqCPU(path, selectedFreq)
                    _primeClusterState.value = _primeClusterState.value.copy(
                        minFreq = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU7),
                        maxFreq = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU7)
                    )
                }
                "gpu" -> {
                    val path = if (target == "min") SoCUtils.MIN_FREQ_GPU else SoCUtils.MAX_FREQ_GPU
                    SoCUtils.writeFreqGPU(path, selectedFreq)
                    _gpuState.value = _gpuState.value.copy(
                        minFreq = Utils.readFile(SoCUtils.MIN_FREQ_GPU),
                        maxFreq = Utils.readFile(SoCUtils.MAX_FREQ_GPU)
                    )
                }
            }
        }
    }

    fun updateGov(selectedGov: String, cluster: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val path = when (cluster) {
                "little" -> SoCUtils.GOV_CPU0
                "big" -> {
                    if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.GOV_CPU4)) {
                        SoCUtils.GOV_CPU4
                    } else if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.GOV_CPU6)) {
                        SoCUtils.GOV_CPU6
                    } else {
                        null
                    }
                }
                "prime" -> SoCUtils.GOV_CPU7
                "gpu" -> SoCUtils.GOV_GPU
                else -> null
            }

            if (path != null) {
                Utils.writeFile(path, selectedGov)

                when (cluster) {
                    "little" -> {
                        _cpu0State.value = _cpu0State.value.copy(gov = Utils.readFile(SoCUtils.GOV_CPU0))
                    }
                    "big" -> {
                        _bigClusterState.value = _bigClusterState.value.copy(gov = Utils.readFile(path))
                    }
                    "prime" -> {
                        _primeClusterState.value = _primeClusterState.value.copy(gov = Utils.readFile(SoCUtils.GOV_CPU7))
                    }
                    "gpu" -> {
                        _gpuState.value = _gpuState.value.copy(gov = Utils.readFile(SoCUtils.GOV_GPU))
                    }
                }
            }
        }
    }

    fun updateAdrenoBoost(selectedBoost: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(SoCUtils.ADRENO_BOOST, selectedBoost)
            _gpuState.value = _gpuState.value.copy(adrenoBoost = Utils.readFile(SoCUtils.ADRENO_BOOST))
        }
    }

    fun updateGPUThrottling(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = if (isChecked) "1" else "0"
            Utils.writeFile(SoCUtils.GPU_THROTTLING, newValue)
            _gpuState.value = _gpuState.value.copy(gpuThrottling = Utils.readFile(SoCUtils.GPU_THROTTLING))
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
