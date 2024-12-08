package com.rve.rvkernelmanager.utils

import android.system.Os
import android.os.Build
import android.content.Context
import android.app.ActivityManager
import kotlin.math.ceil

fun getDeviceCodename(): String {
    return Build.DEVICE
}

fun getAndroidVersion(): String {
    return Build.VERSION.RELEASE
}

fun getSOC(): String {
    return Build.SOC_MODEL
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
    return if (rvosVersion.isNotEmpty()) rvosVersion else null
}

fun getKernelVersion(): String {
    return try {
        Os.uname().release
    } catch (e: Exception) {
        e.printStackTrace()
        "Unable to get kernel version"
    }
}
