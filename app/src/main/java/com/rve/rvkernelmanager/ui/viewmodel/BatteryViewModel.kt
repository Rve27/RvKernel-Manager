package com.rve.rvkernelmanager.ui.viewmodel

import android.util.Log
import android.content.Context
import android.content.BroadcastReceiver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import com.rve.rvkernelmanager.utils.Utils
import com.rve.rvkernelmanager.utils.BatteryUtils

class BatteryViewModel : ViewModel() {
    data class BatteryInfo(
        val tech: String = "",
        val health: String = "",
        val temp: String = "",
        val voltage: String = "",
        val designCapacity: String = "",
        val maximumCapacity: String = ""
    )

    data class ChargingState(
        val hasFastCharging: Boolean = false,
        val hasBypassCharging: Boolean = false,
        val isFastChargingChecked: Boolean = false,
        val isBypassChargingChecked: Boolean = false,
        val inputSuspendPath: String? = null
    )

    private val _batteryInfo = MutableStateFlow(BatteryInfo())
    val batteryInfo: StateFlow<BatteryInfo> = _batteryInfo

    private val _chargingState = MutableStateFlow(ChargingState())
    val chargingState: StateFlow<ChargingState> = _chargingState

    private var tempReceiver: BroadcastReceiver? = null
    private var voltageReceiver: BroadcastReceiver? = null
    private var maxCapacityReceiver: BroadcastReceiver? = null
    private var inputSuspendPath: String? = null

    fun initializeBatteryInfo(context: Context) {
        loadBatteryInfo(context)
        registerBatteryListeners(context)
        checkChargingFiles()
    }

    fun loadBatteryInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tech = BatteryUtils.getBatteryTechnology(context)
                val health = BatteryUtils.getBatteryHealth(context)
                val designCapacity = BatteryUtils.getBatteryDesignCapacity(context)

                _batteryInfo.value = _batteryInfo.value.copy(
                    tech = tech,
                    health = health,
                    designCapacity = designCapacity
                )
            } catch (e: Exception) {
                Log.e("BatteryVM", "Error loading battery info", e)
            }
        }
    }

    fun registerBatteryListeners(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
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

            val inputSuspendPath = when {
                Utils.testFile(BatteryUtils.INPUT_SUSPEND_1) -> BatteryUtils.INPUT_SUSPEND_1
                Utils.testFile(BatteryUtils.INPUT_SUSPEND_2) -> BatteryUtils.INPUT_SUSPEND_2
                else -> null
            }

            val hasBypassCharging = inputSuspendPath != null
            val isBypassChargingChecked = inputSuspendPath?.let {
                Utils.readFile(it) == "1"
            } ?: false

            val isFastChargingChecked = Utils.readFile(BatteryUtils.FAST_CHARGING) == "1"

            _chargingState.value = ChargingState(
                hasFastCharging = hasFastCharging,
                hasBypassCharging = hasBypassCharging,
                isFastChargingChecked = isFastChargingChecked,
                isBypassChargingChecked = isBypassChargingChecked,
                inputSuspendPath = inputSuspendPath
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

    fun toggleBypassCharging(checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val path = _chargingState.value.inputSuspendPath ?: return@launch
            val success = Utils.writeFile(path, if (checked) "1" else "0")

            if (success) {
                _chargingState.value = _chargingState.value.copy(
                    isBypassChargingChecked = checked
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
