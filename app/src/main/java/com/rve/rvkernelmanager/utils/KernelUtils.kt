/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.utils

import android.system.Os
import com.rve.rvkernelmanager.utils.Utils
import com.topjohnwu.superuser.Shell

object KernelUtils {
    const val FULL_KERNEL_VERSION = "/proc/version"

    const val SCHED_AUTOGROUP = "/proc/sys/kernel/sched_autogroup_enabled"
    const val PRINTK = "/proc/sys/kernel/printk"

    const val SCHED_UTIL_CLAMP_MAX = "/proc/sys/kernel/sched_util_clamp_max"
    const val SCHED_UTIL_CLAMP_MIN = "/proc/sys/kernel/sched_util_clamp_min"
    const val SCHED_UTIL_CLAMP_MIN_RT_DEFAULT = "/proc/sys/kernel/sched_util_clamp_min_rt_default"

    const val ZRAM = "/dev/block/zram0"
    const val ZRAM_RESET = "/sys/block/zram0/reset"
    const val ZRAM_SIZE = "/sys/block/zram0/disksize"
    const val ZRAM_COMP_ALGORITHM = "/sys/block/zram0/comp_algorithm"
    const val SWAPPINESS = "/proc/sys/vm/swappiness"

    const val TCP_CONGESTION_ALGORITHM = "/proc/sys/net/ipv4/tcp_congestion_control"
    const val TCP_AVAILABLE_CONGESTION_ALGORITHM = "/proc/sys/net/ipv4/tcp_available_congestion_control"

    const val WireGuard = "/sys/module/wireguard/version"

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

    fun getZramCompAlgorithm(): String {
        val algorithms = Utils.readFile(ZRAM_COMP_ALGORITHM)
        return if (algorithms.isNotEmpty()) {
            val regex = "\\[([^\\]]+)\\]".toRegex()
            val match = regex.find(algorithms)
            match?.groupValues?.get(1) ?: "Unknown"
        } else {
            "Unknown"
        }
    }

    fun getAvailableZramCompAlgorithms(): List<String> {
        val algorithms = Utils.readFile(ZRAM_COMP_ALGORITHM)
        return if (algorithms.isNotEmpty()) {
            algorithms.replace("[", "").replace("]", "")
                .split("\\s+".toRegex())
                .filter { it.isNotBlank() }
        } else {
            emptyList()
        }
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

    fun setZramCompAlgorithm(algorithm: String) {
        Shell.cmd("echo $algorithm > $ZRAM_COMP_ALGORITHM").exec()
    }

    fun getTcpCongestionAlgorithm(): String {
        return Utils.readFile(TCP_CONGESTION_ALGORITHM).trim().takeIf { it.isNotEmpty() } ?: "Unknown"
    }

    fun getAvailableTcpCongestionAlgorithm(): List<String> {
        val algorithms = Utils.readFile(TCP_AVAILABLE_CONGESTION_ALGORITHM)
        return if (algorithms.isNotEmpty()) {
            algorithms.trim()
                .split("\\s+".toRegex())
                .filter { it.isNotBlank() }
        } else {
            emptyList()
        }
    }

    fun setTcpCongestionAlgorithm(algorithm: String) {
        Utils.writeFile(TCP_CONGESTION_ALGORITHM, algorithm)
    }

    fun getWireGuardVersion(): String {
        return Utils.readFile(WireGuard)
    }
}
