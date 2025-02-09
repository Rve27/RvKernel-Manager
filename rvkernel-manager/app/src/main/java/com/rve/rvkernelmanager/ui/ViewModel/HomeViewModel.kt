package com.rve.rvkernelmanager.ui.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.rve.rvkernelmanager.utils.*
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

    private val _rvosVersion = MutableStateFlow<String?>(null)
    val rvosVersion: StateFlow<String?> = _rvosVersion

    private val _somethingVersion = MutableStateFlow<String?>(null)
    val somethingVersion: StateFlow<String?> = _somethingVersion

    private val _kernelVersion = MutableStateFlow("")
    val kernelVersion: StateFlow<String> = _kernelVersion

    private val _isCPUInfo = MutableStateFlow(false)
    val isCPUInfo: StateFlow<Boolean> = _isCPUInfo

    private val _isFullKernelVersion = MutableStateFlow(false)
    val isFullKernelVersion: StateFlow<Boolean> = _isFullKernelVersion

    fun loadDeviceInfo(context: Context) {
        viewModelScope.launch {
            _deviceCodename.value = Utils.getDeviceCodename()
            _ramInfo.value = Utils.getTotalRam(context)
            _cpu.value = Utils.getCPU()
            _gpuModel.value = Utils.getGPUModel()
            _androidVersion.value = Utils.getAndroidVersion()
            _rvosVersion.value = Utils.getRvOSVersion()
            _somethingVersion.value = Utils.getSomethingOSVersion()
            _kernelVersion.value = Utils.getKernelVersion()
        }
    }

    fun toggleCPUInfo() {
        _isCPUInfo.value = !_isCPUInfo.value
        _cpu.value = if (_isCPUInfo.value) {
            Utils.getCPUInfo()
        } else {
            Utils.getCPU()
        }
    }

    fun toggleFullKernelVersion() {
        _isFullKernelVersion.value = !_isFullKernelVersion.value
        _kernelVersion.value = if (_isFullKernelVersion.value) {
            setPermissions(644, Utils.FULL_KERNEL_VERSION_PATH)
            readFile(Utils.FULL_KERNEL_VERSION_PATH)
        } else {
            Utils.getKernelVersion()
        }
    }
}
