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
    private val _deviceName = MutableStateFlow("N/A")
    val deviceName: StateFlow<String> = _deviceName

    private val _deviceCodename = MutableStateFlow("N/A")
    val deviceCodename: StateFlow<String> = _deviceCodename

    private val _ramInfo = MutableStateFlow("N/A")
    val ramInfo: StateFlow<String> = _ramInfo

    private val _zram = MutableStateFlow("N/A")
    val zram: StateFlow<String> = _zram

    private val _cpu = MutableStateFlow("N/A")
    val cpu: StateFlow<String> = _cpu

    private val _extendCpu = MutableStateFlow("N/A")
    val extendCpu: StateFlow<String> = _extendCpu

    private val _gpuModel = MutableStateFlow("N/A")
    val gpuModel: StateFlow<String> = _gpuModel

    private val _androidVersion = MutableStateFlow("N/A")
    val androidVersion: StateFlow<String> = _androidVersion

    private val _sdkVersion = MutableStateFlow("N/A")
    val sdkVersion: StateFlow<String> = _sdkVersion

    private val _kernelVersion = MutableStateFlow("N/A")
    val kernelVersion: StateFlow<String> = _kernelVersion

    private val _fullKernelVersion = MutableStateFlow("N/A")
    val fullKernelVersion: StateFlow<String> = _fullKernelVersion

    fun loadDeviceInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
	    _deviceName.value = Utils.getDeviceName()
            _deviceCodename.value = Utils.getDeviceCodename()
            _ramInfo.value = Utils.getTotalRam(context)
	    _zram.value = Utils.getZramSize()
            _gpuModel.value = Utils.getGPUModel()
            _androidVersion.value = Utils.getAndroidVersion()
	    _sdkVersion.value = Utils.getSdkVersion()
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
