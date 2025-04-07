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

    const val FAST_CHARGING_PATH = "/sys/kernel/fast_charge/force_fast_charge"
    const val BATTERY_DESIGN_CAPACITY_PATH = "/sys/class/power_supply/battery/charge_full_design"
    const val BATTERY_MAXIMUM_CAPACITY_PATH = "/sys/class/power_supply/battery/charge_full"
    const val INPUT_SUSPEND = "/sys/class/power_supply/battery/input_suspend"
    
    fun getBatteryTechnology(context: Context): String {
        val batteryIntent: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        return batteryIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: context.getString(R.string.not_available)
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
            else -> context.getString(R.string.not_available)
        }
    }
    
    fun getBatteryTemperature(context: Context): String {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        return if (temp != -1) {
            "%.1f °C".format(temp / 10.0)
        } else {
            context.getString(R.string.not_available)
        }
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
                    context?.getString(R.string.not_available) ?: "N/A"
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
            context.getString(R.string.not_available)
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
                    context?.getString(R.string.not_available) ?: "N/A"
                }
                callback(voltageStr)
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return receiver
    }
    
    fun getBatteryDesignCapacity(context: Context): String {
        return try {
            val result = Shell.cmd("cat $BATTERY_DESIGN_CAPACITY_PATH").exec()
    
            if (result.isSuccess) {
                val mAh = result.out.firstOrNull()?.trim()
                "${(mAh?.toIntOrNull() ?: 0) / 1000} mAh"
            } else {
                context.getString(R.string.not_available)
            }
        } catch (e: Exception) {
            Log.e("BatteryUtils", "Error reading design capacity", e)
            context.getString(R.string.not_available)
        }
    }
    
    fun getBatteryMaximumCapacity(context: Context): String {
        return try {
            val maxCapacityResult = Shell.cmd("cat $BATTERY_MAXIMUM_CAPACITY_PATH").exec()
            val maxCapacity = maxCapacityResult.out.firstOrNull()?.trim()?.toIntOrNull() ?: 0
    
            val designCapacityResult = Shell.cmd("cat $BATTERY_DESIGN_CAPACITY_PATH").exec()
            val designCapacity = designCapacityResult.out.firstOrNull()?.trim()?.toIntOrNull() ?: 0
    
            val percentage = if (designCapacity > 0) {
                ((maxCapacity.toDouble() / designCapacity) * 100).roundToInt()
            } else {
                0
            }
    
            "${maxCapacity / 1000} mAh ($percentage%)"
        } catch (e: Exception) {
            Log.e("BatteryUtils", "Error reading maximum capacity", e)
            context.getString(R.string.not_available)
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
