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

    private val _cpu0State = MutableStateFlow(CPUState("N/A", "N/A", "N/A", "N/A", emptyList(), emptyList()))
    val cpu0State: StateFlow<CPUState> = _cpu0State

    private val _cpuUsage = MutableStateFlow("N/A")
    val cpuUsage: StateFlow<String> = _cpuUsage

    private val _cpuTemp = MutableStateFlow("N/A")
    val cpuTemp: StateFlow<String> = _cpuTemp

    private val _bigClusterState = MutableStateFlow(CPUState("N/A", "N/A", "N/A", "N/A", emptyList(), emptyList()))
    val bigClusterState: StateFlow<CPUState> = _bigClusterState

    private val _primeClusterState = MutableStateFlow(CPUState("N/A", "N/A", "N/A", "N/A", emptyList(), emptyList()))
    val primeClusterState: StateFlow<CPUState> = _primeClusterState

    private val _gpuState = MutableStateFlow(GPUState("N/A", "N/A", "N/A", "N/A", "N/A", "N/A", emptyList(), emptyList()))
    val gpuState: StateFlow<GPUState> = _gpuState

    private val _gpuTemp = MutableStateFlow("N/A")
    val gpuTemp: StateFlow<String> = _gpuTemp

    private val _gpuUsage = MutableStateFlow("N/A")
    val gpuUsage: StateFlow<String> = _gpuUsage

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
                delay(3000)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val minFreqCPU0 = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU0)
            val maxFreqCPU0 = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU0)
	    val currentFreqCPU0 = SoCUtils.readFreqCPU(SoCUtils.CURRENT_FREQ_CPU0)
            val govCPU0 = Utils.readFile(SoCUtils.GOV_CPU0)
            val availableFreqCPU0 = SoCUtils.readAvailableFreqCPU(SoCUtils.AVAILABLE_FREQ_CPU0)
            val availableGovCPU0 = SoCUtils.readAvailableGovCPU(SoCUtils.AVAILABLE_GOV_CPU0)

            _cpu0State.value = CPUState(
                minFreqCPU0,
                maxFreqCPU0,
		currentFreqCPU0,
                govCPU0,
                availableFreqCPU0,
                availableGovCPU0
            )

	    _cpuUsage.value = SoCUtils.getCpuUsage()
	    _cpuTemp.value = Utils.getTemp(SoCUtils.CPU_TEMP)

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

                    val minFreq = SoCUtils.readFreqCPU(minFreqPath!!)
                    val maxFreq = SoCUtils.readFreqCPU(maxFreqPath!!)
		    val currentFreq = SoCUtils.readFreqCPU(currentFreqPath!!)
                    val gov = Utils.readFile(govPath!!)
                    val availableFreq = SoCUtils.readAvailableFreqBoost(availableFreqPath!!, availableBoostFreqPath!!)
                    val availableGov = SoCUtils.readAvailableGovCPU(availableGovPath!!)

                    _bigClusterState.value = CPUState(
                        minFreq,
                        maxFreq,
			currentFreq,
                        gov,
                        availableFreq,
                        availableGov
                    )
                }
            }

            _hasPrimeCluster.value = Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU7)
            if (_hasPrimeCluster.value) {
                val minFreqCPU7 = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU7)
                val maxFreqCPU7 = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU7)
		val currentFreqCPU7 = SoCUtils.readFreqCPU(SoCUtils.CURRENT_FREQ_CPU7)
                val govCPU7 = Utils.readFile(SoCUtils.GOV_CPU7)
                val availableFreqCPU7 = SoCUtils.readAvailableFreqCPU(SoCUtils.AVAILABLE_FREQ_CPU7)
                val availableGovCPU7 = SoCUtils.readAvailableGovCPU(SoCUtils.AVAILABLE_GOV_CPU7)

                _primeClusterState.value = CPUState(
                    minFreqCPU7,
                    maxFreqCPU7,
		    currentFreqCPU7,
                    govCPU7,
                    availableFreqCPU7,
                    availableGovCPU7
                )
            }

            val minFreqGPU = Utils.readFile(SoCUtils.MIN_FREQ_GPU)
            val maxFreqGPU = Utils.readFile(SoCUtils.MAX_FREQ_GPU)
	    val currentFreqGPU = SoCUtils.readFreqGPU(SoCUtils.CURRENT_FREQ_GPU)
            val govGPU = Utils.readFile(SoCUtils.GOV_GPU)
            val adrenoBoost = Utils.readFile(SoCUtils.ADRENO_BOOST)
            val gpuThrottling = Utils.readFile(SoCUtils.GPU_THROTTLING)
            val availableFreqGPU = SoCUtils.readAvailableFreqGPU(SoCUtils.AVAILABLE_FREQ_GPU)
            val availableGovGPU = SoCUtils.readAvailableGovGPU(SoCUtils.AVAILABLE_GOV_GPU)

            _gpuState.value = GPUState(
                minFreqGPU,
                maxFreqGPU,
		currentFreqGPU,
                govGPU,
                adrenoBoost,
                gpuThrottling,
                availableFreqGPU,
                availableGovGPU
            )

	    _gpuTemp.value = Utils.getTemp(SoCUtils.GPU_TEMP)
	    _gpuUsage.value = SoCUtils.getGpuUsage()
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

    fun updateAdrenoBoost(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(SoCUtils.ADRENO_BOOST, value)
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
