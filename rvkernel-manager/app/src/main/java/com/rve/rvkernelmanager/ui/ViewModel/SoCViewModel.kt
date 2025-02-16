package com.rve.rvkernelmanager.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.rve.rvkernelmanager.utils.Utils
import com.rve.rvkernelmanager.utils.SoCUtils

class SoCViewModel : ViewModel() {

    data class CPUState(
        val minFreq: String,
        val maxFreq: String,
        val gov: String,
        val availableFreq: List<String>,
        val availableGov: List<String>
    )

    data class GPUState(
        val minFreq: String,
        val maxFreq: String,
        val gov: String,
        val adrenoBoost: String,
        val gpuThrottling: String,
        val availableFreq: List<String>,
        val availableGov: List<String>
    )

    private val _cpu0State = MutableStateFlow(CPUState("", "", "", emptyList(), emptyList()))
    val cpu0State: StateFlow<CPUState> = _cpu0State

    private val _bigClusterState = MutableStateFlow(CPUState("", "", "", emptyList(), emptyList()))
    val bigClusterState: StateFlow<CPUState> = _bigClusterState

    private val _primeClusterState = MutableStateFlow(CPUState("", "", "", emptyList(), emptyList()))
    val primeClusterState: StateFlow<CPUState> = _primeClusterState

    private val _gpuState = MutableStateFlow(GPUState("", "", "", "", "", emptyList(), emptyList()))
    val gpuState: StateFlow<GPUState> = _gpuState

    private val _hasBigCluster = MutableStateFlow(false)
    val hasBigCluster: StateFlow<Boolean> = _hasBigCluster

    private val _hasPrimeCluster = MutableStateFlow(false)
    val hasPrimeCluster: StateFlow<Boolean> = _hasPrimeCluster

    private val _hasAdrenoBoost = MutableStateFlow(false)
    val hasAdrenoBoost: StateFlow<Boolean> = _hasAdrenoBoost

    private val _hasGPUThrottling = MutableStateFlow(false)
    val hasGPUThrottling: StateFlow<Boolean> = _hasGPUThrottling

    private var lastReadTime: Long = 0
    private val cacheDuration = 3000L

    private var pollingJob: Job? = null

    init {
        setPermissionsOnce()
        loadInitialData()
        startPolling()
    }

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
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

    private fun setPermissionsOnce() {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.setPermissions(644, SoCUtils.MIN_FREQ_CPU0_PATH)
            Utils.setPermissions(644, SoCUtils.MAX_FREQ_CPU0_PATH)
            Utils.setPermissions(644, SoCUtils.GOV_CPU0_PATH)
            Utils.setPermissions(644, SoCUtils.AVAILABLE_FREQ_CPU0_PATH)
            Utils.setPermissions(644, SoCUtils.AVAILABLE_GOV_CPU0_PATH)
            Utils.setPermissions(644, SoCUtils.MIN_FREQ_CPU4_PATH)
            Utils.setPermissions(644, SoCUtils.MAX_FREQ_CPU4_PATH)
            Utils.setPermissions(644, SoCUtils.GOV_CPU4_PATH)
            Utils.setPermissions(644, SoCUtils.AVAILABLE_FREQ_CPU4_PATH)
            Utils.setPermissions(644, SoCUtils.AVAILABLE_GOV_CPU4_PATH)
            Utils.setPermissions(644, SoCUtils.MIN_FREQ_CPU7_PATH)
            Utils.setPermissions(644, SoCUtils.MAX_FREQ_CPU7_PATH)
            Utils.setPermissions(644, SoCUtils.GOV_CPU7_PATH)
            Utils.setPermissions(644, SoCUtils.AVAILABLE_FREQ_CPU7_PATH)
            Utils.setPermissions(644, SoCUtils.AVAILABLE_GOV_CPU7_PATH)
            Utils.setPermissions(644, SoCUtils.MIN_FREQ_GPU_PATH)
            Utils.setPermissions(644, SoCUtils.MAX_FREQ_GPU_PATH)
            Utils.setPermissions(644, SoCUtils.GOV_GPU_PATH)
            Utils.setPermissions(644, SoCUtils.ADRENO_BOOST_PATH)
            Utils.setPermissions(644, SoCUtils.GPU_THROTTLING_PATH)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            if (shouldReadFromFile()) {
                withContext(Dispatchers.IO) {
                    val currentMinFreqCPU0 = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU0_PATH)
                    val currentMaxFreqCPU0 = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU0_PATH)
                    val currentGovCPU0 = Utils.readFile(SoCUtils.GOV_CPU0_PATH)
                    val currentAvailableFreqCPU0 = SoCUtils.readAvailableFreqCPU(SoCUtils.AVAILABLE_FREQ_CPU0_PATH)
                    val currentAvailableGovCPU0 = SoCUtils.readAvailableGovCPU(SoCUtils.AVAILABLE_GOV_CPU0_PATH)

                    _cpu0State.value = CPUState(
                        currentMinFreqCPU0,
                        currentMaxFreqCPU0,
                        currentGovCPU0,
                        currentAvailableFreqCPU0,
                        currentAvailableGovCPU0
                    )

                    val bigClusterPath = if (Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU4_PATH)) {
                        SoCUtils.AVAILABLE_FREQ_CPU4_PATH
                    } else if (Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU6_PATH)) {
                        SoCUtils.AVAILABLE_FREQ_CPU6_PATH
                    } else {
                        null
                    }
                    _hasBigCluster.value = bigClusterPath != null

                    if (_hasBigCluster.value) {
                        val basePath = when (bigClusterPath) {
                            SoCUtils.AVAILABLE_FREQ_CPU4_PATH -> "cpu4"
                            SoCUtils.AVAILABLE_FREQ_CPU6_PATH -> "cpu6"
                            else -> null
                        }

                        if (basePath != null) {
                            val minFreqPath = when (basePath) {
                                "cpu4" -> SoCUtils.MIN_FREQ_CPU4_PATH
                                "cpu6" -> SoCUtils.MIN_FREQ_CPU6_PATH
                                else -> null
                            }
                            val maxFreqPath = when (basePath) {
                                "cpu4" -> SoCUtils.MAX_FREQ_CPU4_PATH
                                "cpu6" -> SoCUtils.MAX_FREQ_CPU6_PATH
                                else -> null
                            }
                            val govPath = when (basePath) {
                                "cpu4" -> SoCUtils.GOV_CPU4_PATH
                                "cpu6" -> SoCUtils.GOV_CPU6_PATH
                                else -> null
                            }
                            val availableFreqPath = when (basePath) {
                                "cpu4" -> SoCUtils.AVAILABLE_FREQ_CPU4_PATH
                                "cpu6" -> SoCUtils.AVAILABLE_FREQ_CPU6_PATH
                                else -> null
                            }
                            val availableBoostFreqPath = when (basePath) {
                                "cpu4" -> SoCUtils.AVAILABLE_BOOST_CPU4_PATH
                                "cpu6" -> SoCUtils.AVAILABLE_BOOST_CPU6_PATH
                                else -> null
                            }
                            val availableGovPath = when (basePath) {
                                "cpu4" -> SoCUtils.AVAILABLE_GOV_CPU4_PATH
                                "cpu6" -> SoCUtils.AVAILABLE_GOV_CPU6_PATH
                                else -> null
                            }

                            val currentMinFreq = SoCUtils.readFreqCPU(minFreqPath!!)
                            val currentMaxFreq = SoCUtils.readFreqCPU(maxFreqPath!!)
                            val currentGov = Utils.readFile(govPath!!)
                            val currentAvailableFreq = SoCUtils.readAvailableFreqBoost(availableFreqPath!!, availableBoostFreqPath!!)
                            val currentAvailableGov = SoCUtils.readAvailableGovCPU(availableGovPath!!)

                            _bigClusterState.value = CPUState(
                                currentMinFreq,
                                currentMaxFreq,
                                currentGov,
                                currentAvailableFreq,
                                currentAvailableGov
                            )
                        }
                    }

                    _hasPrimeCluster.value = Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU7_PATH)
                    if (_hasPrimeCluster.value) {
                        val currentMinFreqCPU7 = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU7_PATH)
                        val currentMaxFreqCPU7 = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU7_PATH)
                        val currentGovCPU7 = Utils.readFile(SoCUtils.GOV_CPU7_PATH)
                        val currentAvailableFreqCPU7 = SoCUtils.readAvailableFreqCPU(SoCUtils.AVAILABLE_FREQ_CPU7_PATH)
                        val currentAvailableGovCPU7 = SoCUtils.readAvailableGovCPU(SoCUtils.AVAILABLE_GOV_CPU7_PATH)

                        _primeClusterState.value = CPUState(
                            currentMinFreqCPU7,
                            currentMaxFreqCPU7,
                            currentGovCPU7,
                            currentAvailableFreqCPU7,
                            currentAvailableGovCPU7
                        )
                    }

                    val currentMinFreqGPU = Utils.readFile(SoCUtils.MIN_FREQ_GPU_PATH)
                    val currentMaxFreqGPU = Utils.readFile(SoCUtils.MAX_FREQ_GPU_PATH)
                    val currentGovGPU = Utils.readFile(SoCUtils.GOV_GPU_PATH)
                    val currentAdrenoBoost = Utils.readFile(SoCUtils.ADRENO_BOOST_PATH)
                    val currentGpuThrottling = Utils.readFile(SoCUtils.GPU_THROTTLING_PATH)
                    val currentAvailableFreqGPU = SoCUtils.readAvailableFreqGPU(SoCUtils.AVAILABLE_FREQ_GPU_PATH)
                    val currentAvailableGovGPU = SoCUtils.readAvailableGovGPU(SoCUtils.AVAILABLE_GOV_GPU_PATH)

                    _gpuState.value = GPUState(
                        currentMinFreqGPU,
                        currentMaxFreqGPU,
                        currentGovGPU,
                        currentAdrenoBoost,
                        currentGpuThrottling,
                        currentAvailableFreqGPU,
                        currentAvailableGovGPU
                    )

                    _hasAdrenoBoost.value = Utils.testFile(SoCUtils.ADRENO_BOOST_PATH)
                    _hasGPUThrottling.value = Utils.testFile(SoCUtils.GPU_THROTTLING_PATH)

                    lastReadTime = System.currentTimeMillis()
                }
            }
        }
    }

    private fun shouldReadFromFile(): Boolean {
        return System.currentTimeMillis() - lastReadTime > cacheDuration
    }

    fun updateFreq(target: String, selectedFreq: String, cluster: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (cluster) {
                "little" -> {
                    val path = if (target == "min") SoCUtils.MIN_FREQ_CPU0_PATH else SoCUtils.MAX_FREQ_CPU0_PATH
                    SoCUtils.writeFreqCPU(path, selectedFreq)
                    _cpu0State.value = _cpu0State.value.copy(
                        minFreq = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU0_PATH),
                        maxFreq = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU0_PATH)
                    )
                }
                "big" -> {
                    val minPath = if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.MIN_FREQ_CPU4_PATH)) {
                        SoCUtils.MIN_FREQ_CPU4_PATH
                    } else if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.MIN_FREQ_CPU6_PATH)) {
                        SoCUtils.MIN_FREQ_CPU6_PATH
                    } else {
                        null
                    }
                    val maxPath = if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.MAX_FREQ_CPU4_PATH)) {
                        SoCUtils.MAX_FREQ_CPU4_PATH
                    } else if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.MAX_FREQ_CPU6_PATH)) {
                        SoCUtils.MAX_FREQ_CPU6_PATH
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
                    val path = if (target == "min") SoCUtils.MIN_FREQ_CPU7_PATH else SoCUtils.MAX_FREQ_CPU7_PATH
                    SoCUtils.writeFreqCPU(path, selectedFreq)
                    _primeClusterState.value = _primeClusterState.value.copy(
                        minFreq = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU7_PATH),
                        maxFreq = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU7_PATH)
                    )
                }
                "gpu" -> {
                    val path = if (target == "min") SoCUtils.MIN_FREQ_GPU_PATH else SoCUtils.MAX_FREQ_GPU_PATH
                    SoCUtils.writeFreqGPU(path, selectedFreq)
                    _gpuState.value = _gpuState.value.copy(
                        minFreq = Utils.readFile(SoCUtils.MIN_FREQ_GPU_PATH),
                        maxFreq = Utils.readFile(SoCUtils.MAX_FREQ_GPU_PATH)
                    )
                }
            }
        }
    }

    fun updateGov(selectedGov: String, cluster: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val path = when (cluster) {
                "little" -> SoCUtils.GOV_CPU0_PATH
                "big" -> {
                    if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.GOV_CPU4_PATH)) {
                        SoCUtils.GOV_CPU4_PATH
                    } else if (_hasBigCluster.value == true && Utils.testFile(SoCUtils.GOV_CPU6_PATH)) {
                        SoCUtils.GOV_CPU6_PATH
                    } else {
                        null
                    }
                }
                "prime" -> SoCUtils.GOV_CPU7_PATH
                "gpu" -> SoCUtils.GOV_GPU_PATH
                else -> null
            }

            if (path != null) {
                Utils.writeFile(path, selectedGov)

                when (cluster) {
                    "little" -> {
                        _cpu0State.value = _cpu0State.value.copy(gov = Utils.readFile(SoCUtils.GOV_CPU0_PATH))
                    }
                    "big" -> {
                        _bigClusterState.value = _bigClusterState.value.copy(gov = Utils.readFile(path))
                    }
                    "prime" -> {
                        _primeClusterState.value = _primeClusterState.value.copy(gov = Utils.readFile(SoCUtils.GOV_CPU7_PATH))
                    }
                    "gpu" -> {
                        _gpuState.value = _gpuState.value.copy(gov = Utils.readFile(SoCUtils.GOV_GPU_PATH))
                    }
                }
            }
        }
    }

    fun updateAdrenoBoost(selectedBoost: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(SoCUtils.ADRENO_BOOST_PATH, selectedBoost)
            _gpuState.value = _gpuState.value.copy(adrenoBoost = Utils.readFile(SoCUtils.ADRENO_BOOST_PATH))
        }
    }

    fun updateGPUThrottling(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = if (isChecked) "1" else "0"
            Utils.writeFile(SoCUtils.GPU_THROTTLING_PATH, newValue)
            _gpuState.value = _gpuState.value.copy(gpuThrottling = Utils.readFile(SoCUtils.GPU_THROTTLING_PATH))
        }
    }
}
