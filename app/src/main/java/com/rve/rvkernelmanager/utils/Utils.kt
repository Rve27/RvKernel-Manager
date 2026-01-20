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
package com.rve.rvkernelmanager.utils

import android.content.Context
import android.os.Build
import android.util.Log
import com.rve.rvkernelmanager.R
import com.topjohnwu.superuser.Shell
import java.io.File

object Utils {
    const val TAG = "Utils"

    fun getDeviceName(context: Context): String = runCatching {
        Build.MODEL
    }.getOrElse {
        Log.e(TAG, "getDeviceName: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun getDeviceCodename(context: Context): String = runCatching {
        Build.DEVICE
    }.getOrElse {
        Log.e(TAG, "getDeviceCodename: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun getAndroidVersion(context: Context): String = runCatching {
        Build.VERSION.RELEASE
    }.getOrElse {
        Log.e(TAG, "getAndroidVersion: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun getSdkVersion(): Int = runCatching {
        Build.VERSION.SDK_INT
    }.getOrElse {
        Log.e(TAG, "getSdkVersion: ${it.message}", it)
        0
    }

    fun getManufacturer(context: Context): String = runCatching {
        Build.MANUFACTURER
    }.getOrElse {
        Log.e(TAG, "getManufacturer: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun getSystemProperty(key: String): String = runCatching {
        Shell.cmd("getprop $key").exec().let {
            if (!it.isSuccess) {
                Log.e(TAG, "getSystemProperty: ${it.err}")
            }
            it.out.firstOrNull()?.trim().orEmpty()
        }
    }.onFailure {
        Log.e(TAG, "getSystemProperty: ${it.message}", it)
    }.getOrDefault("")

    fun getTemp(context: Context, filePath: String): String = runCatching {
        val value = readFile(filePath).trim()
        value.toFloatOrNull()?.div(1000)?.let { "%.1f".format(it) } ?: context.getString(R.string.unknown)
    }.getOrElse {
        Log.e(TAG, "getTemp: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun testFile(filePath: String): Boolean = runCatching {
        File(filePath).exists() ||
            Shell.cmd("test -f $filePath && echo true || echo false").exec()
                .takeIf { it.isSuccess }?.out?.firstOrNull() == "true"
    }.getOrElse {
        Log.e(TAG, "testFile: ${it.message}", it)
        false
    }

    fun setPermissions(permission: Int, filePath: String) {
        runCatching {
            Shell.cmd("chmod $permission $filePath").exec()
        }.onFailure {
            Log.e(TAG, "setPermissions: ${it.message}", it)
        }
    }

    fun readFile(filePath: String): String = runCatching {
        Shell.cmd("cat $filePath").exec().takeIf { it.isSuccess }?.out?.joinToString("\n")?.trim().orEmpty()
    }.getOrElse {
        Log.e(TAG, "readFile: ${it.message}", it)
        ""
    }

    fun <T : Any> writeFile(filePath: String, value: T): Boolean = runCatching {
        Shell.cmd("echo $value > $filePath").exec().isSuccess
    }.getOrElse {
        Log.e(TAG, "writeFile: ${it.message}", it)
        false
    }

    fun getAppVersion(context: Context): String = runCatching {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        "${packageInfo.versionName} (${packageInfo.longVersionCode})"
    }.getOrElse {
        Log.e(TAG, "getAppVersion: ${it.message}", it)
        context.getString(R.string.unknown)
    }

    fun reboot(mode: String = "") {
        val command = if (mode.isBlank()) {
            "reboot"
        } else {
            "reboot $mode"
        }
        Shell.cmd(command).exec()
    }
}
