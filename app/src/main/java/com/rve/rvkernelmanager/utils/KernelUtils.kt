/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package com.rve.rvkernelmanager.utils

import android.system.Os

import com.topjohnwu.superuser.Shell

import com.rve.rvkernelmanager.utils.Utils

object KernelUtils {
    const val FULL_KERNEL_VERSION = "/proc/version"
    const val SWAPPINESS = "/proc/sys/vm/swappiness"
    const val SCHED_AUTOGROUP = "/proc/sys/kernel/sched_autogroup_enabled"
    const val PRINTK = "/proc/sys/kernel/printk"
    const val ZRAM = "/dev/block/zram0"
    const val ZRAM_RESET = "/sys/block/zram0/reset"
    const val ZRAM_SIZE = "/sys/block/zram0/disksize"

    fun getKernelVersion(): String {
        return Os.uname().release
    }

    fun getFullKernelVersion(): String {
	return Utils.readFile(FULL_KERNEL_VERSION)
    }

    fun getZramSize(): String {
        val sizeInBytes = Utils.readFile(ZRAM_SIZE).toLongOrNull() ?: 0L
        val sizeInGb = sizeInBytes / 1073741824.0
        return if (sizeInGb == 0.0) "Unknown" else "${sizeInGb.toInt()} GB"
    }

    fun swapoffZram() {
	Shell.cmd("swapoff $ZRAM").exec()
    }

    fun swaponZram() {
	Shell.cmd("swapon $ZRAM").exec()
    }

    fun mkswapZram() {
	Shell.cmd("mkswap $ZRAM").exec()
    }

    fun resetZram() {
	Shell.cmd("echo 1 > $ZRAM_RESET").exec()
    }
}
