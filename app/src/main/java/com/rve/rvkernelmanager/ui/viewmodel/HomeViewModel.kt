package com.rve.rvkernelmanager.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.Dispatchers
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

    fun loadDeviceInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _deviceCodename.value = Utils.getDeviceCodename()
            _ramInfo.value = Utils.getTotalRam(context)
            _gpuModel.value = Utils.getGPUModel()
            _androidVersion.value = Utils.getAndroidVersion()
            _cpu.value = Utils.getCPUInfo()
            _kernelVersion.value = Utils.getKernelVersion()
        }
    }

    fun showCPUInfo() {
        _isExtendCPUInfo.value = !_isExtendCPUInfo.value

        if (_isExtendCPUInfo.value) {
            _cpu.value = Utils.getExtendCPUInfo()
        } else {
            _cpu.value = Utils.getCPUInfo()
        }
    }

    fun showFullKernelVersion() {
        _isFullKernelVersion.value = !_isFullKernelVersion.value

        if (_isFullKernelVersion.value) {
            Utils.setPermissions(644, Utils.FULL_KERNEL_VERSION)
            _kernelVersion.value = Utils.readFile(Utils.FULL_KERNEL_VERSION)
        } else {
            _kernelVersion.value = Utils.getKernelVersion()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
