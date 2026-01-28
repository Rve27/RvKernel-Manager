/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

// Dear programmer:
// When I wrote this code, only god and
// I knew how it worked.
// Now, only god knows it!
//
// Therefore, if you are trying to optimize
// this routine and it fails (most surely),
// please increase this counter as a
// warning for the next person:
//
// total hours wasted here = 254
//
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.battery.BatteryScreen
import com.rve.rvkernelmanager.ui.home.HomeScreen
import com.rve.rvkernelmanager.ui.kernelParameter.KernelParameterScreen
import com.rve.rvkernelmanager.ui.soc.SoCScreen

sealed class NavigationRoute(val route: String, val titleRes: Int, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : NavigationRoute(
        route = "home",
        titleRes = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    )

    object SoC : NavigationRoute(
        route = "soc",
        titleRes = R.string.nav_soc,
        selectedIcon = Icons.Filled.Memory,
        unselectedIcon = Icons.Outlined.Memory,
    )

    object Battery : NavigationRoute(
        route = "battery",
        titleRes = R.string.nav_battery,
        selectedIcon = Icons.Filled.BatteryFull,
        unselectedIcon = Icons.Outlined.Battery0Bar,
    )

    object KernelParameter : NavigationRoute(
        route = "kernel",
        titleRes = R.string.nav_kernel,
        selectedIcon = Icons.Filled.Storage,
        unselectedIcon = Icons.Outlined.Storage,
    )
}

@Composable
fun RvKernelManagerNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable(HomeRoute) {
            HomeScreen(navController = navController)
        }
        composable(SoCRoute) {
            SoCScreen(navController = navController)
        }
        composable(BatteryRoute) {
            BatteryScreen(navController = navController)
        }
        composable(KernelRoute) {
            KernelParameterScreen(navController = navController)
        }
    }
}

const val HomeRoute = "home"
const val SoCRoute = "soc"
const val BatteryRoute = "battery"
const val KernelRoute = "kernel"
