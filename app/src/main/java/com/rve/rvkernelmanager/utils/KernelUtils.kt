/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package com.rve.rvkernelmanager.utils

import android.system.Os
import android.util.Log

import com.rve.rvkernelmanager.utils.Utils

object KernelUtils {
    const val FULL_KERNEL_VERSION = "/proc/version"
    const val SWAPPINESS = "/proc/sys/vm/swappiness"
    const val SCHED_AUTOGROUP = "/proc/sys/kernel/sched_autogroup_enabled"
    const val PRINTK = "/proc/sys/kernel/printk"

    fun getKernelVersion(): String = runCatching {
        Os.uname().release
    }.getOrElse {
        Log.e("getKernelVersion", "Error: ${it.message}", it)
        "Unable to get kernel version"
    }

    fun getFullKernelVersion(): String {
	return Utils.readFile(FULL_KERNEL_VERSION)
    }
}
