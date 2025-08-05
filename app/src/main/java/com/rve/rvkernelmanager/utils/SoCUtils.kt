/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.rve.rvkernelmanager.utils.Utils
import com.topjohnwu.superuser.Shell
import java.io.File
import kotlin.math.ceil

object SoCUtils {

    const val CPU_INFO = "/proc/cpuinfo"
    const val CPU_TEMP = "/sys/class/thermal/thermal_zone0/temp"

    const val MIN_FREQ_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_min_freq"
    const val MAX_FREQ_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_max_freq"
    const val CURRENT_FREQ_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_cur_freq"
    const val AVAILABLE_FREQ_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_available_frequencies"
    const val GOV_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_governor"
    const val AVAILABLE_GOV_CPU0 = "/sys/devices/system/cpu/cpufreq/policy0/scaling_available_governors"

    const val MIN_FREQ_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_min_freq"
    const val MAX_FREQ_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_max_freq"
    const val CURRENT_FREQ_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_cur_freq"
    const val AVAILABLE_FREQ_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_available_frequencies"
    const val AVAILABLE_BOOST_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_boost_frequencies"
    const val GOV_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_governor"
    const val AVAILABLE_GOV_CPU4 = "/sys/devices/system/cpu/cpufreq/policy4/scaling_available_governors"

    const val MIN_FREQ_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_min_freq"
    const val MAX_FREQ_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_max_freq"
    const val CURRENT_FREQ_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_cur_freq"
    const val AVAILABLE_FREQ_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_available_frequencies"
    const val AVAILABLE_BOOST_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_boost_frequencies"
    const val GOV_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_governor"
    const val AVAILABLE_GOV_CPU6 = "/sys/devices/system/cpu/cpufreq/policy6/scaling_available_governors"

    const val MIN_FREQ_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_min_freq"
    const val MAX_FREQ_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_max_freq"
    const val CURRENT_FREQ_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_cur_freq"
    const val AVAILABLE_FREQ_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_available_frequencies"
    const val AVAILABLE_BOOST_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_boost_frequencies"
    const val GOV_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_governor"
    const val AVAILABLE_GOV_CPU7 = "/sys/devices/system/cpu/cpufreq/policy7/scaling_available_governors"

    const val CPU_INPUT_BOOST_MS = "/sys/devices/system/cpu/cpu_boost/input_boost_ms"
    const val CPU_SCHED_BOOST_ON_INPUT = "/sys/devices/system/cpu/cpu_boost/sched_boost_on_input"

    const val GPU_MODEL = "/sys/class/kgsl/kgsl-3d0/gpu_model"

    const val MIN_FREQ_GPU = "/sys/class/kgsl/kgsl-3d0/min_clock_mhz"
    const val MAX_FREQ_GPU = "/sys/class/kgsl/kgsl-3d0/max_clock_mhz"
    const val CURRENT_FREQ_GPU = "/sys/class/kgsl/kgsl-3d0/gpuclk"
    const val AVAILABLE_FREQ_GPU = "/sys/class/kgsl/kgsl-3d0/gpu_available_frequencies"
    const val GOV_GPU = "/sys/class/kgsl/kgsl-3d0/devfreq/governor"
    const val AVAILABLE_GOV_GPU = "/sys/class/kgsl/kgsl-3d0/devfreq/available_governors"
    const val DEFAULT_PWRLEVEL = "/sys/class/kgsl/kgsl-3d0/default_pwrlevel"
    const val ADRENO_BOOST = "/sys/class/kgsl/kgsl-3d0/devfreq/adrenoboost"
    const val GPU_THROTTLING = "/sys/class/kgsl/kgsl-3d0/throttling"
    const val GPU_TEMP = "/sys/class/kgsl/kgsl-3d0/temp"

    private var sPrevTotal: Long = -1
    private var sPrevIdle: Long = -1

    fun getCPUInfo(): String = Shell.cmd("cat $CPU_INFO | grep 'Hardware' | head -n 1").exec()
        .takeIf { it.isSuccess }?.out?.firstOrNull()
        ?.replace("Hardware\t: ", "")?.trim()?.takeIf { it.isNotBlank() } ?: "Unknown"

    fun getExtendCPUInfo(): String {
        val hardware = getCPUInfo()
        val cores = Shell.cmd("cat $CPU_INFO | grep 'processor' | wc -l").exec()
            .takeIf { it.isSuccess }?.out?.firstOrNull() ?: "0"
        val archLine = Shell.cmd("cat $CPU_INFO | grep 'Processor' | head -n 1").exec()
            .takeIf { it.isSuccess }?.out?.firstOrNull()
        val arch = if (archLine?.contains("AArch64") == true) "AArch64" else "Unknown"
        return "$hardware\n$cores Cores ($arch)"
    }

    fun readFreqCPU(filePath: String): String {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                val freq = file.readText().trim()
                (freq.toInt() / 1000).toString()
            } else {
                ""
            }
        } catch (e: Exception) {
            Log.e("readFreqCPU", "Error reading file $filePath: ${e.message}", e)
            ""
        }
    }

    fun writeFreqCPU(filePath: String, frequency: String) {
        try {
            val freqInKHz = (frequency.toInt() * 1000).toString()
            val command = "echo $freqInKHz > $filePath"
            Shell.cmd(command).exec()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readAvailableFreqCPU(filePath: String): List<String> {
        return try {
            val file = File("$filePath")
            if (file.exists()) {
                file.readText()
                    .trim()
                    .split(" ")
                    .map { (it.toInt() / 1000).toString() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("readAvailableFreqCPU", "Error reading file $filePath: ${e.message}", e)
            emptyList()
        }
    }

    fun readAvailableFreqBoost(freqPath: String, boostPath: String): List<String> {
        val regularFreq = readAvailableFreqCPU(freqPath)
        val boostFreq = readAvailableFreqCPU(boostPath)
        return (regularFreq + boostFreq)
            .distinct()
            .sortedBy { it.toIntOrNull() ?: 0 }
    }

    fun readAvailableGovCPU(filePath: String): List<String> {
        return try {
            val file = File("$filePath")
            if (file.exists()) {
                file.readText()
                    .trim()
                    .split(" ")
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("readAvailableGov", "Error reading file $filePath: ${e.message}", e)
            emptyList()
        }
    }

    fun getGPUModel(): String = Shell.cmd("cat $GPU_MODEL").exec()
        .takeIf { it.isSuccess }?.out?.firstOrNull()?.let { raw ->
            raw.replace("Adreno", "Adreno (TM) ").replace(Regex("v\\d+"), "").trim()
        }?.takeIf { it.isNotBlank() } ?: "Unknown"

    fun writeFreqGPU(filePath: String, frequency: String) {
        try {
            val freqInKHz = frequency.replace("000000", "")
            val command = "echo $freqInKHz > $filePath"
            Shell.cmd(command).exec()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readAvailableFreqGPU(filePath: String): List<String> {
        return try {
            val result = Shell.cmd("cat $filePath").exec()
            if (result.isSuccess) {
                result.out.firstOrNull()
                    ?.trim()
                    ?.split(" ")
                    ?.map { (it.toInt() / 1000000).toString() }
                    ?: emptyList()
            } else {
                Log.e("readAvailableFreqGPU", "Command execution failed: ${result.err}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("readAvailableFreqGPU", "Error reading file $filePath: ${e.message}", e)
            emptyList()
        }
    }

    fun readAvailableGovGPU(filePath: String): List<String> {
        return try {
            val result = Shell.cmd("cat $filePath").exec()
            if (result.isSuccess) {
                result.out.firstOrNull()
                    ?.trim()
                    ?.split(" ")
                    ?: emptyList()
            } else {
                Log.e("readAvailableGovGPU", "Command execution failed: ${result.err}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("readAvailableGovGPU", "Error reading file $filePath: ${e.message}", e)
            emptyList()
        }
    }

    fun readFreqGPU(filePath: String): String {
        return try {
            val result = Shell.cmd("cat $filePath").exec()
            if (result.isSuccess) {
                result.out.firstOrNull()
                    ?.trim()
                    ?.let { (it.toLong() / 1000000).toString() }
                    ?: ""
            } else {
                Log.e("readCurrentGPUFreq", "Command execution failed: ${result.err}")
                ""
            }
        } catch (e: Exception) {
            Log.e("readCurrentGPUFreq", "Error reading file $filePath: ${e.message}", e)
            ""
        }
    }

    fun getCpuUsage(): String {
        val stat = Utils.readFile("/proc/stat") ?: return "N/A"
        val trimmedStat = stat.trim()

        if (!trimmedStat.startsWith("cpu")) return "N/A"

        val parts = trimmedStat.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (parts.size < 8) return "N/A"

        try {
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
                return "N/A"
            }
        } catch (e: NumberFormatException) {
            Log.e("SoCUtils", "Error parsing CPU stats: ${e.message}")
            return "N/A"
        }
    }

    fun getGpuUsage(): String {
        val usage = Utils.readFile("/sys/class/kgsl/kgsl-3d0/gpu_busy_percentage")
        if (usage.isEmpty()) return "N/A"
        val cleanedUsage = usage.replace("%", "").trim()
        return try {
            val value = cleanedUsage.toInt()
            value.toString()
        } catch (e: NumberFormatException) {
            "N/A"
        }
    }

    fun getTotalRam(context: Context): String {
        val memoryInfo = ActivityManager.MemoryInfo().apply {
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(this)
        }
        val sizeInGb = memoryInfo.totalMem / (1024.0 * 1024 * 1024)
        return "${ceil(sizeInGb).toInt()} GB"
    }
}
