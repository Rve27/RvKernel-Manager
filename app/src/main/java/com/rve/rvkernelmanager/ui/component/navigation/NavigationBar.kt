/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.ui.component.navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.Battery0Bar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rve.rvkernelmanager.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.SoC,
        BottomNavItem.Battery,
        BottomNavItem.KernelParameter,
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (currentDestination?.route == item.route) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(id = item.title),
                    )
                },
                label = { Text(stringResource(id = item.title)) },
                selected = currentDestination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = false,
            )
        }
    }
}

sealed class BottomNavItem(val route: String, val title: Int, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : BottomNavItem(
        route = "home",
        title = R.string.home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    )

    object SoC : BottomNavItem(
        route = "soc",
        title = R.string.soc,
        selectedIcon = Icons.Filled.Memory,
        unselectedIcon = Icons.Outlined.Memory,
    )

    object Battery : BottomNavItem(
        route = "battery",
        title = R.string.battery,
        selectedIcon = Icons.Filled.BatteryFull,
        unselectedIcon = Icons.Outlined.Battery0Bar,
    )

    object KernelParameter : BottomNavItem(
        route = "kernel",
        title = R.string.kernel_parameter,
        selectedIcon = Icons.Filled.Storage,
        unselectedIcon = Icons.Outlined.Storage,
    )
}
