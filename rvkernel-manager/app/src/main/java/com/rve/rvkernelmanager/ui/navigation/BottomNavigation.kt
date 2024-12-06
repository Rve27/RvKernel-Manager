package com.rve.rvkernelmanager.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import kotlinx.serialization.Serializable
import com.rve.rvkernelmanager.R

sealed interface Route {
    @Serializable data object Home : Route
    @Serializable data object SoC : Route
    @Serializable data object Battery : Route
    @Serializable data object Misc : Route
}

data class TopLevelDestination(
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

class BottomNavigationActions(private val navController: NavHostController) {

    fun navigateTo(destination: TopLevelDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(
        route = Route.Home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Filled.Home,
        iconTextId = R.string.tab_home
    ),
    TopLevelDestination(
        route = Route.SoC,
        selectedIcon = Icons.Filled.Memory,
        unselectedIcon = Icons.Filled.Memory,
        iconTextId = R.string.tab_soc
    ),
    TopLevelDestination(
        route = Route.Battery,
        selectedIcon = Icons.Filled.BatteryChargingFull,
        unselectedIcon = Icons.Filled.BatteryChargingFull,
        iconTextId = R.string.tab_battery
    ),
    TopLevelDestination(
        route = Route.Misc,
        selectedIcon = Icons.Filled.Storage,
        unselectedIcon = Icons.Filled.Storage,
        iconTextId = R.string.tab_storage
    )
)

@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    navigateToTopLevelDestination: (TopLevelDestination) -> Unit
) {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        TOP_LEVEL_DESTINATIONS.forEach { rvKernelManagerDestination ->
            NavigationBarItem(
                selected = currentDestination.hasRoute(rvKernelManagerDestination),
                onClick = { navigateToTopLevelDestination(rvKernelManagerDestination) },
                icon = {
                    Icon(
                        imageVector = rvKernelManagerDestination.selectedIcon,
                        contentDescription = stringResource(id = rvKernelManagerDestination.iconTextId)
                    )
                }
            )
        }
    }
}

fun NavDestination?.hasRoute(destination: TopLevelDestination): Boolean =
    this?.hasRoute(destination.route::class) ?: false
