/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.SystemClock
import android.util.Log
import com.rve.rvkernelmanager.ui.battery.BatteryPreference
import com.topjohnwu.superuser.Shell
import kotlin.math.roundToInt

object BatteryUtils {

    const val FAST_CHARGING = "/sys/kernel/fast_charge/force_fast_charge"
    const val BYPASS_CHARGING = "/sys/class/power_supply/battery/input_suspend"
    const val BATTERY_DESIGN_CAPACITY = "/sys/class/power_supply/battery/charge_full_design"
    const val BATTERY_MAXIMUM_CAPACITY = "/sys/class/power_supply/battery/charge_full"
    const val BATTERY_TECHNOLOGY = "/sys/class/power_supply/battery/technology"

    const val THERMAL_SCONFIG = "/sys/class/thermal/thermal_message/sconfig"

    const val TAG = "BatteryUtils"

    private fun Context.getBatteryIntent(): Intent? = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

    fun getBatteryTechnology(context: Context): String = runCatching {
        val result = Shell.cmd("cat $BATTERY_TECHNOLOGY").exec()
        if (result.isSuccess && result.out.isNotEmpty()) {
            result.out.firstOrNull()?.trim() ?: "N/A"
        } else {
            context.getBatteryIntent()?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "N/A"
        }
    }.getOrElse {
        Log.e(TAG, "Error reading battery technology", it)
        context.getBatteryIntent()?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "N/A"
    }

    fun getBatteryHealth(context: Context): String {
        return when (context.getBatteryIntent()?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "N/A"
        }
    }

    fun getBatteryLevel(context: Context): String {
        val level = context.getBatteryIntent()?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        return if (level != -1) "$level%" else "N/A"
    }

    fun getBatteryDesignCapacity(): String = runCatching {
        val result = Shell.cmd("cat $BATTERY_DESIGN_CAPACITY").exec()
        if (result.isSuccess && result.out.isNotEmpty()) {
            val mAh = result.out.firstOrNull()?.trim()?.toIntOrNull()?.div(1000) ?: 0
            return "$mAh mAh"
        } else {
            Log.w(TAG, "Failed to read design capacity: ${result.err}")
            "N/A"
        }
    }.getOrElse {
        Log.e(TAG, "Error reading design capacity", it)
        "N/A"
    }

    fun getBatteryMaximumCapacity(context: Context): String = runCatching {
        val maxCapacityResult = Shell.cmd("cat $BATTERY_MAXIMUM_CAPACITY").exec()
        if (!maxCapacityResult.isSuccess || maxCapacityResult.out.isEmpty()) {
            return "N/A"
        }

        val maxCapacity = maxCapacityResult.out.firstOrNull()?.trim()?.toIntOrNull() ?: 0
        if (maxCapacity <= 0) return "N/A"

        var designCapacity = 0

        val designCapacityResult = Shell.cmd("cat $BATTERY_DESIGN_CAPACITY").exec()
        if (designCapacityResult.isSuccess && designCapacityResult.out.isNotEmpty()) {
            designCapacity = designCapacityResult.out.firstOrNull()?.trim()?.toIntOrNull() ?: 0
        }

        if (designCapacity == 0) {
            val batteryPreference = BatteryPreference.getInstance(context)
            val manualCapacity = batteryPreference.getManualDesignCapacity()
            if (manualCapacity > 0) {
                designCapacity = manualCapacity * 1000
            }
        }

        if (designCapacity > 0) {
            val percentage = (maxCapacity / designCapacity.toDouble() * 100).roundToInt()
            "${maxCapacity / 1000} mAh ($percentage%)"
        } else {
            "${maxCapacity / 1000} mAh (N/A%)"
        }
    }.getOrElse {
        Log.e(TAG, "Error reading maximum capacity", it)
        "N/A"
    }

    fun getUptime(): String {
        val uptimeMillis = SystemClock.elapsedRealtime()
        val seconds = (uptimeMillis / 1000) % 60
        val minutes = (uptimeMillis / (1000 * 60)) % 60
        val hours = (uptimeMillis / (1000 * 60 * 60)) % 24
        val days = (uptimeMillis / (1000 * 60 * 60 * 24))

        return buildString {
            if (days > 0) append("${days}d ")
            if (hours > 0 || days > 0) append("${hours}h ")
            if (minutes > 0 || hours > 0 || days > 0) append("${minutes}m ")
            append("${seconds}s")
        }.trim()
    }

    fun getDeepSleep(): String {
        val deepSleepMillis = SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()
        val seconds = (deepSleepMillis / 1000) % 60
        val minutes = (deepSleepMillis / (1000 * 60)) % 60
        val hours = (deepSleepMillis / (1000 * 60 * 60)) % 24
        val days = deepSleepMillis / (1000 * 60 * 60 * 24)

        val percentage = if (SystemClock.elapsedRealtime() > 0) {
            (deepSleepMillis * 100 / SystemClock.elapsedRealtime()).toInt()
        } else {
            0
        }

        return buildString {
            if (days > 0) append("${days}d ")
            if (hours > 0 || days > 0) append("${hours}h ")
            if (minutes > 0 || hours > 0 || days > 0) append("${minutes}m ")
            append("${seconds}s")
            append(" ($percentage%)")
        }.trim()
    }

    private fun registerBatteryListener(context: Context, onReceive: (Intent) -> Unit): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                intent?.let(onReceive)
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return receiver
    }

    fun registerBatteryLevelListener(context: Context, callback: (String) -> Unit): BroadcastReceiver =
        registerBatteryListener(context) { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            callback(if (level != -1) "$level%" else "N/A")
        }

    fun registerBatteryTemperatureListener(context: Context, callback: (String) -> Unit): BroadcastReceiver =
        registerBatteryListener(context) { intent ->
            val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            callback(if (temp != -1) "%.1f °C".format(temp / 10.0) else "N/A")
        }

    fun registerBatteryVoltageListener(context: Context, callback: (String) -> Unit): BroadcastReceiver =
        registerBatteryListener(context) { intent ->
            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            callback(if (voltage != -1) "%.3f V".format(voltage / 1000.0) else "N/A")
        }

    fun registerBatteryCapacityListener(context: Context, callback: (String) -> Unit): BroadcastReceiver =
        registerBatteryListener(context) {
            callback(getBatteryMaximumCapacity(context))
        }
}
