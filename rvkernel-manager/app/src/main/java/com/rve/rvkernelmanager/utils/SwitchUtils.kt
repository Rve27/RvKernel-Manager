package com.rve.rvkernelmanager.utils

import com.topjohnwu.superuser.Shell

private const val FAST_CHARGING_PATH = "/sys/kernel/fast_charge/force_fast_charge"

fun writeFastCharging(value: String): Boolean = 
    Shell.cmd("echo $value > $FAST_CHARGING_PATH").exec().isSuccess

fun readFastCharging(): String = 
    Shell.cmd("cat $FAST_CHARGING_PATH").exec().let { result ->
        if (result.isSuccess && result.out.isNotEmpty()) result.out[0].trim() else "0"
    }
