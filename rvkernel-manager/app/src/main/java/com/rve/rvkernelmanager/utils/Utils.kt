package com.rve.rvkernelmanager.utils

import android.system.Os
import android.os.Build
import android.content.Context
import android.app.ActivityManager
import android.util.Log
import kotlin.math.ceil
import com.topjohnwu.superuser.Shell

object Utils {

    const val FULL_KERNEL_VERSION_PATH = "/proc/version"
    const val CPU_INFO_PATH = "/proc/cpuinfo"
    const val GPU_MODEL_PATH = "/sys/class/kgsl/kgsl-3d0/gpu_model"
    
    fun getDeviceCodename(): String {
        return Build.DEVICE
    }
    
    fun getAndroidVersion(): String {
        return Build.VERSION.RELEASE
    }
    
    fun getCPUInfo(): String {
        return try {
            val hardwareResult = Shell.cmd("cat $CPU_INFO_PATH | grep 'Hardware' | head -n 1").exec()
    
            if (hardwareResult.isSuccess) {
                hardwareResult.out.firstOrNull()?.replace("Hardware\t: ", "")?.trim() ?: "Unknown"
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            Log.e("getCPUInfo", "Exception during command execution", e)
            "Unknown"
        }
    }
    
    fun getExtendCPUInfo(): String {
        return try {
            val hardwareResult = Shell.cmd("cat $CPU_INFO_PATH | grep 'Hardware' | head -n 1").exec()
            val coresResult = Shell.cmd("cat $CPU_INFO_PATH | grep 'processor' | wc -l").exec()
            val archResult = Shell.cmd("cat $CPU_INFO_PATH | grep 'Processor' | head -n 1").exec()
    
            val hardware = if (hardwareResult.isSuccess) {
                hardwareResult.out.firstOrNull()?.replace("Hardware\t: ", "")?.trim() ?: "Unknown"
            } else "Unknown"
    
            val cores = if (coresResult.isSuccess) {
                coresResult.out.firstOrNull()?.trim() ?: "0"
            } else "0"
    
            val arch = if (archResult.isSuccess) {
                val processor = archResult.out.firstOrNull()?.trim() ?: ""
                if (processor.contains("AArch64")) "AArch64" else "Unknown"
            } else "Unknown"
    
            "$hardware\n$cores Cores ($arch)"
        } catch (e: Exception) {
            Log.e("getExtendCPUInfo", "Exception during command execution", e)
            "Exception during command execution"
        }
    }
    
    fun getTotalRam(context: Context): String {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
    
        val totalMem = memoryInfo.totalMem / (1024.0 * 1024 * 1024)
        val totalRam = ceil(totalMem).toInt()
    
        return "$totalRam GB"
    }
    
    fun getSystemProperty(key: String): String {
        return try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getMethod = systemPropertiesClass.getMethod("get", String::class.java)
            getMethod.invoke(null, key) as String
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    
    fun getRvOSVersion(): String? {
        val rvosVersion = getSystemProperty("ro.rvos.version")
        val rvosBuildType = getSystemProperty("ro.rvos.build.type")
        
        return if (rvosVersion.isNotEmpty() || rvosBuildType.isNotEmpty()) {
            "$rvosVersion${if (rvosBuildType.isNotEmpty()) " $rvosBuildType" else ""}"
        } else null
    }
    
    fun getSomethingOSVersion(): String? {
        val somethingVersion = getSystemProperty("ro.somethingos.version")
        return if (somethingVersion.isNotEmpty()) somethingVersion else null
    }
    
    fun getKernelVersion(): String {
        return try {
            Os.uname().release
        } catch (e: Exception) {
            e.printStackTrace()
            "Unable to get kernel version"
        }
    }
    
    fun getGPUModel(): String {
        return try {
            val result = Shell.cmd("cat $GPU_MODEL_PATH").exec()
            if (result.isSuccess) {
                val gpuModel = result.out.firstOrNull()?.trim() ?: "Unknown"
                val formattedModel = gpuModel
                    .replace("Adreno", "Adreno (TM) ")
                    .replace(Regex("v\\d+"), "")
                formattedModel
            } else {
                Log.e("getGPUModel", "Command execution failed: ${result.err}")
                "Unknown"
            }
        } catch (e: Exception) {
            Log.e("getGPUModel", "Error reading GPU model: ${e.message}", e)
            "Unknown"
        }
    }
}
