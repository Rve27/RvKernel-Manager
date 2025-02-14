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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rve.rvkernelmanager.ui.navigation.*
import com.rve.rvkernelmanager.ui.screen.*
import com.rve.rvkernelmanager.ui.theme.RvKernelManagerTheme
import com.rve.rvkernelmanager.utils.RootUtils
import com.topjohnwu.superuser.Shell

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        var isShellReady by mutableStateOf(false)
        var showNoRootDialog by mutableStateOf(false)

        Shell.getShell { shell ->
            if (RootUtils.isRooted()) {
                isShellReady = true
            } else {
                showNoRootDialog = true
            }
        }

        setContent {
            RvKernelManagerTheme {
                when {
                    showNoRootDialog -> {
                        RootUtils.NoRootDialog { finish() }
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

    companion object {
        init {
	    @Suppress("DEPRECATION")
            if (Shell.getCachedShell() == null) {
                Shell.setDefaultBuilder(
                    Shell.Builder.create()
                        .setFlags(Shell.FLAG_MOUNT_MASTER)
                        .setFlags(Shell.FLAG_REDIRECT_STDERR)
                        .setTimeout(20)
                )
            }
        }
    }
}

@Composable
private fun RvKernelManagerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.Home,
    ) {
        composable<Route.Home> {
            HomeScreen(lifecycleOwner = lifecycleOwner)
        }
        composable<Route.Battery> {
            BatteryScreen(lifecycleOwner = lifecycleOwner)
        }
        composable<Route.SoC> {
            SoCScreen(lifecycleOwner = lifecycleOwner)
        }
        composable<Route.Misc> {
            MiscScreen(lifecycleOwner = lifecycleOwner)
        }
    }
}
