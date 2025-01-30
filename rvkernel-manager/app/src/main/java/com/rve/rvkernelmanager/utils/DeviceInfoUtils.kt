package com.rve.rvkernelmanager.utils

import android.system.Os
import android.os.Build
import android.content.Context
import android.app.ActivityManager
import android.util.Log
import kotlin.math.ceil
import com.topjohnwu.superuser.Shell

const val FULL_KERNEL_VERSION_PATH = "/proc/version"
const val CPU_INFO_PATH = "/proc/cpuinfo"

fun getDeviceCodename(): String {
    return Build.DEVICE
}

fun getAndroidVersion(): String {
    return Build.VERSION.RELEASE
}

fun getCPU(): String {
    return Build.SOC_MODEL
}

fun getCPUInfo(): String {
    return try {
        val hardwareResult = Shell.cmd("cat $CPU_INFO_PATH | grep 'Hardware' | head -n 1").exec()
        val coresResult = Shell.cmd("cat $CPU_INFO_PATH | grep 'processor' | wc -l").exec()
        val archResult = Shell.cmd("cat $CPU_INFO_PATH | grep 'Processor' | head -n 1").exec()

        val hardware = if (hardwareResult.isSuccess) {
            hardwareResult.out.firstOrNull()?.replace("Hardware\t: ", "")?.trim() ?: "Unknown"
        } else "Unknown"

        val cores = if (coresResult.isSuccess) {
            coresResult.out.firstOrNull()?.trim() ?: "0"
        } else "0"

        val arch = if (archResult.isSuccess) {
            val processor = archResult.out.firstOrNull()?.trim() ?: ""
            if (processor.contains("AArch64")) "AArch64" else "Unknown"
        } else "Unknown"

        "$hardware\n$cores Cores ($arch)"
    } catch (e: Exception) {
        Log.e("getCPUInfo", "Exception during command execution", e)
        "Exception during command execution"
    }
}

fun getTotalRam(context: Context): String {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val totalMem = memoryInfo.totalMem / (1024.0 * 1024 * 1024)
    val totalRam = ceil(totalMem).toInt()

    return "$totalRam GB"
}

fun getSystemProperty(key: String, defaultValue: String): String {
    return try {
        val systemPropertiesClass = Class.forName("android.os.SystemProperties")
        val getMethod = systemPropertiesClass.getMethod("get", String::class.java, String::class.java)
        getMethod.invoke(null, key, defaultValue) as String
    } catch (e: Exception) {
        e.printStackTrace()
        defaultValue
    }
}

fun getRvOSVersion(): String? {
    val rvosVersion = getSystemProperty("ro.rvos.version", "")
    val rvosBuildType = getSystemProperty("ro.rvos.build.type", "")
    
    return if (rvosVersion.isNotEmpty() || rvosBuildType.isNotEmpty()) {
        buildString {
            append(rvosVersion)
            if (rvosBuildType.isNotEmpty()) {
                append(" ")
                append(rvosBuildType)
            }
        }
    } else null
}

fun getKernelVersion(): String {
    return try {
        Os.uname().release
    } catch (e: Exception) {
        e.printStackTrace()
        "Unable to get kernel version"
    }
}
