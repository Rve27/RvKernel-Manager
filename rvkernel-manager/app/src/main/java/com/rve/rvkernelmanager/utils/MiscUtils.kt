package com.rve.rvkernelmanager.utils

object MiscUtils {

    const val SWAPPINESS_PATH = "/proc/sys/vm/swappiness"
    const val SCHED_AUTOGROUP_PATH = "/proc/sys/kernel/sched_autogroup_enabled"
    const val THERMAL_SCONFIG_PATH = "/sys/class/thermal/thermal_message/sconfig"
    const val PRINTK_PATH = "/proc/sys/kernel/printk"
}
