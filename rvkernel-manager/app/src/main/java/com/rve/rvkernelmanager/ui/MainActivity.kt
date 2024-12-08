package com.rve.rvkernelmanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rve.rvkernelmanager.ui.navigation.Route
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationBar
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationActions
import com.rve.rvkernelmanager.ui.screen.HomeScreen
import com.rve.rvkernelmanager.ui.screen.BatteryScreen
import com.rve.rvkernelmanager.ui.theme.RvKernelManagerTheme
import com.rve.rvkernelmanager.utils.NoRootDialog
import com.topjohnwu.superuser.Shell

class MainActivity : ComponentActivity() {
    init {
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_MOUNT_MASTER)
                .setTimeout(10)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        var isShellReady by mutableStateOf(false)
        var showNoRootDialog by mutableStateOf(false)

	Shell.getShell { shell ->
            if (shell.isRoot) {
                isShellReady = true
            } else {
                showNoRootDialog = true
            }
        }

        setContent {
            RvKernelManagerTheme {
                when {
                    showNoRootDialog -> {
                        NoRootDialog { finish() }
                    }
                    isShellReady -> {
                        RvKernelManagerTheme {
                            val navController = rememberNavController()
                            val navigationActions = remember(navController) {
                                BottomNavigationActions(navController)
                            }
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
        
                            Scaffold(
                                bottomBar = { 
                                    BottomNavigationBar(
                                        currentDestination = currentDestination,
                                        navigateToTopLevelDestination = navigationActions::navigateTo
                                    )
                                },
                                contentWindowInsets = WindowInsets(0, 0, 0, 0)
                            ) { innerPadding ->
                                RvKernelManagerNavHost(
                                    modifier = Modifier.padding(innerPadding),
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RvKernelManagerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.Home,
    ) {
        composable<Route.Home> {
            HomeScreen()
        }
	composable<Route.Battery> {
	    BatteryScreen()
	}
    }
}

