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

// Dear programmer:
// When I wrote this code, only god and
// I knew how it worked.
// Now, only god knows it!
//
// Therefore, if you are trying to optimize
// this routine and it fails (most surely),
// please increase this counter as a
// warning for the next person:
//
// total hours wasted here = 254
//
package com.rve.rvkernelmanager.ui.battery

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rve.rvkernelmanager.utils.BatteryUtils
import com.rve.rvkernelmanager.utils.KernelUtils
import com.rve.rvkernelmanager.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BatteryViewModel(application: Application) : AndroidViewModel(application) {
    data class BatteryInfo(
        val level: String = "N/A",
        val tech: String = "N/A",
        val health: String = "N/A",
        val temp: String = "N/A",
        val voltage: String = "N/A",
        val deepSleep: String = "N/A",
        val designCapacity: String = "N/A",
        val manualDesignCapacity: Int = 0,
        val maximumCapacity: String = "N/A",
    )

    data class ChargingState(
        val hasFastCharging: Boolean = false,
        val isFastChargingChecked: Boolean = false,
        val kernelVersion: String = "N/A",
        val isBypassChargingChecked: Boolean = false,
    )

    private val _batteryInfo = MutableStateFlow(BatteryInfo())
    val batteryInfo: StateFlow<BatteryInfo> = _batteryInfo

    private val _chargingState = MutableStateFlow(ChargingState())
    val chargingState: StateFlow<ChargingState> = _chargingState

    private val _thermalSconfig = MutableStateFlow("")
    val thermalSconfig: StateFlow<String> = _thermalSconfig

    private val _hasThermalSconfig = MutableStateFlow(false)
    val hasThermalSconfig: StateFlow<Boolean> = _hasThermalSconfig

    private val _uptime = MutableStateFlow("N/A")
    val uptime: StateFlow<String> = _uptime

    private var levelReceiver: BroadcastReceiver? = null
    private var tempReceiver: BroadcastReceiver? = null
    private var voltageReceiver: BroadcastReceiver? = null
    private var maxCapacityReceiver: BroadcastReceiver? = null

    private var job: Job? = null

    fun initializeBatteryInfo(context: Context) {
        loadBatteryInfo(context)
        loadThermalSconfig()
        registerBatteryListeners(context)
        checkChargingFiles()
    }

    fun loadBatteryInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val batteryPreference = BatteryPreference.getInstance(context)

                val level = BatteryUtils.getBatteryLevel(context)
                val tech = BatteryUtils.getBatteryTechnology(context)
                val health = BatteryUtils.getBatteryHealth(context)
                val manualDesignCapacity = batteryPreference.getManualDesignCapacity()
                val designCapacity = if (manualDesignCapacity != 0) "$manualDesignCapacity mAh" else BatteryUtils.getBatteryDesignCapacity(context)
                val deepSleep = BatteryUtils.getDeepSleep(context)

                _batteryInfo.value = _batteryInfo.value.copy(
                    level = level,
                    tech = tech,
                    health = health,
                    designCapacity = designCapacity,
                    manualDesignCapacity = manualDesignCapacity,
                    deepSleep = deepSleep,
                )
            } catch (e: Exception) {
                Log.e("BatteryVM", "Error loading battery info", e)
            }
        }
    }

    fun setManualDesignCapacity(context: Context, value: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val batteryPreference = BatteryPreference.getInstance(context)
            batteryPreference.setManualDesignCapacity(value)

            _batteryInfo.update { currentState ->
                currentState.copy(manualDesignCapacity = value)
            }
        }
    }

    fun loadThermalSconfig() {
        viewModelScope.launch(Dispatchers.IO) {
            _thermalSconfig.value = Utils.readFile(BatteryUtils.THERMAL_SCONFIG)
            _hasThermalSconfig.value = Utils.testFile(BatteryUtils.THERMAL_SCONFIG)
        }
    }

    fun startJob() {
        job?.cancel()
        val context = getApplication<Application>()
        job = viewModelScope.launch {
            while (isActive) {
                _uptime.value = BatteryUtils.getUptime(context)
                delay(1000L)
            }
        }
    }

    fun stopJob() {
        job?.cancel()
        job = null
    }

    fun registerBatteryListeners(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            levelReceiver = BatteryUtils.registerBatteryLevelListener(context) { level ->
                _batteryInfo.value = _batteryInfo.value.copy(level = level)
            }
            tempReceiver = BatteryUtils.registerBatteryTemperatureListener(context) { temp ->
                _batteryInfo.value = _batteryInfo.value.copy(temp = temp)
            }
            voltageReceiver = BatteryUtils.registerBatteryVoltageListener(context) { voltage ->
                _batteryInfo.value = _batteryInfo.value.copy(voltage = voltage)
            }
            maxCapacityReceiver = BatteryUtils.registerBatteryCapacityListener(context) { maxCapacity ->
                _batteryInfo.value = _batteryInfo.value.copy(maximumCapacity = maxCapacity)
            }
        }
    }

    fun unregisterBatteryListeners(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                levelReceiver?.let {
                    context.unregisterReceiver(it)
                    levelReceiver = null
                }
                tempReceiver?.let {
                    context.unregisterReceiver(it)
                    tempReceiver = null
                }
                voltageReceiver?.let {
                    context.unregisterReceiver(it)
                    voltageReceiver = null
                }
                maxCapacityReceiver?.let {
                    context.unregisterReceiver(it)
                    maxCapacityReceiver = null
                }
            } catch (e: Exception) {
                Log.e("BatteryVM", "Error unregistering receivers", e)
            }
        }
    }

    fun checkChargingFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val hasFastCharging = Utils.testFile(BatteryUtils.FAST_CHARGING)
            val isFastChargingChecked = Utils.readFile(BatteryUtils.FAST_CHARGING) == "1"
            val kernelVersion = KernelUtils.getKernelVersion(getApplication())
            val isBypassChargingChecked = Utils.readFile(BatteryUtils.BYPASS_CHARGING) == "1"

            _chargingState.value = ChargingState(
                hasFastCharging = hasFastCharging,
                isFastChargingChecked = isFastChargingChecked,
                kernelVersion = kernelVersion,
                isBypassChargingChecked = isBypassChargingChecked,
            )
        }
    }

    fun updateCharging(filePath: String, checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = Utils.writeFile(filePath, if (checked) "1" else "0")

            if (success) {
                when (filePath) {
                    BatteryUtils.FAST_CHARGING -> {
                        _chargingState.value = _chargingState.value.copy(
                            isFastChargingChecked = checked,
                        )
                    }

                    BatteryUtils.BYPASS_CHARGING -> {
                        _chargingState.value = _chargingState.value.copy(
                            isBypassChargingChecked = checked,
                        )
                    }
                }
            }
        }
    }

    fun updateThermalSconfig(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.setPermissions(644, BatteryUtils.THERMAL_SCONFIG)
            Utils.writeFile(BatteryUtils.THERMAL_SCONFIG, value)
            Utils.setPermissions(444, BatteryUtils.THERMAL_SCONFIG)
            _thermalSconfig.value = value
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        stopJob()
    }
}
