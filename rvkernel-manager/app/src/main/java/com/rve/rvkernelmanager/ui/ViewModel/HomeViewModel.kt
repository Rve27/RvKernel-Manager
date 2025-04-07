package com.rve.rvkernelmanager.ui.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.rve.rvkernelmanager.utils.Utils

class HomeViewModel : ViewModel() {

    private val _deviceCodename = MutableStateFlow("")
    val deviceCodename: StateFlow<String> = _deviceCodename

    private val _ramInfo = MutableStateFlow("")
    val ramInfo: StateFlow<String> = _ramInfo

    private val _cpu = MutableStateFlow("")
    val cpu: StateFlow<String> = _cpu

    private val _gpuModel = MutableStateFlow("")
    val gpuModel: StateFlow<String> = _gpuModel

    private val _androidVersion = MutableStateFlow("")
    val androidVersion: StateFlow<String> = _androidVersion

    private val _kernelVersion = MutableStateFlow("")
    val kernelVersion: StateFlow<String> = _kernelVersion

    private val _isExtendCPUInfo = MutableStateFlow(false)
    val isExtendCPUInfo: StateFlow<Boolean> = _isExtendCPUInfo

    private val _isFullKernelVersion = MutableStateFlow(false)
    val isFullKernelVersion: StateFlow<Boolean> = _isFullKernelVersion

    private var cachedCPUInfo: String? = null
    private var cachedExtendedCPUInfo: String? = null

    private var cachedKernelVersion: String? = null
    private var cachedFullKernelVersion: String? = null

    fun loadDeviceInfo(context: Context) {
        viewModelScope.launch {
            _deviceCodename.value = Utils.getDeviceCodename()
            _ramInfo.value = Utils.getTotalRam(context)
            _gpuModel.value = Utils.getGPUModel()
            _androidVersion.value = Utils.getAndroidVersion()

            if (cachedCPUInfo == null) {
                cachedCPUInfo = Utils.getCPUInfo()
            }
            _cpu.value = cachedCPUInfo ?: ""

            if (cachedKernelVersion == null) {
                cachedKernelVersion = Utils.getKernelVersion()
            }
            _kernelVersion.value = cachedKernelVersion ?: ""
        }
    }

    fun showCPUInfo() {
        _isExtendCPUInfo.value = !_isExtendCPUInfo.value

        if (_isExtendCPUInfo.value) {
            if (cachedExtendedCPUInfo == null) {
                cachedExtendedCPUInfo = Utils.getExtendCPUInfo()
            }
            _cpu.value = cachedExtendedCPUInfo ?: ""
        } else {
            _cpu.value = cachedCPUInfo ?: ""
        }
    }

    fun showFullKernelVersion() {
        _isFullKernelVersion.value = !_isFullKernelVersion.value

        if (_isFullKernelVersion.value) {
            if (cachedFullKernelVersion == null) {
                Utils.setPermissions(644, Utils.FULL_KERNEL_VERSION_PATH)
                cachedFullKernelVersion = Utils.readFile(Utils.FULL_KERNEL_VERSION_PATH)
            }
            _kernelVersion.value = cachedFullKernelVersion ?: ""
        } else {
            _kernelVersion.value = cachedKernelVersion ?: ""
        }
    }
}
