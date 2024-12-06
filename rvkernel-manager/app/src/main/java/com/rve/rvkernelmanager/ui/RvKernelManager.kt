package com.rve.rvkernelmanager.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rve.rvkernelmanager.ui.navigation.Route
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationBar
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationActions
import com.rve.rvkernelmanager.ui.screen.HomeScreen

@Composable
fun RvKernelManager() {
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        BottomNavigationActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                RvKernelManagerNavHost(navController = navController, modifier = Modifier.weight(1f))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    navigateToTopLevelDestination = navigationActions::navigateTo
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
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.Home,
    ) {
        composable<Route.Home> {
            HomeScreen()
        }
    }
}
