package com.rve.rvkernelmanager.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.rve.rvkernelmanager.utils.Utils
import com.rve.rvkernelmanager.utils.SoCUtils

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

    private val _minFreqBigCluster = MutableStateFlow("")
    val minFreqBigCluster: StateFlow<String> = _minFreqBigCluster

    private val _maxFreqBigCluster = MutableStateFlow("")
    val maxFreqBigCluster: StateFlow<String> = _maxFreqBigCluster

    private val _govBigCluster = MutableStateFlow("")
    val govBigCluster: StateFlow<String> = _govBigCluster

    private val _availableFreqBigCluster = MutableStateFlow(listOf<String>())
    val availableFreqBigCluster: StateFlow<List<String>> = _availableFreqBigCluster

    private val _availableGovBigCluster = MutableStateFlow(listOf<String>())
    val availableGovBigCluster: StateFlow<List<String>> = _availableGovBigCluster

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

    private var cachedMinFreqBigCluster: String? = null
    private var cachedMaxFreqBigCluster: String? = null
    private var cachedGovBigCluster: String? = null
    private var cachedAvailableFreqBigCluster: List<String>? = null
    private var cachedAvailableGovBigCluster: List<String>? = null

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
            val currentMinFreqCPU0 = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU0_PATH)
            if (currentMinFreqCPU0 != cachedMinFreqCPU0) {
                _minFreqCPU0.value = currentMinFreqCPU0
                cachedMinFreqCPU0 = currentMinFreqCPU0
            }

            val currentMaxFreqCPU0 = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU0_PATH)
            if (currentMaxFreqCPU0 != cachedMaxFreqCPU0) {
                _maxFreqCPU0.value = currentMaxFreqCPU0
                cachedMaxFreqCPU0 = currentMaxFreqCPU0
            }

            val currentGovCPU0 = Utils.readFile(SoCUtils.GOV_CPU0_PATH)
            if (currentGovCPU0 != cachedGovCPU0) {
                _govCPU0.value = currentGovCPU0
                cachedGovCPU0 = currentGovCPU0
            }

            val currentAvailableFreqCPU0 = SoCUtils.readAvailableFreqCPU(SoCUtils.AVAILABLE_FREQ_CPU0_PATH)
            if (currentAvailableFreqCPU0 != cachedAvailableFreqCPU0) {
                _availableFreqCPU0.value = currentAvailableFreqCPU0
                cachedAvailableFreqCPU0 = currentAvailableFreqCPU0
            }

            val currentAvailableGovCPU0 = SoCUtils.readAvailableGovCPU(SoCUtils.AVAILABLE_GOV_CPU0_PATH)
            if (currentAvailableGovCPU0 != cachedAvailableGovCPU0) {
                _availableGovCPU0.value = currentAvailableGovCPU0
                cachedAvailableGovCPU0 = currentAvailableGovCPU0
            }

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
                    val (availableFreqPath, availableBoostPath) = when (basePath) {
                        "cpu4" -> SoCUtils.AVAILABLE_FREQ_CPU4_PATH to SoCUtils.AVAILABLE_BOOST_CPU4_PATH
                        "cpu6" -> SoCUtils.AVAILABLE_FREQ_CPU6_PATH to SoCUtils.AVAILABLE_BOOST_CPU6_PATH
                        else -> null to null
                    }
                    val availableGovPath = when (basePath) {
                        "cpu4" -> SoCUtils.AVAILABLE_GOV_CPU4_PATH
                        "cpu6" -> SoCUtils.AVAILABLE_GOV_CPU6_PATH
                        else -> null
                    }
    
                    if (minFreqPath != null) Utils.setPermissions(644, minFreqPath)
                    if (maxFreqPath != null) Utils.setPermissions(644, maxFreqPath)
                    if (govPath != null) Utils.setPermissions(644, govPath)
                    if (availableGovPath != null) Utils.setPermissions(644, availableGovPath)
    
                    val currentMinFreq = SoCUtils.readFreqCPU(minFreqPath!!)
                    val currentMaxFreq = SoCUtils.readFreqCPU(maxFreqPath!!)
                    val currentGov = Utils.readFile(govPath!!)
                    val currentAvailableFreq = if (availableFreqPath != null && availableBoostPath != null) {
                        SoCUtils.readAvailableFreqBoost(availableFreqPath, availableBoostPath)
                    } else {
                        emptyList()
                    }
                    val currentAvailableGov = SoCUtils.readAvailableGovCPU(availableGovPath!!)
    
                    if (currentMinFreq != cachedMinFreqBigCluster) {
                        _minFreqBigCluster.value = currentMinFreq
                        cachedMinFreqBigCluster = currentMinFreq
                    }
                    if (currentMaxFreq != cachedMaxFreqBigCluster) {
                        _maxFreqBigCluster.value = currentMaxFreq
                        cachedMaxFreqBigCluster = currentMaxFreq
                    }
                    if (currentGov != cachedGovBigCluster) {
                        _govBigCluster.value = currentGov
                        cachedGovBigCluster = currentGov
                    }
                    if (currentAvailableFreq != cachedAvailableFreqBigCluster) {
                        _availableFreqBigCluster.value = currentAvailableFreq
                        cachedAvailableFreqBigCluster = currentAvailableFreq
                    }
                    if (currentAvailableGov != cachedAvailableGovBigCluster) {
                        _availableGovBigCluster.value = currentAvailableGov
                        cachedAvailableGovBigCluster = currentAvailableGov
                    }
                }
	    }

            _hasPrimeCluster.value = Utils.testFile(SoCUtils.AVAILABLE_FREQ_CPU7_PATH)
            if (_hasPrimeCluster.value) {
                Utils.setPermissions(644, SoCUtils.AVAILABLE_FREQ_CPU7_PATH)
                Utils.setPermissions(644, SoCUtils.MIN_FREQ_CPU7_PATH)
                Utils.setPermissions(644, SoCUtils.MAX_FREQ_CPU7_PATH)
                Utils.setPermissions(644, SoCUtils.AVAILABLE_GOV_CPU7_PATH)
                Utils.setPermissions(644, SoCUtils.GOV_CPU7_PATH)

                val currentMinFreqCPU7 = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU7_PATH)
                if (currentMinFreqCPU7 != cachedMinFreqCPU7) {
                    _minFreqCPU7.value = currentMinFreqCPU7
                    cachedMinFreqCPU7 = currentMinFreqCPU7
                }

                val currentMaxFreqCPU7 = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU7_PATH)
                if (currentMaxFreqCPU7 != cachedMaxFreqCPU7) {
                    _maxFreqCPU7.value = currentMaxFreqCPU7
                    cachedMaxFreqCPU7 = currentMaxFreqCPU7
                }

                val currentGovCPU7 = Utils.readFile(SoCUtils.GOV_CPU7_PATH)
                if (currentGovCPU7 != cachedGovCPU7) {
                    _govCPU7.value = currentGovCPU7
                    cachedGovCPU7 = currentGovCPU7
                }

                val currentAvailableFreqCPU7 = SoCUtils.readAvailableFreqBoost(SoCUtils.AVAILABLE_FREQ_CPU7_PATH, SoCUtils.AVAILABLE_BOOST_CPU7_PATH)
                if (currentAvailableFreqCPU7 != cachedAvailableFreqCPU7) {
                    _availableFreqCPU7.value = currentAvailableFreqCPU7
                    cachedAvailableFreqCPU7 = currentAvailableFreqCPU7
                }

                val currentAvailableGovCPU7 = SoCUtils.readAvailableGovCPU(SoCUtils.AVAILABLE_GOV_CPU7_PATH)
                if (currentAvailableGovCPU7 != cachedAvailableGovCPU7) {
                    _availableGovCPU7.value = currentAvailableGovCPU7
                    cachedAvailableGovCPU7 = currentAvailableGovCPU7
                }
            }

            Utils.setPermissions(644, SoCUtils.AVAILABLE_FREQ_GPU_PATH)
            Utils.setPermissions(644, SoCUtils.MIN_FREQ_GPU_PATH)
            Utils.setPermissions(644, SoCUtils.MAX_FREQ_GPU_PATH)
            Utils.setPermissions(644, SoCUtils.AVAILABLE_GOV_GPU_PATH)
            Utils.setPermissions(644, SoCUtils.GOV_GPU_PATH)
            Utils.setPermissions(644, SoCUtils.ADRENO_BOOST_PATH)
            Utils.setPermissions(644, SoCUtils.GPU_THROTTLING_PATH)

            val currentMinFreqGPU = Utils.readFile(SoCUtils.MIN_FREQ_GPU_PATH)
            if (currentMinFreqGPU != cachedMinFreqGPU) {
                _minFreqGPU.value = currentMinFreqGPU
                cachedMinFreqGPU = currentMinFreqGPU
            }

            val currentMaxFreqGPU = Utils.readFile(SoCUtils.MAX_FREQ_GPU_PATH)
            if (currentMaxFreqGPU != cachedMaxFreqGPU) {
                _maxFreqGPU.value = currentMaxFreqGPU
                cachedMaxFreqGPU = currentMaxFreqGPU
            }

            val currentGovGPU = Utils.readFile(SoCUtils.GOV_GPU_PATH)
            if (currentGovGPU != cachedGovGPU) {
                _govGPU.value = currentGovGPU
                cachedGovGPU = currentGovGPU
            }

            val currentAvailableFreqGPU = SoCUtils.readAvailableFreqGPU(SoCUtils.AVAILABLE_FREQ_GPU_PATH)
            if (currentAvailableFreqGPU != cachedAvailableFreqGPU) {
                _availableFreqGPU.value = currentAvailableFreqGPU
                cachedAvailableFreqGPU = currentAvailableFreqGPU
            }

            val currentAvailableGovGPU = SoCUtils.readAvailableGovGPU(SoCUtils.AVAILABLE_GOV_GPU_PATH)
            if (currentAvailableGovGPU != cachedAvailableGovGPU) {
                _availableGovGPU.value = currentAvailableGovGPU
                cachedAvailableGovGPU = currentAvailableGovGPU
            }

            val currentAdrenoBoost = Utils.readFile(SoCUtils.ADRENO_BOOST_PATH)
            if (currentAdrenoBoost != cachedAdrenoBoost) {
                _adrenoBoost.value = currentAdrenoBoost
                cachedAdrenoBoost = currentAdrenoBoost
            }

            val currentGpuThrottling = Utils.readFile(SoCUtils.GPU_THROTTLING_PATH)
            if (currentGpuThrottling != cachedGpuThrottling) {
                _gpuThrottling.value = currentGpuThrottling
                cachedGpuThrottling = currentGpuThrottling
            }

            _hasAdrenoBoost.value = Utils.testFile(SoCUtils.ADRENO_BOOST_PATH)
            _hasGPUThrottling.value = Utils.testFile(SoCUtils.GPU_THROTTLING_PATH)
        }
    }

    fun updateFreq(target: String, selectedFreq: String, cluster: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (cluster) {
                "little" -> {
                    val path = if (target == "min") SoCUtils.MIN_FREQ_CPU0_PATH else SoCUtils.MAX_FREQ_CPU0_PATH
                    SoCUtils.writeFreqCPU(path, selectedFreq)
                    _minFreqCPU0.value = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU0_PATH)
                    _maxFreqCPU0.value = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU0_PATH)
                    cachedMinFreqCPU0 = _minFreqCPU0.value
                    cachedMaxFreqCPU0 = _maxFreqCPU0.value
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
    
                        _minFreqBigCluster.value = SoCUtils.readFreqCPU(minPath)
                        _maxFreqBigCluster.value = SoCUtils.readFreqCPU(maxPath)
                        cachedMinFreqBigCluster = _minFreqBigCluster.value
                        cachedMaxFreqBigCluster = _maxFreqBigCluster.value
                    }
                }
                "prime" -> {
                    val path = if (target == "min") SoCUtils.MIN_FREQ_CPU7_PATH else SoCUtils.MAX_FREQ_CPU7_PATH
                    SoCUtils.writeFreqCPU(path, selectedFreq)
                    _minFreqCPU7.value = SoCUtils.readFreqCPU(SoCUtils.MIN_FREQ_CPU7_PATH)
                    _maxFreqCPU7.value = SoCUtils.readFreqCPU(SoCUtils.MAX_FREQ_CPU7_PATH)
                    cachedMinFreqCPU7 = _minFreqCPU7.value
                    cachedMaxFreqCPU7 = _maxFreqCPU7.value
                }
                "gpu" -> {
                    val path = if (target == "min") SoCUtils.MIN_FREQ_GPU_PATH else SoCUtils.MAX_FREQ_GPU_PATH
                    SoCUtils.writeFreqGPU(path, selectedFreq)
                    _minFreqGPU.value = Utils.readFile(SoCUtils.MIN_FREQ_GPU_PATH)
                    _maxFreqGPU.value = Utils.readFile(SoCUtils.MAX_FREQ_GPU_PATH)
                    cachedMinFreqGPU = _minFreqGPU.value
                    cachedMaxFreqGPU = _maxFreqGPU.value
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
                        _govCPU0.value = Utils.readFile(SoCUtils.GOV_CPU0_PATH)
                        cachedGovCPU0 = _govCPU0.value
                    }
                    "big" -> {
                        _govBigCluster.value = Utils.readFile(path)
                        cachedGovBigCluster = _govBigCluster.value
                    }
                    "prime" -> {
                        _govCPU7.value = Utils.readFile(SoCUtils.GOV_CPU7_PATH)
                        cachedGovCPU7 = _govCPU7.value
                    }
                    "gpu" -> {
                        _govGPU.value = Utils.readFile(SoCUtils.GOV_GPU_PATH)
                        cachedGovGPU = _govGPU.value
                    }
                }
            }
        }
    }

    fun updateAdrenoBoost(selectedBoost: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.writeFile(SoCUtils.ADRENO_BOOST_PATH, selectedBoost)
            _adrenoBoost.value = Utils.readFile(SoCUtils.ADRENO_BOOST_PATH)
            cachedAdrenoBoost = _adrenoBoost.value
        }
    }

    fun updateGPUThrottling(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newValue = if (isChecked) "1" else "0"
            Utils.writeFile(SoCUtils.GPU_THROTTLING_PATH, newValue)
            _gpuThrottling.value = Utils.readFile(SoCUtils.GPU_THROTTLING_PATH)
            cachedGpuThrottling = _gpuThrottling.value
        }
    }
}
