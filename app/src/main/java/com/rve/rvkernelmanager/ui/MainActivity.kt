/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rve.rvkernelmanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rve.rvkernelmanager.ui.navigation.RvKernelManagerNavHost
import com.rve.rvkernelmanager.ui.theme.RvKernelManagerTheme
import com.topjohnwu.superuser.Shell
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
  
    private var isRoot = false
    
    private var showRootDialog by mutableStateOf(false)
    
    private var isChecking by mutableStateOf(true)

    private val checkRoot = Runnable {
        Shell.getShell { shell ->
            isRoot = shell.isRoot
            if (!isRoot) {
                showRootDialog = true
            }
          
            isChecking = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen: SplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { isChecking }
        
        enableEdgeToEdge()
        Thread(checkRoot).start()

        setContent {
            RvKernelManagerTheme {
                RvKernelManagerApp(showRootDialog = showRootDialog)
            }
        }
    }

    companion object {
        init {
        
            @Suppress("DEPRECATION")
            if (Shell.getCachedShell() == null) {
                Shell.setDefaultBuilder(
                    Shell.Builder.create()
                        .setFlags(Shell.FLAG_MOUNT_MASTER or Shell.FLAG_REDIRECT_STDERR)
                        .setTimeout(20),
                )
            }
        }
    }
}

@Composable
fun RvKernelManagerApp(showRootDialog: Boolean = false) {
    
    if (showRootDialog) {
        AlertDialog(
            onDismissRequest = { 
                
            },
            title = { Text("Root Access Missing") },
            text = {
                Text(
                    text = "RvKernel Manager requires root access to function properly.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        exitProcess(0)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Exit")
                }
            },
        )
    }
    
    // Content utama tetap dirender di belakang dialog
    Surface {
        RvKernelManagerNavHost()
    }
}
