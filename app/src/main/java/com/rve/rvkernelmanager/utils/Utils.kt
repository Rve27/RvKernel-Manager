package com.rve.rvkernelmanager.utils

import java.io.File
import android.system.Os
import android.os.Build
import android.content.Context
import android.app.ActivityManager
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.math.ceil
import com.topjohnwu.superuser.Shell

object Utils {

    const val FULL_KERNEL_VERSION = "/proc/version"
    const val CPU_INFO = "/proc/cpuinfo"
    const val GPU_MODEL = "/sys/class/kgsl/kgsl-3d0/gpu_model"
    
    fun getDeviceCodename(): String {
        return Build.DEVICE
    }
    
    fun getAndroidVersion(): String {
        return Build.VERSION.RELEASE
    }
    
    fun getCPUInfo(): String {
        return try {
            val hardwareResult = Shell.cmd("cat $CPU_INFO | grep 'Hardware' | head -n 1").exec()
    
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
            val hardwareResult = Shell.cmd("cat $CPU_INFO | grep 'Hardware' | head -n 1").exec()
            val coresResult = Shell.cmd("cat $CPU_INFO | grep 'processor' | wc -l").exec()
            val archResult = Shell.cmd("cat $CPU_INFO | grep 'Processor' | head -n 1").exec()
    
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
            val result = Shell.cmd("cat $GPU_MODEL").exec()
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

        fun testFile(filePath: String): Boolean {
        return if (File(filePath).exists()) {
            true
        } else {
            Shell.cmd("test -f $filePath && echo true || echo false")
                .exec()
                .out[0] == "true"
        }
    }
    
    fun setPermissions(permission: Int, filePath: String) {
        Shell.cmd("chmod $permission $filePath").exec()
    }
    
    fun readFile(filePath: String): String {
        return try {
            val result = Shell.cmd("cat $filePath").exec()
            if (result.isSuccess) {
                result.out.joinToString("\n").trim()
            } else {
                Log.e("ReadFile", "Failed to read file at $filePath: ${result.err}")
                ""
            }
        } catch (e: Exception) {
            Log.e("ReadFile", "Error executing shell command for $filePath: ${e.message}", e)
            ""
        }
    }
    
    fun writeFile(filePath: String, value: String): Boolean {
        return try {
            val command = "echo $value > $filePath"
            val result = Shell.cmd(command).exec()
            if (result.isSuccess) true else {
                Log.e("WriteFile", "Failed to write file at $filePath: ${result.err}")
                false
            }
        } catch (e: Exception) {
            Log.e("WriteFile", "Error executing shell command for $filePath: ${e.message}", e)
            false
        }
    }

    @Composable
    fun CustomItem(
        title: String? = null,
        body: String? = null,
        onClick: (() -> Unit)? = null,
        animateContent: Boolean = false,
        titleLarge: Boolean = false,
        icon: Any? = null
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                when (icon) {
                    is ImageVector -> Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 20.dp)
                    )
                    is Painter -> Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 20.dp)
                    )
                }
            }
            Column {
                if (title != null) {
                    Text(
                        text = title,
                        style = if (titleLarge) {
                            MaterialTheme.typography.titleLarge
                        } else {
                            MaterialTheme.typography.titleMedium
                        }
                    )
                }
                if (body != null) {
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .clickable(enabled = onClick != null) { onClick?.invoke() }
                            .then(if (animateContent) Modifier.animateContentSize() else Modifier)
                            .padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
