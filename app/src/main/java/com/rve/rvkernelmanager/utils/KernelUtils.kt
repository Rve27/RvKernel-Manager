/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.utils

import android.system.Os
import com.rve.rvkernelmanager.utils.Utils
import com.topjohnwu.superuser.Shell

object KernelUtils {
    const val FullKernelVersion = "/proc/version"

    const val SchedAutoGroup = "/proc/sys/kernel/sched_autogroup_enabled"
    const val Printk = "/proc/sys/kernel/printk"

    const val SchedUtilClampMax = "/proc/sys/kernel/sched_util_clamp_max"
    const val SchedUtilClampMin = "/proc/sys/kernel/sched_util_clamp_min"
    const val SchedUtilClampMinRtDefault = "/proc/sys/kernel/sched_util_clamp_min_rt_default"

    const val Zram = "/dev/block/zram0"
    const val ZramReset = "/sys/block/zram0/reset"
    const val ZramSize = "/sys/block/zram0/disksize"
    const val ZramCompAlgorithm = "/sys/block/zram0/comp_algorithm"
    const val Swappiness = "/proc/sys/vm/swappiness"
    const val DirtyRatio = "/proc/sys/vm/dirty_ratio"

    const val TcpCongestionAlgorithm = "/proc/sys/net/ipv4/tcp_congestion_control"
    const val TcpAvailableCongestionAlgorithm = "/proc/sys/net/ipv4/tcp_available_congestion_control"

    const val WireGuard = "/sys/module/wireguard/version"

    fun getKernelVersion(): String {
        return Os.uname().release
    }

    fun getFullKernelVersion(): String {
        return Utils.readFile(FullKernelVersion)
    }

    fun getZramSize(): String {
        val sizeInBytes = Utils.readFile(ZramSize).toLongOrNull() ?: 0L
        val sizeInGb = sizeInBytes / 1073741824.0
        return if (sizeInGb == 0.0) "Unknown" else "${sizeInGb.toInt()} GB"
    }

    fun getZramCompAlgorithm(): String {
        val algorithms = Utils.readFile(ZramCompAlgorithm)
        return if (algorithms.isNotEmpty()) {
            val regex = "\\[([^\\]]+)\\]".toRegex()
            val match = regex.find(algorithms)
            match?.groupValues?.get(1) ?: "Unknown"
        } else {
            "Unknown"
        }
    }

    fun getAvailableZramCompAlgorithms(): List<String> {
        val algorithms = Utils.readFile(ZramCompAlgorithm)
        return if (algorithms.isNotEmpty()) {
            algorithms.replace("[", "").replace("]", "")
                .split("\\s+".toRegex())
                .filter { it.isNotBlank() }
        } else {
            emptyList()
        }
    }

    fun swapoffZram() {
        Shell.cmd("swapoff $Zram").exec()
    }

    fun swaponZram() {
        Shell.cmd("swapon $Zram").exec()
    }

    fun mkswapZram() {
        Shell.cmd("mkswap $Zram").exec()
    }

    fun resetZram() {
        Shell.cmd("echo 1 > $ZramReset").exec()
    }

    fun setZramCompAlgorithm(algorithm: String) {
        Shell.cmd("echo $algorithm > $ZramCompAlgorithm").exec()
    }

    fun getTcpCongestionAlgorithm(): String {
        return Utils.readFile(TcpCongestionAlgorithm).trim().takeIf { it.isNotEmpty() } ?: "Unknown"
    }

    fun getAvailableTcpCongestionAlgorithm(): List<String> {
        val algorithms = Utils.readFile(TcpAvailableCongestionAlgorithm)
        return if (algorithms.isNotEmpty()) {
            algorithms.trim()
                .split("\\s+".toRegex())
                .filter { it.isNotBlank() }
        } else {
            emptyList()
        }
    }

    fun setTcpCongestionAlgorithm(algorithm: String) {
        Utils.writeFile(TcpCongestionAlgorithm, algorithm)
    }

    fun getWireGuardVersion(): String {
        return Utils.readFile(WireGuard)
    }
}
