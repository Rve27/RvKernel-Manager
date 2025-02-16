package com.rve.rvkernelmanager.utils

import java.io.File
import android.util.Log
import com.topjohnwu.superuser.Shell

object SoCUtils {

    const val MIN_FREQ_CPU0_PATH = "/sys/devices/system/cpu/cpufreq/policy0/scaling_min_freq"
    const val MAX_FREQ_CPU0_PATH = "/sys/devices/system/cpu/cpufreq/policy0/scaling_max_freq"
    const val AVAILABLE_FREQ_CPU0_PATH = "/sys/devices/system/cpu/cpufreq/policy0/scaling_available_frequencies"
    const val GOV_CPU0_PATH = "/sys/devices/system/cpu/cpufreq/policy0/scaling_governor"
    const val AVAILABLE_GOV_CPU0_PATH = "/sys/devices/system/cpu/cpufreq/policy0/scaling_available_governors"
    
    const val MIN_FREQ_CPU4_PATH = "/sys/devices/system/cpu/cpufreq/policy4/scaling_min_freq"
    const val MAX_FREQ_CPU4_PATH = "/sys/devices/system/cpu/cpufreq/policy4/scaling_max_freq"
    const val AVAILABLE_FREQ_CPU4_PATH = "/sys/devices/system/cpu/cpufreq/policy4/scaling_available_frequencies"
    const val AVAILABLE_BOOST_CPU4_PATH = "/sys/devices/system/cpu/cpufreq/policy4/scaling_boost_frequencies"
    const val GOV_CPU4_PATH = "/sys/devices/system/cpu/cpufreq/policy4/scaling_governor"
    const val AVAILABLE_GOV_CPU4_PATH = "/sys/devices/system/cpu/cpufreq/policy4/scaling_available_governors"

    const val MIN_FREQ_CPU6_PATH = "/sys/devices/system/cpu/cpufreq/policy6/scaling_min_freq"
    const val MAX_FREQ_CPU6_PATH = "/sys/devices/system/cpu/cpufreq/policy6/scaling_max_freq"
    const val AVAILABLE_FREQ_CPU6_PATH = "/sys/devices/system/cpu/cpufreq/policy6/scaling_available_frequencies"
    const val AVAILABLE_BOOST_CPU6_PATH = "/sys/devices/system/cpu/cpufreq/policy6/scaling_boost_frequencies"
    const val GOV_CPU6_PATH = "/sys/devices/system/cpu/cpufreq/policy6/scaling_governor"
    const val AVAILABLE_GOV_CPU6_PATH = "/sys/devices/system/cpu/cpufreq/policy6/scaling_available_governors"
    
    const val MIN_FREQ_CPU7_PATH = "/sys/devices/system/cpu/cpufreq/policy7/scaling_min_freq"
    const val MAX_FREQ_CPU7_PATH = "/sys/devices/system/cpu/cpufreq/policy7/scaling_max_freq"
    const val AVAILABLE_FREQ_CPU7_PATH = "/sys/devices/system/cpu/cpufreq/policy7/scaling_available_frequencies"
    const val AVAILABLE_BOOST_CPU7_PATH = "/sys/devices/system/cpu/cpufreq/policy7/scaling_boost_frequencies"
    const val GOV_CPU7_PATH = "/sys/devices/system/cpu/cpufreq/policy7/scaling_governor"
    const val AVAILABLE_GOV_CPU7_PATH = "/sys/devices/system/cpu/cpufreq/policy7/scaling_available_governors"
    
    const val MIN_FREQ_GPU_PATH = "/sys/class/kgsl/kgsl-3d0/min_clock_mhz"
    const val MAX_FREQ_GPU_PATH = "/sys/class/kgsl/kgsl-3d0/max_clock_mhz"
    const val AVAILABLE_FREQ_GPU_PATH = "/sys/class/kgsl/kgsl-3d0/gpu_available_frequencies"
    const val GOV_GPU_PATH = "/sys/class/kgsl/kgsl-3d0/devfreq/governor"
    const val AVAILABLE_GOV_GPU_PATH = "/sys/class/kgsl/kgsl-3d0/devfreq/available_governors"
    const val ADRENO_BOOST_PATH = "/sys/class/kgsl/kgsl-3d0/devfreq/adrenoboost"
    const val GPU_THROTTLING_PATH = "/sys/class/kgsl/kgsl-3d0/throttling"
    
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
}
