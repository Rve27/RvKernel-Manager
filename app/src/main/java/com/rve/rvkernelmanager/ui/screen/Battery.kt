package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rve.rvkernelmanager.ui.navigation.*
import com.rve.rvkernelmanager.ui.viewmodel.BatteryViewModel
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.component.CustomListItem
import com.rve.rvkernelmanager.ui.component.MonitorListItem
import com.rve.rvkernelmanager.ui.component.SwitchItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryScreen(
    viewModel: BatteryViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner,
    navController: NavController
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val chargingState by viewModel.chargingState.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.initializeBatteryInfo(context)
                Lifecycle.Event.ON_PAUSE -> viewModel.unregisterBatteryListeners(context)
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
	    PinnedTopAppBar(scrollBehavior = scrollBehavior)
	},
	bottomBar = {
	    BottomNavigationBar(navController = navController)
	},
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
	    state = rememberLazyListState()
        ) {
	    item {
		Spacer(Modifier.height(16.dp))
		BatteryMonitorCard(viewModel)
	    }
	    item {
                BatteryInfoCard(viewModel)
	    }
            if (chargingState.hasFastCharging || chargingState.hasBypassCharging) {
		item {
                    ChargingCard(viewModel)
		}
            }
	    item {
                Spacer(Modifier)
	    }
        }
    }
}

@Composable
fun BatteryMonitorCard(viewModel: BatteryViewModel) {
    val batteryInfo by viewModel.batteryInfo.collectAsState()

    Card {
        CustomListItem(
            title = "Battery Monitor",
            titleLarge = true,
	    icon = painterResource(R.drawable.ic_monitor)
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

	Card(
	    elevation = CardDefaults.cardElevation(
		defaultElevation = 8.dp
	    )
	) {
	    Column(
		modifier = Modifier.padding(16.dp)
	    ) {
		MonitorListItem(
                    title = "Level",
                    summary = batteryInfo.level
		)

		Spacer(Modifier.height(8.dp))
		MonitorListItem(
		    title = "Temperature",
		    summary = batteryInfo.temp
		)
	    }
	}
    }
}

@Composable
fun BatteryInfoCard(viewModel: BatteryViewModel) {
    val batteryInfo by viewModel.batteryInfo.collectAsState()

    Card {
        CustomListItem(
            title = "Battery Information",
	    titleLarge = true
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        CustomListItem(
            title = "Technology",
            summary = batteryInfo.tech,
            icon = painterResource(R.drawable.ic_technology),
        )

        CustomListItem(
            title = "Health",
            summary = batteryInfo.health,
            icon = painterResource(R.drawable.ic_health),
        )

        CustomListItem(
            title = "Voltage",
            summary = batteryInfo.voltage,
            icon = painterResource(R.drawable.ic_lightning),
        )

	Column(
	    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
	) {
            Card(
		elevation = CardDefaults.cardElevation(
		    defaultElevation = 8.dp
                )
            ) {
                CustomListItem(
                    title = "Design capacity",
                    summary = batteryInfo.designCapacity
                )

                CustomListItem(
                    title = "Maximum capacity",
                    summary = batteryInfo.maximumCapacity
                )
            }
        }
    }
}

@Composable
fun ChargingCard(viewModel: BatteryViewModel) {
    val chargingState by viewModel.chargingState.collectAsState()

    Card {
        CustomListItem(
            title = "Charging",
            titleLarge = true
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        if (chargingState.hasFastCharging) {
            SwitchItem(
                title = "Fast charging",
                summary = "Enable force fast charging",
                checked = chargingState.isFastChargingChecked,
                onCheckedChange = { viewModel.toggleFastCharging(it) }
            )
        }

        if (chargingState.hasBypassCharging) {
            SwitchItem(
                title = "Bypass charging",
                summary = "Make sure your kernel supports this feature!",
                checked = chargingState.isBypassChargingChecked,
                onCheckedChange = { viewModel.toggleBypassCharging(it) }
            )
        }
    }
}
