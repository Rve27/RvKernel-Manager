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

    private val _extendCpu = MutableStateFlow("")
    val extendCpu: StateFlow<String> = _extendCpu

    private val _gpuModel = MutableStateFlow("")
    val gpuModel: StateFlow<String> = _gpuModel

    private val _androidVersion = MutableStateFlow("")
    val androidVersion: StateFlow<String> = _androidVersion

    private val _kernelVersion = MutableStateFlow("")
    val kernelVersion: StateFlow<String> = _kernelVersion

    private val _fullKernelVersion = MutableStateFlow("")
    val fullKernelVersion: StateFlow<String> = _fullKernelVersion

    private val _isExtendCPUInfo = MutableStateFlow(false)
    val isExtendCPUInfo: StateFlow<Boolean> = _isExtendCPUInfo

    fun loadDeviceInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _deviceCodename.value = Utils.getDeviceCodename()
            _ramInfo.value = Utils.getTotalRam(context)
            _gpuModel.value = Utils.getGPUModel()
            _androidVersion.value = Utils.getAndroidVersion()
            _cpu.value = Utils.getCPUInfo()
	    _extendCpu.value = Utils.getExtendCPUInfo()
            _kernelVersion.value = Utils.getKernelVersion()
	    _fullKernelVersion.value = Utils.getFullKernelVersion()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
