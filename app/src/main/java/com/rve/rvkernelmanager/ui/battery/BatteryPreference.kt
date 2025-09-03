/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.ui.battery

import android.content.Context
import android.content.SharedPreferences

class BatteryPreference(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("battery_prefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: BatteryPreference? = null

        private const val KEY_MANUAL_DESIGN_CAPACITY = "manual_design_capacity"

        fun getInstance(context: Context): BatteryPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BatteryPreference(context).also { INSTANCE = it }
            }
        }
    }

    fun setManualDesignCapacity(value: Int) {
        prefs.edit().putInt(KEY_MANUAL_DESIGN_CAPACITY, value).apply()
    }

    fun getManualDesignCapacity(): Int {
        return prefs.getInt(KEY_MANUAL_DESIGN_CAPACITY, 0)
    }
}
