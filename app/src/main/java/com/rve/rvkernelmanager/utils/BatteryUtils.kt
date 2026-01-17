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
package com.rve.rvkernelmanager.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.SystemClock
import android.util.Log
import com.rve.rvkernelmanager.R
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
            result.out.firstOrNull()?.trim() ?: context.getString(R.string.unknown)
        } else {
            context.getBatteryIntent()?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: context.getString(R.string.unknown)
        }
    }.getOrElse {
        Log.e(TAG, "getBatteryTechnology: ${it.message}", it)
        context.getBatteryIntent()?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: context.getString(R.string.unknown)
    }

    fun getBatteryHealth(context: Context): String = runCatching {
        when (context.getBatteryIntent()?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> context.getString(R.string.health_good)
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> context.getString(R.string.health_overheat)
            BatteryManager.BATTERY_HEALTH_DEAD -> context.getString(R.string.health_dead)
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> context.getString(R.string.health_over_voltage)
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> context.getString(R.string.health_unspecified)
            BatteryManager.BATTERY_HEALTH_COLD -> context.getString(R.string.health_cold)
            else -> context.getString(R.string.unknown)
        }
    }.getOrElse {
        Log.e(TAG, "getBatteryHealth: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun getBatteryLevel(context: Context): String = runCatching {
        val level = context.getBatteryIntent()?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        if (level != -1) "$level%" else context.getString(R.string.unknown)
    }.getOrElse {
        Log.e(TAG, "getBatteryLevel: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun getBatteryDesignCapacity(context: Context): String = runCatching {
        val result = Shell.cmd("cat $BATTERY_DESIGN_CAPACITY").exec()
        if (result.isSuccess && result.out.isNotEmpty()) {
            val mAh = result.out.firstOrNull()?.trim()?.toIntOrNull()?.div(1000) ?: 0
            return "$mAh mAh"
        } else {
            context.getString(R.string.unknown)
        }
    }.getOrElse {
        Log.e(TAG, "getBatteryDesignCapacity: ${it.message}", it)
        context.getString(R.string.na)
    }

    fun getBatteryMaximumCapacity(context: Context): String = runCatching {
        val maxCapacityResult = Shell.cmd("cat $BATTERY_MAXIMUM_CAPACITY").exec()
        if (!maxCapacityResult.isSuccess || maxCapacityResult.out.isEmpty()) {
            return context.getString(R.string.unknown)
        }

        val maxCapacity = maxCapacityResult.out.firstOrNull()?.trim()?.toIntOrNull() ?: 0
        if (maxCapacity <= 0) return context.getString(R.string.unknown)

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
        context.getString(R.string.na)
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

    fun getDeepSleep(context: Context): String = runCatching {
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
        context.getString(R.string.unknown)
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
            callback(if (level != -1) "$level%" else context.getString(R.string.unknown))
        }

    fun registerBatteryTemperatureListener(context: Context, callback: (String) -> Unit): BroadcastReceiver =
        registerBatteryListener(context) { intent ->
            val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            callback(if (temp != -1) "%.1f °C".format(temp / 10.0) else context.getString(R.string.unknown))
        }

    fun registerBatteryVoltageListener(context: Context, callback: (String) -> Unit): BroadcastReceiver =
        registerBatteryListener(context) { intent ->
            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            callback(if (voltage != -1) "%.3f V".format(voltage / 1000.0) else context.getString(R.string.unknown))
        }

    fun registerBatteryCapacityListener(context: Context, callback: (String) -> Unit): BroadcastReceiver =
        registerBatteryListener(context) {
            callback(getBatteryMaximumCapacity(context))
        }
}
