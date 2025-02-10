package com.rve.rvkernelmanager.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.rve.rvkernelmanager.utils.*

class SoCViewModel : ViewModel() {

    private val _minFreqCPU0 = MutableStateFlow("")
    val minFreqCPU0: StateFlow<String> = _minFreqCPU0

    private val _maxFreqCPU0 = MutableStateFlow("")
    val maxFreqCPU0: StateFlow<String> = _maxFreqCPU0

    private val _govCPU0 = MutableStateFlow("")
    val govCPU0: StateFlow<String> = _govCPU0

    private val _availableFreqCPU0 = MutableStateFlow(listOf<String>())
    val availableFreqCPU0: StateFlow<List<String>> = _availableFreqCPU0

    private val _availableGovCPU0 = MutableStateFlow(listOf<String>())
    val availableGovCPU0: StateFlow<List<String>> = _availableGovCPU0

    private val _minFreqCPU4 = MutableStateFlow("")
    val minFreqCPU4: StateFlow<String> = _minFreqCPU4

    private val _maxFreqCPU4 = MutableStateFlow("")
    val maxFreqCPU4: StateFlow<String> = _maxFreqCPU4

    private val _govCPU4 = MutableStateFlow("")
    val govCPU4: StateFlow<String> = _govCPU4

    private val _availableFreqCPU4 = MutableStateFlow(listOf<String>())
    val availableFreqCPU4: StateFlow<List<String>> = _availableFreqCPU4

    private val _availableGovCPU4 = MutableStateFlow(listOf<String>())
    val availableGovCPU4: StateFlow<List<String>> = _availableGovCPU4

    private val _minFreqCPU7 = MutableStateFlow("")
    val minFreqCPU7: StateFlow<String> = _minFreqCPU7

    private val _maxFreqCPU7 = MutableStateFlow("")
    val maxFreqCPU7: StateFlow<String> = _maxFreqCPU7

    private val _govCPU7 = MutableStateFlow("")
    val govCPU7: StateFlow<String> = _govCPU7

    private val _availableFreqCPU7 = MutableStateFlow(listOf<String>())
    val availableFreqCPU7: StateFlow<List<String>> = _availableFreqCPU7

    private val _availableGovCPU7 = MutableStateFlow(listOf<String>())
    val availableGovCPU7: StateFlow<List<String>> = _availableGovCPU7

    private val _minFreqGPU = MutableStateFlow("")
    val minFreqGPU: StateFlow<String> = _minFreqGPU

    private val _maxFreqGPU = MutableStateFlow("")
    val maxFreqGPU: StateFlow<String> = _maxFreqGPU

    private val _govGPU = MutableStateFlow("")
    val govGPU: StateFlow<String> = _govGPU

    private val _adrenoBoost = MutableStateFlow("")
    val adrenoBoost: StateFlow<String> = _adrenoBoost

    private val _gpuThrottling = MutableStateFlow("")
    val gpuThrottling: StateFlow<String> = _gpuThrottling

    private val _availableFreqGPU = MutableStateFlow(listOf<String>())
    val availableFreqGPU: StateFlow<List<String>> = _availableFreqGPU

    private val _availableGovGPU = MutableStateFlow(listOf<String>())
    val availableGovGPU: StateFlow<List<String>> = _availableGovGPU

    private val _hasBigCluster = MutableStateFlow(false)
    val hasBigCluster: StateFlow<Boolean> = _hasBigCluster

    private val _hasPrimeCluster = MutableStateFlow(false)
    val hasPrimeCluster: StateFlow<Boolean> = _hasPrimeCluster

    private val _hasAdrenoBoost = MutableStateFlow(false)
    val hasAdrenoBoost: StateFlow<Boolean> = _hasAdrenoBoost

    private val _hasGPUThrottling = MutableStateFlow(false)
    val hasGPUThrottling: StateFlow<Boolean> = _hasGPUThrottling

    private var cachedMinFreqCPU0: String? = null
    private var cachedMaxFreqCPU0: String? = null
    private var cachedGovCPU0: String? = null
    private var cachedAvailableFreqCPU0: List<String>? = null
    private var cachedAvailableGovCPU0: List<String>? = null

    private var cachedMinFreqCPU4: String? = null
    private var cachedMaxFreqCPU4: String? = null
    private var cachedGovCPU4: String? = null
    private var cachedAvailableFreqCPU4: List<String>? = null
    private var cachedAvailableGovCPU4: List<String>? = null

    private var cachedMinFreqCPU7: String? = null
    private var cachedMaxFreqCPU7: String? = null
    private var cachedGovCPU7: String? = null
    private var cachedAvailableFreqCPU7: List<String>? = null
    private var cachedAvailableGovCPU7: List<String>? = null

    private var cachedMinFreqGPU: String? = null
    private var cachedMaxFreqGPU: String? = null
    private var cachedGovGPU: String? = null
    private var cachedAdrenoBoost: String? = null
    private var cachedGpuThrottling: String? = null
    private var cachedAvailableFreqGPU: List<String>? = null
    private var cachedAvailableGovGPU: List<String>? = null

    private var pollingJob: Job? = null

    init {
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

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentMinFreqCPU0 = readFreqCPU(MIN_FREQ_CPU0_PATH)
            if (currentMinFreqCPU0 != cachedMinFreqCPU0) {
                _minFreqCPU0.value = currentMinFreqCPU0
                cachedMinFreqCPU0 = currentMinFreqCPU0
            }

            val currentMaxFreqCPU0 = readFreqCPU(MAX_FREQ_CPU0_PATH)
            if (currentMaxFreqCPU0 != cachedMaxFreqCPU0) {
                _maxFreqCPU0.value = currentMaxFreqCPU0
                cachedMaxFreqCPU0 = currentMaxFreqCPU0
            }

            val currentGovCPU0 = readFile(GOV_CPU0_PATH)
            if (currentGovCPU0 != cachedGovCPU0) {
                _govCPU0.value = currentGovCPU0
                cachedGovCPU0 = currentGovCPU0
            }

            val currentAvailableFreqCPU0 = readAvailableFreqCPU(AVAILABLE_FREQ_CPU0_PATH)
            if (currentAvailableFreqCPU0 != cachedAvailableFreqCPU0) {
                _availableFreqCPU0.value = currentAvailableFreqCPU0
                cachedAvailableFreqCPU0 = currentAvailableFreqCPU0
            }

            val currentAvailableGovCPU0 = readAvailableGovCPU(AVAILABLE_GOV_CPU0_PATH)
            if (currentAvailableGovCPU0 != cachedAvailableGovCPU0) {
                _availableGovCPU0.value = currentAvailableGovCPU0
                cachedAvailableGovCPU0 = currentAvailableGovCPU0
            }

            _hasBigCluster.value = testFile(AVAILABLE_FREQ_CPU4_PATH)
            if (_hasBigCluster.value) {
                setPermissions(644, AVAILABLE_FREQ_CPU4_PATH)
                setPermissions(644, MIN_FREQ_CPU4_PATH)
                setPermissions(644, MAX_FREQ_CPU4_PATH)
                setPermissions(644, AVAILABLE_GOV_CPU4_PATH)
                setPermissions(644, GOV_CPU4_PATH)

                val currentMinFreqCPU4 = readFreqCPU(MIN_FREQ_CPU4_PATH)
                if (currentMinFreqCPU4 != cachedMinFreqCPU4) {
                    _minFreqCPU4.value = currentMinFreqCPU4
                    cachedMinFreqCPU4 = currentMinFreqCPU4
                }

                val currentMaxFreqCPU4 = readFreqCPU(MAX_FREQ_CPU4_PATH)
                if (currentMaxFreqCPU4 != cachedMaxFreqCPU4) {
                    _maxFreqCPU4.value = currentMaxFreqCPU4
                    cachedMaxFreqCPU4 = currentMaxFreqCPU4
                }

                val currentGovCPU4 = readFile(GOV_CPU4_PATH)
                if (currentGovCPU4 != cachedGovCPU4) {
                    _govCPU4.value = currentGovCPU4
                    cachedGovCPU4 = currentGovCPU4
                }

                val currentAvailableFreqCPU4 = readAvailableFreqBoost(AVAILABLE_FREQ_CPU4_PATH, AVAILABLE_BOOST_CPU4_PATH)
                if (currentAvailableFreqCPU4 != cachedAvailableFreqCPU4) {
                    _availableFreqCPU4.value = currentAvailableFreqCPU4
                    cachedAvailableFreqCPU4 = currentAvailableFreqCPU4
                }

                val currentAvailableGovCPU4 = readAvailableGovCPU(AVAILABLE_GOV_CPU4_PATH)
                if (currentAvailableGovCPU4 != cachedAvailableGovCPU4) {
                    _availableGovCPU4.value = currentAvailableGovCPU4
                    cachedAvailableGovCPU4 = currentAvailableGovCPU4
                }
            }

            _hasPrimeCluster.value = testFile(AVAILABLE_FREQ_CPU7_PATH)
            if (_hasPrimeCluster.value) {
                setPermissions(644, AVAILABLE_FREQ_CPU7_PATH)
                setPermissions(644, MIN_FREQ_CPU7_PATH)
                setPermissions(644, MAX_FREQ_CPU7_PATH)
                setPermissions(644, AVAILABLE_GOV_CPU7_PATH)
                setPermissions(644, GOV_CPU7_PATH)

                val currentMinFreqCPU7 = readFreqCPU(MIN_FREQ_CPU7_PATH)
                if (currentMinFreqCPU7 != cachedMinFreqCPU7) {
                    _minFreqCPU7.value = currentMinFreqCPU7
                    cachedMinFreqCPU7 = currentMinFreqCPU7
                }

                val currentMaxFreqCPU7 = readFreqCPU(MAX_FREQ_CPU7_PATH)
                if (currentMaxFreqCPU7 != cachedMaxFreqCPU7) {
                    _maxFreqCPU7.value = currentMaxFreqCPU7
                    cachedMaxFreqCPU7 = currentMaxFreqCPU7
                }

                val currentGovCPU7 = readFile(GOV_CPU7_PATH)
                if (currentGovCPU7 != cachedGovCPU7) {
                    _govCPU7.value = currentGovCPU7
                    cachedGovCPU7 = currentGovCPU7
                }

                val currentAvailableFreqCPU7 = readAvailableFreqBoost(AVAILABLE_FREQ_CPU7_PATH, AVAILABLE_BOOST_CPU7_PATH)
                if (currentAvailableFreqCPU7 != cachedAvailableFreqCPU7) {
                    _availableFreqCPU7.value = currentAvailableFreqCPU7
                    cachedAvailableFreqCPU7 = currentAvailableFreqCPU7
                }

                val currentAvailableGovCPU7 = readAvailableGovCPU(AVAILABLE_GOV_CPU7_PATH)
                if (currentAvailableGovCPU7 != cachedAvailableGovCPU7) {
                    _availableGovCPU7.value = currentAvailableGovCPU7
                    cachedAvailableGovCPU7 = currentAvailableGovCPU7
                }
            }

            setPermissions(644, AVAILABLE_FREQ_GPU_PATH)
            setPermissions(644, MIN_FREQ_GPU_PATH)
            setPermissions(644, MAX_FREQ_GPU_PATH)
            setPermissions(644, AVAILABLE_GOV_GPU_PATH)
            setPermissions(644, GOV_GPU_PATH)
            setPermissions(644, ADRENO_BOOST_PATH)
            setPermissions(644, GPU_THROTTLING_PATH)

            val currentMinFreqGPU = readFile(MIN_FREQ_GPU_PATH)
            if (currentMinFreqGPU != cachedMinFreqGPU) {
                _minFreqGPU.value = currentMinFreqGPU
                cachedMinFreqGPU = currentMinFreqGPU
            }

            val currentMaxFreqGPU = readFile(MAX_FREQ_GPU_PATH)
            if (currentMaxFreqGPU != cachedMaxFreqGPU) {
                _maxFreqGPU.value = currentMaxFreqGPU
                cachedMaxFreqGPU = currentMaxFreqGPU
            }

            val currentGovGPU = readFile(GOV_GPU_PATH)
            if (currentGovGPU != cachedGovGPU) {
                _govGPU.value = currentGovGPU
                cachedGovGPU = currentGovGPU
            }

            val currentAvailableFreqGPU = readAvailableFreqGPU(AVAILABLE_FREQ_GPU_PATH)
            if (currentAvailableFreqGPU != cachedAvailableFreqGPU) {
                _availableFreqGPU.value = currentAvailableFreqGPU
                cachedAvailableFreqGPU = currentAvailableFreqGPU
            }

            val currentAvailableGovGPU = readAvailableGovGPU(AVAILABLE_GOV_GPU_PATH)
            if (currentAvailableGovGPU != cachedAvailableGovGPU) {
                _availableGovGPU.value = currentAvailableGovGPU
                cachedAvailableGovGPU = currentAvailableGovGPU
            }

            val currentAdrenoBoost = readFile(ADRENO_BOOST_PATH)
            if (currentAdrenoBoost != cachedAdrenoBoost) {
                _adrenoBoost.value = currentAdrenoBoost
                cachedAdrenoBoost = currentAdrenoBoost
            }

            val currentGpuThrottling = readFile(GPU_THROTTLING_PATH)
            if (currentGpuThrottling != cachedGpuThrottling) {
                _gpuThrottling.value = currentGpuThrottling
                cachedGpuThrottling = currentGpuThrottling
            }

            _hasAdrenoBoost.value = testFile(ADRENO_BOOST_PATH)
            _hasGPUThrottling.value = testFile(GPU_THROTTLING_PATH)
        }
    }

    fun updateFreq(target: String, selectedFreq: String, cluster: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (cluster) {
                "little" -> {
                    val path = if (target == "min") MIN_FREQ_CPU0_PATH else MAX_FREQ_CPU0_PATH
                    writeFreqCPU(path, selectedFreq)
                    _minFreqCPU0.value = readFreqCPU(MIN_FREQ_CPU0_PATH)
                    _maxFreqCPU0.value = readFreqCPU(MAX_FREQ_CPU0_PATH)
                    cachedMinFreqCPU0 = _minFreqCPU0.value
                    cachedMaxFreqCPU0 = _maxFreqCPU0.value
                }
                "big" -> {
                    val path = if (target == "min") MIN_FREQ_CPU4_PATH else MAX_FREQ_CPU4_PATH
                    writeFreqCPU(path, selectedFreq)
                    _minFreqCPU4.value = readFreqCPU(MIN_FREQ_CPU4_PATH)
                    _maxFreqCPU4.value = readFreqCPU(MAX_FREQ_CPU4_PATH)
                    cachedMinFreqCPU4 = _minFreqCPU4.value
                    cachedMaxFreqCPU4 = _maxFreqCPU4.value
                }
                "prime" -> {
                    val path = if (target == "min") MIN_FREQ_CPU7_PATH else MAX_FREQ_CPU7_PATH
                    writeFreqCPU(path, selectedFreq)
                    _minFreqCPU7.value = readFreqCPU(MIN_FREQ_CPU7_PATH)
                    _maxFreqCPU7.value = readFreqCPU(MAX_FREQ_CPU7_PATH)
                    cachedMinFreqCPU7 = _minFreqCPU7.value
                    cachedMaxFreqCPU7 = _maxFreqCPU7.value
                }
                "gpu" -> {
                    val path = if (target == "min") MIN_FREQ_GPU_PATH else MAX_FREQ_GPU_PATH
                    writeFreqGPU(path, selectedFreq)  // Menggunakan writeFreqGPU untuk GPU
                    _minFreqGPU.value = readFile(MIN_FREQ_GPU_PATH)
                    _maxFreqGPU.value = readFile(MAX_FREQ_GPU_PATH)
                    cachedMinFreqGPU = _minFreqGPU.value
                    cachedMaxFreqGPU = _maxFreqGPU.value
                }
            }
        }
    }

    fun updateGov(selectedGov: String, cluster: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val path = when (cluster) {
                "little" -> GOV_CPU0_PATH
                "big" -> GOV_CPU4_PATH
                "prime" -> GOV_CPU7_PATH
                "gpu" -> GOV_GPU_PATH
                else -> return@launch
            }
            writeFile(path, selectedGov)

            when (cluster) {
                "little" -> {
                    _govCPU0.value = readFile(GOV_CPU0_PATH)
                    cachedGovCPU0 = _govCPU0.value
                }
                "big" -> {
                    _govCPU4.value = readFile(GOV_CPU4_PATH)
                    cachedGovCPU4 = _govCPU4.value
                }
                "prime" -> {
                    _govCPU7.value = readFile(GOV_CPU7_PATH)
                    cachedGovCPU7 = _govCPU7.value
                }
                "gpu" -> {
                    _govGPU.value = readFile(GOV_GPU_PATH)
                    cachedGovGPU = _govGPU.value
                }
            }
        }
    }

    fun updateAdrenoBoost(selectedBoost: String) {
        viewModelScope.launch(Dispatchers.IO) {
            writeFile(ADRENO_BOOST_PATH, selectedBoost)
            _adrenoBoost.value = readFile(ADRENO_BOOST_PATH)
            cachedAdrenoBoost = _adrenoBoost.value
        }
    }

    fun updateGPUThrottling(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = if (isChecked) "1" else "0"
            writeFile(GPU_THROTTLING_PATH, newValue)
            _gpuThrottling.value = readFile(GPU_THROTTLING_PATH)
            cachedGpuThrottling = _gpuThrottling.value
        }
    }
}
