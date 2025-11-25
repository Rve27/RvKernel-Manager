/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.utils

import android.annotation.SuppressLint
import android.system.Os
import com.topjohnwu.superuser.Shell

object KernelUtils {
    @SuppressLint("SdCardPath")
    const val KERNEL_PROFILE_PATH = "/sdcard/RvKernel-Manager/kernel-profile"
    const val KERNEL_PROFILE_CURRENT = "$KERNEL_PROFILE_PATH/current_profile"
    const val KERNEL_PROFILE_POWERSAVE = "$KERNEL_PROFILE_PATH/powersave.sh"
    const val KERNEL_PROFILE_BALANCE = "$KERNEL_PROFILE_PATH/balance.sh"
    const val KERNEL_PROFILE_PERFORMANCE = "$KERNEL_PROFILE_PATH/performance.sh"

    const val FULL_KERNEL_VERSION = "/proc/version"

    const val SCHED_AUTO_GROUP = "/proc/sys/kernel/sched_autogroup_enabled"
    const val PRINTK = "/proc/sys/kernel/printk"

    const val SCHED_UTIL_CLAMP_MAX = "/proc/sys/kernel/sched_util_clamp_max"
    const val SCHED_UTIL_CLAMP_MIN = "/proc/sys/kernel/sched_util_clamp_min"
    const val SCHED_UTIL_CLAMP_MIN_RT_DEFAULT = "/proc/sys/kernel/sched_util_clamp_min_rt_default"

    const val ZRAM = "/dev/block/zram0"
    const val ZRAM_RESET = "/sys/block/zram0/reset"
    const val ZRAM_SIZE = "/sys/block/zram0/disksize"
    const val ZRAM_COMP_ALGORITHM = "/sys/block/zram0/comp_algorithm"
    const val SWAPPINESS = "/proc/sys/vm/swappiness"
    const val DIRTY_RATIO = "/proc/sys/vm/dirty_ratio"

    const val TCP_CONGESTION_ALGORITHM = "/proc/sys/net/ipv4/tcp_congestion_control"
    const val TCP_AVAILABLE_CONGESTION_ALGORITHM = "/proc/sys/net/ipv4/tcp_available_congestion_control"

    const val WIREGUARD_VERSION = "/sys/module/wireguard/version"

    const val BORE = "/proc/sys/kernel/sched_bore"
    const val BURST_SMOOTHNESS_LONG = "/proc/sys/kernel/sched_burst_smoothness_long"
    const val BURST_SMOOTHNESS_SHORT = "/proc/sys/kernel/sched_burst_smoothness_short"
    const val BURST_FORK_ATAVISTIC = "/proc/sys/kernel/sched_burst_fork_atavistic"
    const val BURST_PENALTY_OFFSET = "/proc/sys/kernel/sched_burst_penalty_offset"
    const val BURST_PENALTY_SCALE = "/proc/sys/kernel/sched_burst_penalty_scale"
    const val BURST_CACHE_LIFETIME = "/proc/sys/kernel/sched_burst_cache_lifetime"

    fun getKernelProfile(): Int {
        return Utils.readFile(KERNEL_PROFILE_CURRENT).toInt()
    }

    fun setKernelProfile(profile: Int) {
        when (profile) {
            0 -> {
                Shell.cmd("su -c sh $KERNEL_PROFILE_POWERSAVE").exec()
                Utils.writeFile(KERNEL_PROFILE_CURRENT, "0")
            }
            1 -> {
                Shell.cmd("su -c sh $KERNEL_PROFILE_BALANCE").exec()
                Utils.writeFile(KERNEL_PROFILE_CURRENT, "1")
            }
            2 -> {
                Shell.cmd("su -c sh $KERNEL_PROFILE_PERFORMANCE").exec()
                Utils.writeFile(KERNEL_PROFILE_CURRENT, "2")
            }
        }
    }

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
        return Utils.readFile(WIREGUARD_VERSION)
    }

    fun mkdirKernelProfilePath() {
        Shell.cmd("mkdir -p $KERNEL_PROFILE_PATH").exec()
    }

    fun createCurrentKernelProfileNode() {
        Shell.cmd("echo 1 > $KERNEL_PROFILE_CURRENT")
    }
}
