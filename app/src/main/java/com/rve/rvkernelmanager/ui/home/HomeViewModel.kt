/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
        val sdkVersion: Int = 0,
        val hasWireGuard: Boolean = false,
        val wireGuard: String = "N/A",
        val kernelVersion: String = "N/A",
        val fullKernelVersion: String = "N/A",
    )

    private val _deviceInfo = MutableStateFlow(DeviceInfo())
    val deviceInfo: StateFlow<DeviceInfo> = _deviceInfo

    private val _appVersion = MutableStateFlow("Unknown")
    val appVersion: StateFlow<String> = _appVersion

    fun loadDeviceInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _deviceInfo.value = DeviceInfo(
                deviceName = Utils.getDeviceName(context),
                deviceCodename = Utils.getDeviceCodename(context),
                manufacturer = Utils.getManufacturer(context),
                ramInfo = SoCUtils.getTotalRam(context),
                zram = KernelUtils.getZramSize(context),
                gpuModel = SoCUtils.getOpenGL(context),
                androidVersion = Utils.getAndroidVersion(context),
                sdkVersion = Utils.getSdkVersion(),
                cpu = SoCUtils.getCpuInfo(context),
                hasWireGuard = Utils.testFile(KernelUtils.WIREGUARD_VERSION),
                wireGuard = KernelUtils.getWireGuardVersion(context),
                kernelVersion = KernelUtils.getKernelVersion(context),
                fullKernelVersion = KernelUtils.getFullKernelVersion(context),
            )
        }
    }

    fun loadAppVersion(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _appVersion.value = Utils.getAppVersion(context)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
