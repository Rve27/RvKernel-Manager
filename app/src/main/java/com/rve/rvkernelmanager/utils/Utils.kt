/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package com.rve.rvkernelmanager.utils

import java.io.File

import android.app.*
import android.os.Build
import android.content.Context
import android.util.Log

import com.topjohnwu.superuser.Shell

object Utils {
    fun getDeviceName() = Build.MODEL

    fun getDeviceCodename() = Build.DEVICE

    fun getAndroidVersion() = Build.VERSION.RELEASE

    fun getSdkVersion() = Build.VERSION.SDK_INT.toString()

    fun getManufacturer() = Build.MANUFACTURER

    fun getSystemProperty(key: String): String = runCatching {
        val clazz = Class.forName("android.os.SystemProperties")
        val method = clazz.getMethod("get", String::class.java)
        method.invoke(null, key) as? String
    }.getOrNull().orEmpty()

    fun getTemp(filePath: String): String {
        val value = readFile(filePath).trim()
        return value.toFloatOrNull()?.div(1000)?.let { "%.1f".format(it) } ?: "N/A"
    }

    fun testFile(filePath: String): Boolean =
        File(filePath).exists() || Shell.cmd("test -f $filePath && echo true || echo false").exec()
            .takeIf { it.isSuccess }?.out?.firstOrNull() == "true"

    fun setPermissions(permission: Int, filePath: String) {
        Shell.cmd("chmod $permission $filePath").exec()
    }

    fun readFile(filePath: String): String =
        Shell.cmd("cat $filePath").exec().takeIf { it.isSuccess }?.out?.joinToString("\n")?.trim().orEmpty()

    fun writeFile(filePath: String, value: String): Boolean = runCatching {
        Shell.cmd("echo $value > $filePath").exec().isSuccess
    }.getOrElse {
        Log.e("writeFile", "Error writing to $filePath: ${it.message}", it)
        false
    }

    fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.longVersionCode})"
        } catch (e: Exception) {
            "Unknown"
        }
    }
}
