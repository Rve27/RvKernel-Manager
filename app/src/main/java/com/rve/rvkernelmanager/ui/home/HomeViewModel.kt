/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package com.rve.rvkernelmanager.ui.home

import android.content.Context

import androidx.lifecycle.*

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import com.rve.rvkernelmanager.utils.*

class HomeViewModel : ViewModel() {
    data class DeviceInfo(
        val deviceName: String = "N/A",
        val deviceCodename: String = "N/A",
        val manufacturer: String = "N/A",
        val ramInfo: String = "N/A",
        val zram: String = "N/A",
        val cpu: String = "N/A",
        val extendCpu: String = "N/A",
        val gpuModel: String = "N/A",
        val androidVersion: String = "N/A",
        val sdkVersion: String = "N/A",
        val kernelVersion: String = "N/A",
        val fullKernelVersion: String = "N/A"
    )

    private val _deviceInfo = MutableStateFlow(DeviceInfo())
    val deviceInfo: StateFlow<DeviceInfo> = _deviceInfo

    fun loadDeviceInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _deviceInfo.value = DeviceInfo(
                deviceName = Utils.getDeviceName(),
                deviceCodename = Utils.getDeviceCodename(),
                manufacturer = Utils.getManufacturer(),
                ramInfo = SoCUtils.getTotalRam(context),
                zram = KernelUtils.getZramSize(),
                gpuModel = SoCUtils.getGPUModel(),
                androidVersion = Utils.getAndroidVersion(),
                sdkVersion = Utils.getSdkVersion(),
                cpu = SoCUtils.getCPUInfo(),
                extendCpu = SoCUtils.getExtendCPUInfo(),
                kernelVersion = KernelUtils.getKernelVersion(),
                fullKernelVersion = KernelUtils.getFullKernelVersion()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
