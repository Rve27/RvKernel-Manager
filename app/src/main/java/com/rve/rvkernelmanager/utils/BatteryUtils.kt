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
            result.out.firstOrNull()?.trim() ?: "unknown"
        } else {
            context.getBatteryIntent()?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "unknown"
        }
    }.getOrElse {
        Log.e(TAG, "getBatteryTechnology: ${it.message}", it)
        context.getBatteryIntent()?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "unknown"
    }

    fun getBatteryHealth(context: Context): String = runCatching {
        when (context.getBatteryIntent()?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "unknown"
        }
    }.getOrElse {
        Log.e(TAG, "getBatteryHealth: ${it.message}", it)
        "unknown"
    }

    fun getBatteryLevel(context: Context): String = runCatching {
        val level = context.getBatteryIntent()?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        if (level != -1) "$level%" else "unknown"
    }.getOrElse {
        Log.e(TAG, "getBatteryLevel: ${it.message}", it)
        "unknown"
    }

    fun getBatteryDesignCapacity(): String = runCatching {
        val result = Shell.cmd("cat $BATTERY_DESIGN_CAPACITY").exec()
        if (result.isSuccess && result.out.isNotEmpty()) {
            val mAh = result.out.firstOrNull()?.trim()?.toIntOrNull()?.div(1000) ?: 0
            return "$mAh mAh"
        } else {
            "unknown"
        }
    }.getOrElse {
        Log.e(TAG, "getBatteryDesignCapacity: ${it.message}", it)
        "N/A"
    }

    fun getBatteryMaximumCapacity(context: Context): String = runCatching {
        val maxCapacityResult = Shell.cmd("cat $BATTERY_MAXIMUM_CAPACITY").exec()
        if (!maxCapacityResult.isSuccess || maxCapacityResult.out.isEmpty()) {
            return "unknown"
        }

        val maxCapacity = maxCapacityResult.out.firstOrNull()?.trim()?.toIntOrNull() ?: 0
        if (maxCapacity <= 0) return "unknown"

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
            "${maxCapacity / 1000} mAh (%)"
        }
    }.getOrElse {
        Log.e(TAG, "getBatteryMaximumCapacity: ${it.message}", it)
        "N/A"
    }

    fun getUptime(): String = runCatching {
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
    }.getOrElse {
        Log.e(TAG, "getUptime: ${it.message}", it)
        "unknown"
    }

    fun getDeepSleep(): String = runCatching {
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
    }.getOrElse {
        Log.e(TAG, "getDeepSleep: ${it.message}", it)
        "unknown"
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
            callback(if (level != -1) "$level%" else "unknown")
        }

    fun registerBatteryTemperatureListener(context: Context, callback: (String) -> Unit): BroadcastReceiver =
        registerBatteryListener(context) { intent ->
            val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            callback(if (temp != -1) "%.1f °C".format(temp / 10.0) else "unknown")
        }

    fun registerBatteryVoltageListener(context: Context, callback: (String) -> Unit): BroadcastReceiver =
        registerBatteryListener(context) { intent ->
            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            callback(if (voltage != -1) "%.3f V".format(voltage / 1000.0) else "unknown")
        }

    fun registerBatteryCapacityListener(context: Context, callback: (String) -> Unit): BroadcastReceiver =
        registerBatteryListener(context) {
            callback(getBatteryMaximumCapacity(context))
        }
}
