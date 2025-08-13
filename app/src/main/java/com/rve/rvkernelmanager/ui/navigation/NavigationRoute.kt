/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.Battery0Bar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rve.rvkernelmanager.ui.battery.BatteryScreen
import com.rve.rvkernelmanager.ui.home.HomeScreen
import com.rve.rvkernelmanager.ui.kernelParameter.KernelParameterScreen
import com.rve.rvkernelmanager.ui.soc.SoCScreen

sealed class NavigationRoute(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : NavigationRoute(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    )

    object SoC : NavigationRoute(
        route = "soc",
        title = "SoC",
        selectedIcon = Icons.Filled.Memory,
        unselectedIcon = Icons.Outlined.Memory,
    )

    object Battery : NavigationRoute(
        route = "battery",
        title = "Battery",
        selectedIcon = Icons.Filled.BatteryFull,
        unselectedIcon = Icons.Outlined.Battery0Bar,
    )

    object KernelParameter : NavigationRoute(
        route = "kernel",
        title = "Kernel",
        selectedIcon = Icons.Filled.Storage,
        unselectedIcon = Icons.Outlined.Storage,
    )
}

@Composable
fun RvKernelManagerNavHost(navController: NavHostController, lifecycleOwner: LifecycleOwner) {
    NavHost(navController = navController, startDestination = "home") {
        composable(HomeRoute) {
            HomeScreen(lifecycleOwner = lifecycleOwner, navController = navController)
        }
        composable(SoCRoute) {
            SoCScreen(lifecycleOwner = lifecycleOwner, navController = navController)
        }
        composable(BatteryRoute) {
            BatteryScreen(lifecycleOwner = lifecycleOwner, navController = navController)
        }
        composable(KernelRoute) {
            KernelParameterScreen(lifecycleOwner = lifecycleOwner, navController = navController)
        }
    }
}

const val HomeRoute = "home"
const val SoCRoute = "soc"
const val BatteryRoute = "battery"
const val KernelRoute = "kernel"
