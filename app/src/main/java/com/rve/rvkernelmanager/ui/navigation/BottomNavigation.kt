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
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.BatteryStd
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        selectedIcon = Icons.Rounded.Home,
        iconTextId = R.string.tab_home
    ),
    TopLevelDestination(
        route = Route.SoC,
        selectedIcon = Icons.Rounded.Memory,
        iconTextId = R.string.tab_soc
    ),
    TopLevelDestination(
        route = Route.Battery,
        selectedIcon = Icons.Rounded.BatteryStd,
        iconTextId = R.string.tab_battery
    ),
    TopLevelDestination(
        route = Route.Misc,
        selectedIcon = Icons.Rounded.Storage,
        iconTextId = R.string.tab_misc
    )
)

@Composable
fun BottomNavigationBar(
   currentDestination: NavDestination?,
   navigateToTopLevelDestination: (TopLevelDestination) -> Unit
) {
   var selectedItem by remember { mutableIntStateOf(0) }

   NavigationBar(modifier = Modifier.fillMaxWidth()) {
       TOP_LEVEL_DESTINATIONS.forEachIndexed { index, rvKernelManagerDestination ->
           NavigationBarItem(
               selected = currentDestination.hasRoute(rvKernelManagerDestination),
               onClick = {
                   selectedItem = index
                   navigateToTopLevelDestination(rvKernelManagerDestination)
               },
               icon = {
                   Icon(
                       imageVector = rvKernelManagerDestination.selectedIcon,
                       contentDescription = stringResource(id = rvKernelManagerDestination.iconTextId)
                   )
               },
               label = { Text(stringResource(id = rvKernelManagerDestination.iconTextId)) },
               alwaysShowLabel = false
           )
       }
   }
}

fun NavDestination?.hasRoute(destination: TopLevelDestination): Boolean =
    this?.hasRoute(destination.route::class) ?: false
