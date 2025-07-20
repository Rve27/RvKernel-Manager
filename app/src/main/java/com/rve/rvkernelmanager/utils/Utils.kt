/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package com.rve.rvkernelmanager.utils

import java.io.File

import android.os.Build
import android.content.Context
import android.app.ActivityManager
import android.util.Log

import kotlin.math.*

import com.topjohnwu.superuser.Shell

object Utils {

    const val CPU_INFO = "/proc/cpuinfo"
    const val GPU_MODEL = "/sys/class/kgsl/kgsl-3d0/gpu_model"

    fun getDeviceName() = Build.MODEL
    fun getDeviceCodename() = Build.DEVICE
    fun getAndroidVersion() = Build.VERSION.RELEASE
    fun getSdkVersion() = Build.VERSION.SDK_INT.toString()

    fun getCPUInfo(): String = shellReadLine("cat $CPU_INFO | grep 'Hardware' | head -n 1")
        ?.replace("Hardware\t: ", "")?.trim().orUnknown()
    
    fun getExtendCPUInfo(): String {
        val hardware = getCPUInfo()
        val cores = shellReadLine("cat $CPU_INFO | grep 'processor' | wc -l") ?: "0"
        val archLine = shellReadLine("cat $CPU_INFO | grep 'Processor' | head -n 1")
        val arch = if (archLine?.contains("AArch64") == true) "AArch64" else "Unknown"
        return "$hardware\n$cores Cores ($arch)"
    }
    
    fun getTotalRam(context: Context): String {
        val memoryInfo = ActivityManager.MemoryInfo().apply {
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(this)
        }
	val sizeInGb = memoryInfo.totalMem / (1024.0 * 1024 * 1024)
	return "${ceil(sizeInGb).toInt()} GB"
    }

    fun getSystemProperty(key: String): String = runCatching {
        val clazz = Class.forName("android.os.SystemProperties")
        val method = clazz.getMethod("get", String::class.java)
        method.invoke(null, key) as? String
    }.getOrNull().orEmpty()

    fun getGPUModel(): String = shellReadLine("cat $GPU_MODEL")?.let { raw ->
        raw.replace("Adreno", "Adreno (TM) ").replace(Regex("v\\d+"), "").trim()
    }.orUnknown()

    fun getTemp(filePath: String): String {
        val value = readFile(filePath).trim()
        return value.toFloatOrNull()?.div(1000)?.let { "%.1f".format(it) } ?: "N/A"
    }

    fun testFile(filePath: String): Boolean =
        File(filePath).exists() || shellReadLine("test -f $filePath && echo true || echo false") == "true"

    fun setPermissions(permission: Int, filePath: String) {
        Shell.cmd("chmod $permission $filePath").exec()
    }

    fun readFile(filePath: String): String =
        shellReadLines("cat $filePath")?.joinToString("\n")?.trim().orEmpty()

    fun writeFile(filePath: String, value: String): Boolean = runCatching {
        Shell.cmd("echo $value > $filePath").exec().isSuccess
    }.getOrElse {
        Log.e("writeFile", "Error writing to $filePath: ${it.message}", it)
        false
    }

    private fun shellReadLine(command: String): String? =
        Shell.cmd(command).exec().takeIf { it.isSuccess }?.out?.firstOrNull()

    private fun shellReadLines(command: String): List<String>? =
        Shell.cmd(command).exec().takeIf { it.isSuccess }?.out

    private fun String?.orUnknown() = this?.takeIf { it.isNotBlank() } ?: "Unknown"
}
