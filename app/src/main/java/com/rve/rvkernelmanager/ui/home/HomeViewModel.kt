/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rve.rvkernelmanager.utils.KernelUtils
import com.rve.rvkernelmanager.utils.SoCUtils
import com.rve.rvkernelmanager.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    data class DeviceInfo(
        val deviceName: String = "N/A",
        val deviceCodename: String = "N/A",
        val manufacturer: String = "N/A",
        val ramInfo: String = "N/A",
        val zram: String = "N/A",
        val cpu: String = "N/A",
        val gpuModel: String = "N/A",
        val androidVersion: String = "N/A",
        val sdkVersion: String = "N/A",
        val hasWireGuard: Boolean = false,
        val wireGuard: String = "N/A",
        val kernelVersion: String = "N/A",
        val fullKernelVersion: String = "N/A",
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
                cpu = SoCUtils.getCpuInfo(),
                hasWireGuard = Utils.testFile(KernelUtils.WireGuard),
                wireGuard = KernelUtils.getWireGuardVersion(),
                kernelVersion = KernelUtils.getKernelVersion(),
                fullKernelVersion = KernelUtils.getFullKernelVersion(),
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
