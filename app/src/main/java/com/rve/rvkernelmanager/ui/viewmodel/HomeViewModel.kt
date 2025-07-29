/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package com.rve.rvkernelmanager.ui.viewmodel

import android.content.Context

import androidx.lifecycle.*

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import com.rve.rvkernelmanager.utils.*

class HomeViewModel : ViewModel() {
    private val _deviceName = MutableStateFlow("N/A")
    val deviceName: StateFlow<String> = _deviceName

    private val _deviceCodename = MutableStateFlow("N/A")
    val deviceCodename: StateFlow<String> = _deviceCodename

    private val _manufacturer = MutableStateFlow("N/A")
    val manufacturer: StateFlow<String> = _manufacturer

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
	    _manufacturer.value = Utils.getManufacturer()
            _ramInfo.value = SoCUtils.getTotalRam(context)
	    _zram.value = KernelUtils.getZramSize()
            _gpuModel.value = SoCUtils.getGPUModel()
            _androidVersion.value = Utils.getAndroidVersion()
	    _sdkVersion.value = Utils.getSdkVersion()
            _cpu.value = SoCUtils.getCPUInfo()
	    _extendCpu.value = SoCUtils.getExtendCPUInfo()
            _kernelVersion.value = KernelUtils.getKernelVersion()
	    _fullKernelVersion.value = KernelUtils.getFullKernelVersion()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
