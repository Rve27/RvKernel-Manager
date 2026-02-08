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
package com.rve.rvkernelmanager.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.rve.rvkernelmanager.R
import com.topjohnwu.superuser.Shell
import java.io.File
import kotlin.math.ceil

object SoCUtils {
    const val TAG = "SoCUtils"

    const val CPU_TEMP = "/sys/class/thermal/thermal_zone0/temp"

    // CPU 0
    const val MIN_FREQ_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_min_freq"
    const val MAX_FREQ_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_max_freq"
    const val CURRENT_FREQ_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_cur_freq"
    const val AVAILABLE_FREQ_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_available_frequencies"
    const val CPUINFO_MAX_FREQ_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/cpuinfo_max_freq" // NEW
    const val GOV_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_governor"
    const val AVAILABLE_GOV_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_available_governors"

    // CPU 3
    const val MIN_FREQ_CPU3 = "/sys/devices/system/cpu/cpufreq/policy3/scaling_min_freq"
    const val MAX_FREQ_CPU3 = "/sys/devices/system/cpu/cpufreq/policy3/scaling_max_freq"
    const val CURRENT_FREQ_CPU3 = "/sys/devices/system/cpu/cpufreq/policy3/scaling_cur_freq"
    const val AVAILABLE_FREQ_CPU3 = "/sys/devices/system/cpu/cpufreq/policy3/scaling_available_frequencies"
    const val AVAILABLE_BOOST_CPU3 = "/sys/devices/system/cpu/cpufreq/policy3/scaling_boost_frequencies"
    const val CPUINFO_MAX_FREQ_CPU3 = "/sys/devices/system/cpu/cpufreq/policy3/cpuinfo_max_freq" // NEW
    const val GOV_CPU3 = "/sys/devices/system/cpu/cpufreq/policy3/scaling_governor"
    const val AVAILABLE_GOV_CPU3 = "/sys/devices/system/cpu/cpufreq/policy3/scaling_available_governors"

    // CPU 4
    const val MIN_FREQ_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_min_freq"
    const val MAX_FREQ_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_max_freq"
    const val CURRENT_FREQ_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_cur_freq"
    const val AVAILABLE_FREQ_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_available_frequencies"
    const val AVAILABLE_BOOST_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_boost_frequencies"
    const val CPUINFO_MAX_FREQ_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/cpuinfo_max_freq" // NEW
    const val GOV_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_governor"
    const val AVAILABLE_GOV_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_available_governors"

    // CPU 6
    const val MIN_FREQ_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_min_freq"
    const val MAX_FREQ_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_max_freq"
    const val CURRENT_FREQ_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_cur_freq"
    const val AVAILABLE_FREQ_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_available_frequencies"
    const val AVAILABLE_BOOST_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_boost_frequencies"
    const val CPUINFO_MAX_FREQ_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/cpuinfo_max_freq" // NEW
    const val GOV_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_governor"
    const val AVAILABLE_GOV_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_available_governors"

    // CPU 7
    const val MIN_FREQ_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_min_freq"
    const val MAX_FREQ_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_max_freq"
    const val CURRENT_FREQ_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_cur_freq"
    const val AVAILABLE_FREQ_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_available_frequencies"
    const val CPUINFO_MAX_FREQ_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/cpuinfo_max_freq" // NEW
    const val GOV_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_governor"
    const val AVAILABLE_GOV_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_available_governors"

    const val CPU_INPUT_BOOST_MS = "/sys/devices/system/cpu/cpu_boost/input_boost_ms"
    const val CPU_SCHED_BOOST_ON_INPUT = "/sys/devices/system/cpu/cpu_boost/sched_boost_on_input"

    const val MIN_FREQ_GPU = "/sys/class/kgsl/kgsl-3d0/min_clock_mhz"
    const val MAX_FREQ_GPU = "/sys/class/kgsl/kgsl-3d0/max_clock_mhz"
    const val CURRENT_FREQ_GPU = "/sys/class/kgsl/kgsl-3d0/gpuclk"
    const val AVAILABLE_FREQ_GPU = "/sys/class/kgsl/kgsl-3d0/freq_table_mhz"
    const val GOV_GPU = "/sys/class/kgsl/kgsl-3d0/devfreq/governor"
    const val AVAILABLE_GOV_GPU = "/sys/class/kgsl/kgsl-3d0/devfreq/available_governors"
    const val MAX_PWRLEVEL = "/sys/class/kgsl/kgsl-3d0/max_pwrlevel"
    const val MIN_PWRLEVEL = "/sys/class/kgsl/kgsl-3d0/min_pwrlevel"
    const val DEFAULT_PWRLEVEL = "/sys/class/kgsl/kgsl-3d0/default_pwrlevel"
    const val ADRENO_BOOST = "/sys/class/kgsl/kgsl-3d0/devfreq/adrenoboost"
    const val GPU_THROTTLING = "/sys/class/kgsl/kgsl-3d0/throttling"
    const val GPU_TEMP = "/sys/class/kgsl/kgsl-3d0/temp"

    private var sPrevTotal: Long = -1
    private var sPrevIdle: Long = -1

    fun getCpuInfo(context: Context): String = runCatching {
        val hardware = Utils.getSystemProperty("ro.hardware")
        val manufacturer = Utils.getSystemProperty("ro.soc.manufacturer")
        val model = Utils.getSystemProperty("ro.soc.model")

        if (hardware.contains("qcom", ignoreCase = true) && model.isNotEmpty()) {
            "Qualcomm Technologies, Inc $model"
        } else if (manufacturer.contains("QTI", ignoreCase = true) && model.isNotEmpty()) {
            "Qualcomm Technologies, Inc $model"
        } else {
            context.getString(R.string.unknown)
        }
    }.getOrElse {
        Log.e(TAG, "getCpuInfo: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun readFreqCPU(filePath: String): String = runCatching {
        val file = File(filePath)
        if (file.exists()) {
            val freq = file.readText().trim()
            (freq.toInt() / 1000).toString()
        } else {
            "0"
        }
    }.getOrElse {
        Log.e(TAG, "readFreqCPU: ${it.message}", it)
        "0"
    }

    fun writeFreqCPU(filePath: String, frequency: String) {
        runCatching {
            val freqInKHz = (frequency.toInt() * 1000).toString()
            val command = "echo $freqInKHz > $filePath"
            Shell.cmd(command).exec()
        }.onFailure {
            Log.e(TAG, "writeFreqCPU: ${it.message}", it)
        }
    }

    // UPDATED: Now accepts optional cpuInfoMaxPath
    fun readAvailableFreqCPU(filePath: String, cpuInfoMaxPath: String? = null): List<String> = runCatching {
        val frequencies = mutableListOf<String>()

        // 1. Read standard available frequencies
        val file = File(filePath)
        if (file.exists()) {
            val list = file.readText()
                .trim()
                .split(" ")
                .mapNotNull { it.toIntOrNull()?.div(1000)?.toString() }
            frequencies.addAll(list)
        }

        // 2. Read cpuinfo_max_freq (Hardware Limit)
        if (cpuInfoMaxPath != null) {
            val maxFile = File(cpuInfoMaxPath)
            if (maxFile.exists()) {
                val maxFreqRaw = maxFile.readText().trim().toIntOrNull()
                if (maxFreqRaw != null) {
                    val maxFreqMhz = (maxFreqRaw / 1000).toString()
                    frequencies.add(maxFreqMhz)
                }
            }
        }

        // 3. Merge, Remove Duplicates, and Sort
        frequencies
            .distinct()
            .sortedBy { it.toIntOrNull() ?: 0 }

    }.getOrElse {
        Log.e(TAG, "readAvailableFreqCPU: ${it.message}", it)
        emptyList()
    }

    // UPDATED: Now accepts optional cpuInfoMaxPath
    fun readAvailableFreqBoost(freqPath: String, boostPath: String, cpuInfoMaxPath: String? = null): List<String> = runCatching {
        val regularFreq = readAvailableFreqCPU(freqPath, cpuInfoMaxPath) // Pass max info here
        val boostFreq = readAvailableFreqCPU(boostPath, null) // Boost usually doesn't need max info check
        
        (regularFreq + boostFreq)
            .distinct()
            .sortedBy { it.toIntOrNull() ?: 0 }
    }.getOrElse {
        Log.e(TAG, "readAvailableFreqBoost: ${it.message}", it)
        emptyList()
    }

    fun readAvailableGovCPU(filePath: String): List<String> = runCatching {
        val file = File(filePath)
        if (file.exists()) {
            file.readText()
                .trim()
                .split(" ")
        } else {
            emptyList()
        }
    }.getOrElse {
        Log.e(TAG, "readAvailableGovCPU: ${it.message}", it)
        emptyList()
    }

    fun getOpenGL(context: Context): String = runCatching {
        val result = Shell.cmd("dumpsys SurfaceFlinger | grep \"GLES:\"").exec()
        if (result.isSuccess && result.out.isNotEmpty()) {
            val glesLine = result.out.firstOrNull()?.trim()
            if (!glesLine.isNullOrBlank()) {
                val regex = Regex("GLES:\\s*[^,]+,\\s*(.+)")
                val matchResult = regex.find(glesLine)
                if (matchResult != null) {
                    val gpuInfo = matchResult.groupValues[1].trim()
                    return gpuInfo
                } else {
                    val commaIndex = glesLine.indexOf(',')
                    if (commaIndex != -1 && commaIndex < glesLine.length - 1) {
                        return glesLine.substring(commaIndex + 1).trim()
                    }
                }
            }
        }
        context.getString(R.string.unknown)
    }.getOrElse {
        Log.e(TAG, "getOpenGL: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun writeFreqGPU(filePath: String, frequency: String) {
        runCatching {
            Shell.cmd("echo $frequency > $filePath").exec()
        }.onFailure {
            Log.e(TAG, "writeFreqGPU: ${it.message}", it)
        }
    }

    fun readAvailableFreqGPU(filePath: String): List<String> = runCatching {
        val result = Shell.cmd("cat $filePath").exec()
        if (result.isSuccess) {
            result.out
                .flatMap { it.trim().split("\\s+".toRegex()) }
                .filter { it.isNotEmpty() }
        } else {
            Log.e("readAvailableFreqGPU", "Command execution failed: ${result.err}")
            emptyList()
        }
    }.getOrElse {
        Log.e(TAG, "readAvailableFreqGPU: ${it.message}", it)
        emptyList()
    }

    fun readAvailableGovGPU(filePath: String): List<String> = runCatching {
        val result = Shell.cmd("cat $filePath").exec()
        if (result.isSuccess) {
            result.out
                .flatMap { it.trim().split("\\s+".toRegex()) }
                .filter { it.isNotEmpty() }
        } else {
            emptyList()
        }
    }.getOrElse {
        Log.e(TAG, "readAvailableGovGPU: ${it.message}", it)
        emptyList()
    }

    fun readFreqGPU(filePath: String): String = runCatching {
        val result = Shell.cmd("cat $filePath").exec()
        if (result.isSuccess) {
            result.out.firstOrNull()
                ?.trim()
                ?.let { (it.toLong() / 1000000).toString() }
                ?: "0"
        } else {
            "0"
        }
    }.getOrElse {
        Log.e(TAG, "readCurrentGPUFreq: ${it.message}", it)
        "0"
    }

    fun getCpuUsage(context: Context): String = runCatching {
        val stat = Utils.readFile("/proc/stat")
        val trimmedStat = stat.trim()

        if (!trimmedStat.startsWith("cpu")) return context.getString(R.string.unknown)

        val parts = trimmedStat.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (parts.size < 8) return context.getString(R.string.unknown)

        val user = parts[1].toLong()
        val nice = parts[2].toLong()
        val system = parts[3].toLong()
        val idle = parts[4].toLong()
        val iowait = parts[5].toLong()
        val irq = parts[6].toLong()
        val softirq = parts[7].toLong()
        val steal = if (parts.size > 8) parts[8].toLong() else 0

        val total = user + nice + system + idle + iowait + irq + softirq + steal

        if (sPrevTotal != -1L && total > sPrevTotal) {
            val diffTotal = total - sPrevTotal
            val diffIdle = idle - sPrevIdle
            val usage = 100 * (diffTotal - diffIdle) / diffTotal
            sPrevTotal = total
            sPrevIdle = idle
            return usage.toString()
        } else {
            sPrevTotal = total
            sPrevIdle = idle
            return context.getString(R.string.unknown)
        }
    }.getOrElse {
        Log.e(TAG, "getCpuUsage: ${it.message}", it)
        return context.getString(R.string.unknown)
    }

    fun getGpuUsage(context: Context): String = runCatching {
        val usage = Utils.readFile("/sys/class/kgsl/kgsl-3d0/gpu_busy_percentage")
        if (usage.isEmpty()) return context.getString(R.string.unknown)
        val cleanedUsage = usage.replace("%", "").trim()
        val value = cleanedUsage.toInt()
        value.toString()
    }.getOrElse {
        Log.e(TAG, "getGpuUsage: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun getTotalRam(context: Context): String = runCatching {
        val memoryInfo = ActivityManager.MemoryInfo().apply {
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(this)
        }
        val sizeInGb = memoryInfo.totalMem / (1024.0 * 1024 * 1024)
        return "${ceil(sizeInGb).toInt()} GB"
    }.getOrElse {
        Log.e(TAG, "getTotalRam: ${it.message}", it)
        context.getString(R.string.unknown)
    }
}
