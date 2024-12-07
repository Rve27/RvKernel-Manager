package com.rve.rvkernelmanager.utils

val fastCharging = "/sys/kernel/fast_charge/force_fast_charge"

fun writeFastCharging(value: String): Boolean {
    return try {
        val process = Runtime.getRuntime().exec("su")
        val outputStream = process.outputStream
        val command = "echo $value > $fastCharging\n"
        outputStream.write(command.toByteArray())
        outputStream.flush()
        outputStream.close()

        process.waitFor()
        process.exitValue() == 0
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun readFastCharging(): String {
    return try {
        val process = Runtime.getRuntime().exec("su")
        val outputStream = process.outputStream
        val inputStream = process.inputStream

        val command = "cat $fastCharging\n"
        outputStream.write(command.toByteArray())
        outputStream.flush()
        outputStream.close()

        val result = inputStream.bufferedReader().readText().trim()
        process.waitFor()
        if (process.exitValue() == 0) result else "0"
    } catch (e: Exception) {
        e.printStackTrace()
        "0"
    }
}
