package com.rve.rvkernelmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rve.rvkernelmanager.ui.navigation.*
import com.rve.rvkernelmanager.ui.screen.*
import com.rve.rvkernelmanager.ui.theme.RvKernelManagerTheme
import com.rve.rvkernelmanager.utils.RootUtils
import com.topjohnwu.superuser.Shell

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RvKernelManagerTheme {
                RootCheckHandler()
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
fun RootCheckHandler() {
    var isShellReady by remember { mutableStateOf(false) }
    var showNoRootDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Shell.getShell { shell ->
            if (shell.isRoot) {
                isShellReady = true
            } else {
                showNoRootDialog = true
            }
        }
    }

    when {
        showNoRootDialog -> {
            RootUtils.NoRootDialog { System.exit(0) }
        }
        isShellReady -> {
            RvKernelManagerApp()
        }
    }
}

@Composable
fun RvKernelManagerApp() {
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current

    Scaffold(
        bottomBar = { 
            BottomNavigationBar(navController)
        },
	contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
	NavHost(
	    navController = navController,
	    startDestination = "home",
	    modifier = Modifier.padding(innerPadding)
	) {
	    composable("home") {
		HomeScreen(lifecycleOwner = lifecycleOwner)
	    }
	    composable("soc") {
		SoCScreen(lifecycleOwner = lifecycleOwner)
	    }
	    composable("battery") {
		BatteryScreen(lifecycleOwner = lifecycleOwner)
	    }
	    composable("misc") {
		MiscScreen(lifecycleOwner = lifecycleOwner)
	    }
	}
    }
}
