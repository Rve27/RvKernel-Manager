/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package com.rve.rvkernelmanager.ui.battery

import android.util.Log
import android.content.*

import androidx.lifecycle.*

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import com.rve.rvkernelmanager.utils.*

class BatteryViewModel : ViewModel() {
    data class BatteryInfo(
	val level: String = "N/A",
        val tech: String = "N/A",
        val health: String = "N/A",
        val temp: String = "N/A",
        val voltage: String = "N/A",
	val deepSleep: String = "N/A",
        val designCapacity: String = "N/A",
        val maximumCapacity: String = "N/A"
    )

    data class ChargingState(
        val hasFastCharging: Boolean = false,
        val isFastChargingChecked: Boolean = false,
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
		val level = BatteryUtils.getBatteryLevel(context)
                val tech = BatteryUtils.getBatteryTechnology(context)
                val health = BatteryUtils.getBatteryHealth(context)
                val designCapacity = BatteryUtils.getBatteryDesignCapacity()
		val deepSleep = BatteryUtils.getDeepSleep()

                _batteryInfo.value = _batteryInfo.value.copy(
		    level = level,
                    tech = tech,
                    health = health,
                    designCapacity = designCapacity,
		    deepSleep = deepSleep
                )
            } catch (e: Exception) {
                Log.e("BatteryVM", "Error loading battery info", e)
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
        job = viewModelScope.launch {
            while (isActive) {
                _uptime.value = BatteryUtils.getUptime()
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

            _chargingState.value = ChargingState(
                hasFastCharging = hasFastCharging,
                isFastChargingChecked = isFastChargingChecked
            )
        }
    }

    fun toggleFastCharging(checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = Utils.writeFile(BatteryUtils.FAST_CHARGING, if (checked) "1" else "0")

            if (success) {
                _chargingState.value = _chargingState.value.copy(
                    isFastChargingChecked = checked
                )
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
