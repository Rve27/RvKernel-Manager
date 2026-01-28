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

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

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
        prefs.edit { putInt(KEY_MANUAL_DESIGN_CAPACITY, value) }
    }

    fun getManualDesignCapacity(): Int {
        return prefs.getInt(KEY_MANUAL_DESIGN_CAPACITY, 0)
    }
}
