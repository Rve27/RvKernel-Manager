package com.rve.rvkernelmanager.utils

object MiscUtils {
    const val SWAPPINESS = "/proc/sys/vm/swappiness"
    const val SCHED_AUTOGROUP = "/proc/sys/kernel/sched_autogroup_enabled"
    const val THERMAL_SCONFIG = "/sys/class/thermal/thermal_message/sconfig"
    const val PRINTK = "/proc/sys/kernel/printk"
}
