package com.rve.rvkernelmanager.utils

import android.system.Os
import android.os.Build
import android.content.Context
import android.app.ActivityManager
import android.util.Log
import kotlin.math.ceil
import com.topjohnwu.superuser.Shell

const val FULL_KERNEL_VERSION_PATH = "/proc/version"

fun getDeviceCodename(): String {
    return Build.DEVICE
}

fun getAndroidVersion(): String {
    return Build.VERSION.RELEASE
}

fun getSOC(): String {
    return Build.SOC_MODEL
}

fun getGPUModel(): String {
    return try {
        val result = Shell.cmd("dumpsys SurfaceFlinger | grep GLES | head -n 1 | cut -f 2 -d ','").exec()
        if (result.isSuccess) {
            result.out.firstOrNull()?.trim() ?: "Unknown GPU"
        } else {
            Log.e("getGPUVersion", "Command execution failed: ${result.err}")
            "Command execution failed"
        }
    } catch (e: Exception) {
        Log.e("getGPUVersion", "Exception during command execution", e)
        "Exception during command execution"
    }
}

fun getGLESVersion(): String {
    return try {
        val result = Shell.cmd("dumpsys SurfaceFlinger | grep GLES | head -n 1 | cut -f 2,3,4,5 -d ','").exec()
        if (result.isSuccess) {
            result.out.firstOrNull()?.trim() ?: "Unknown GPU"
        } else {
            Log.e("getGLESVersion", "Command execution failed: ${result.err}")
            "Command execution failed"
        }
    } catch (e: Exception) {
        Log.e("getGLESVersion", "Exception during command execution", e)
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
    val majorVersion = getSystemProperty("ro.rvos.version.major", "")
    val minorVersion = getSystemProperty("ro.rvos.version.minor", "")
    
    return if (majorVersion.isNotEmpty() || minorVersion.isNotEmpty()) {
        buildString {
            append(majorVersion)
            if (minorVersion.isNotEmpty()) {
                append(" ")
                append(minorVersion)
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
