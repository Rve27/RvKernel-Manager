/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rve.rvkernelmanager.ui.activity

import android.os.Bundle

import androidx.activity.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.navigation.compose.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import com.rve.rvkernelmanager.ui.navigation.*
import com.rve.rvkernelmanager.ui.screen.*
import com.rve.rvkernelmanager.ui.theme.RvKernelManagerTheme

import com.topjohnwu.superuser.Shell

class MainActivity : ComponentActivity() {
    private var isRoot = false
    private var showRootDialog by mutableStateOf(false)
    
    private val checkRoot = Runnable {
        Shell.getShell { shell ->
            isRoot = shell.isRoot
            if (!isRoot) {
                showRootDialog = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
	val splashScreen: SplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

	splashScreen.setKeepOnScreenCondition { !isRoot }
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
                        .setTimeout(20)
                )
            }
        }
    }
}

@Composable
fun RvKernelManagerApp(showRootDialog: Boolean = false) {
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current

    if (showRootDialog) {
	AlertDialog(
	    onDismissRequest = {},
	    text = {
		Text(
		    text = "RvKernel Manager requires root access!",
		    style = MaterialTheme.typography.bodyLarge
		)
	    },
	    confirmButton = {
		TextButton(
		    onClick = {
			System.exit(0)
		    },
		    shapes = ButtonDefaults.shapes()
		) {
		    Text("Exit")
		}
	    }
	)
    }

    Scaffold {
	NavHost(
            navController = navController,
            startDestination = "home",
	) {
            composable("home") {
                HomeScreen(lifecycleOwner = lifecycleOwner, navController = navController)
            }
            composable("soc") {
                SoCScreen(lifecycleOwner = lifecycleOwner, navController = navController)
            }
            composable("battery") {
                BatteryScreen(lifecycleOwner = lifecycleOwner, navController = navController)
            }
            composable("misc") {
                MiscScreen(lifecycleOwner = lifecycleOwner, navController = navController)
            }
	}
    }
}
