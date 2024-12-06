package com.rve.rvkernelmanager.utils

import android.system.Os

fun getKernelVersion(): String {
    return try {
        Os.uname().release
    } catch (e: Exception) {
        e.printStackTrace()
        "Unable to get kernel version"
    }
}
