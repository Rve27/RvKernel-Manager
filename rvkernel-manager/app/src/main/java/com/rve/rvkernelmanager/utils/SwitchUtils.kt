package com.rve.rvkernelmanager.utils

import java.io.File
import com.topjohnwu.superuser.Shell

const val FAST_CHARGING_PATH = "/sys/kernel/fast_charge/force_fast_charge"

fun hasFastCharging(): Boolean {
    val file = File(FAST_CHARGING_PATH)
    return if (file.exists()) {
        true
    } else {
        Shell.cmd("test -f $FAST_CHARGING_PATH && echo true || echo false")
            .exec()
            .out[0] == "true"
    }
}

fun writeFastCharging(value: String): Boolean = 
    Shell.cmd("echo $value > $FAST_CHARGING_PATH").exec().isSuccess

fun readFastCharging(): String? =
    Shell.cmd("cat $FAST_CHARGING_PATH").exec().let { result ->
        if (result.isSuccess && result.out.isNotEmpty()) {
            result.out[0].trim()
        } else {
            null
        }
    }
