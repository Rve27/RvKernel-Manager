package com.rve.rvkernelmanager.utils

import android.util.Log
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlin.math.roundToInt
import com.topjohnwu.superuser.Shell
import com.rve.rvkernelmanager.R

object BatteryUtils {

    const val FAST_CHARGING = "/sys/kernel/fast_charge/force_fast_charge"
    const val BATTERY_DESIGN_CAPACITY = "/sys/class/power_supply/battery/charge_full_design"
    const val BATTERY_MAXIMUM_CAPACITY = "/sys/class/power_supply/battery/charge_full"

    const val THERMAL_SCONFIG = "/sys/class/thermal/thermal_message/sconfig"
    
    fun getBatteryTechnology(context: Context): String {
        val batteryIntent: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        return batteryIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "N/A"
    }
    
    fun getBatteryHealth(context: Context): String {
        val batteryIntent: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
    
        return when (val health = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "N/A"
        }
    }
    
    fun getBatteryTemperature(context: Context): String {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        return if (temp != -1) {
            "%.1f °C".format(temp / 10.0)
        } else {
            "N/A"
        }
    }

    fun getBatteryLevel(context: Context): String {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        return if (level != -1) {
            "$level%"
        } else {
            "N/A"
        }
    }

    fun registerBatteryLevelListener(
        context: Context,
        callback: (String) -> Unit
    ): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val levelStr = if (level != -1) {
                    "$level%"
                } else {
                    "N/A"
                }
                callback(levelStr)
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return receiver
    }

    fun registerBatteryTemperatureListener(
        context: Context,
        callback: (String) -> Unit
    ): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val temp = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
                val tempStr = if (temp != -1) {
                    "%.1f °C".format(temp / 10.0)
                } else {
                    "N/A"
                }
                callback(tempStr)
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return receiver
    }
    
    fun getBatteryVoltage(context: Context): String {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val voltage = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
        return if (voltage != -1) {
            "%.3f V".format(voltage / 1000.0)
        } else {
            "N/A"
        }
    }
    
    fun registerBatteryVoltageListener(
        context: Context,
        callback: (String) -> Unit
    ): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
                val voltageStr = if (voltage != -1) {
                    "%.3f V".format(voltage / 1000.0)
                } else {
                    "N/A"
                }
                callback(voltageStr)
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return receiver
    }
    
    fun getBatteryDesignCapacity(context: Context): String {
        return try {
            val result = Shell.cmd("cat $BATTERY_DESIGN_CAPACITY").exec()

            if (result.isSuccess && result.out.isNotEmpty()) {
                val mAh = result.out.firstOrNull()?.trim()
                "${(mAh?.toIntOrNull() ?: 0) / 1000} mAh"
            } else {
                Log.w("BatteryUtils", "Failed to read design capacity: ${result.err}")
                "N/A"
            }
        } catch (e: Exception) {
            Log.e("BatteryUtils", "Error reading design capacity", e)
            "N/A (Error: ${e.message})"
        }
    }

    fun getBatteryMaximumCapacity(context: Context): String {
        return try {
            val result = Shell.cmd(
                "cat $BATTERY_MAXIMUM_CAPACITY $BATTERY_DESIGN_CAPACITY"
            ).exec()

            if (result.isSuccess && result.out.size >= 2) {
                val maxCapacity = result.out[0].trim().toIntOrNull() ?: 0
                val designCapacity = result.out[1].trim().toIntOrNull() ?: 0

                val percentage = if (designCapacity > 0) {
                    ((maxCapacity.toDouble() / designCapacity) * 100).roundToInt()
                } else 0

                "${maxCapacity / 1000} mAh ($percentage%)"
            } else {
                "N/A"
            }
        } catch (e: Exception) {
            Log.e("BatteryUtils", "Error reading maximum capacity", e)
            "N/A"
        }
    }

    fun registerBatteryCapacityListener(
        context: Context,
        callback: (String) -> Unit
    ): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val capacity = getBatteryMaximumCapacity(context ?: return)
                callback(capacity)
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return receiver
    }
}
