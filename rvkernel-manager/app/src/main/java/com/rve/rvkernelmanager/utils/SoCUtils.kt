package com.rve.rvkernelmanager.utils

import java.io.File
import android.util.Log
import com.topjohnwu.superuser.Shell

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

fun readFreqFile(filePath: String): String {
    return try {
        val file = File(filePath)
        if (file.exists()) {
            val freq = file.readText().trim()
            (freq.toInt() / 1000).toString()
        } else {
            ""
        }
    } catch (e: Exception) {
        Log.e("readFreqFile", "Error reading file $filePath: ${e.message}", e)
        ""
    }
}

fun writeFreqFile(filePath: String, frequency: String) {
    try {
        val freqInKHz = (frequency.toInt() * 1000).toString()
        val command = "echo $freqInKHz > $filePath"
        Shell.cmd(command).exec()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun readAvailableFreq(filePath: String): List<String> {
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
        Log.e("readAvailableFreq", "Error reading file $filePath: ${e.message}", e)
        emptyList()
    }
}

fun readAvailableFreqBoost(freqPath: String, boostPath: String): List<String> {
    val regularFreq = readAvailableFreq(freqPath)
    val boostFreq = readAvailableFreq(boostPath)
    return (regularFreq + boostFreq)
	.distinct()
	.sortedBy { it.toIntOrNull() ?: 0 }
}

fun readAvailableGov(filePath: String): List<String> {
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
