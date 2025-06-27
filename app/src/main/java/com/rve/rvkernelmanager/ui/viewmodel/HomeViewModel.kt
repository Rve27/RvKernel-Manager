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

    private val _device = MutableStateFlow("")
    val device: StateFlow<String> = _device

    private val _ram = MutableStateFlow("")
    val ram: StateFlow<String> = _ram

    private val _cpu = MutableStateFlow("")
    val cpu: StateFlow<String> = _cpu

    private val _gpu = MutableStateFlow("")
    val gpu: StateFlow<String> = _gpu

    private val _android = MutableStateFlow("")
    val android: StateFlow<String> = _android

    private val _kernel = MutableStateFlow("")
    val kernel: StateFlow<String> = _kernel

    fun loadDeviceInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _device.value = Utils.getDeviceCodename()
            _ram.value = Utils.getTotalRam(context)
            _gpu.value = Utils.getGPUModel()
            _android.value = Utils.getAndroidVersion()
            _cpu.value = Utils.getCPUInfo()
            _kernel.value = Utils.getKernelVersion()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
