package com.rve.rvkernelmanager.utils

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

fun getTotalRam(context: Context): String {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val totalMem = memoryInfo.totalMem / (1024.0 * 1024 * 1024)
    val totalRam = ceil(totalMem).toInt()

    return "$totalRam GB"
}
