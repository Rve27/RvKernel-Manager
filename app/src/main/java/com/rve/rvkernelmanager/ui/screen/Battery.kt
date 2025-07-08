package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.ui.navigation.*
import com.rve.rvkernelmanager.ui.viewmodel.BatteryViewModel
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.component.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryScreen(
    viewModel: BatteryViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner
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

    Scaffold {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
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
		    title = "Voltage",
		    summary = batteryInfo.voltage
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
            SwitchListItem(
                title = "Fast charging",
                summary = "Enable force fast charging",
                checked = chargingState.isFastChargingChecked,
                onCheckedChange = { viewModel.toggleFastCharging(it) }
            )
        }

        if (chargingState.hasBypassCharging) {
            SwitchListItem(
                title = "Bypass charging",
                summary = "Make sure your kernel supports this feature!",
                checked = chargingState.isBypassChargingChecked,
                onCheckedChange = { viewModel.toggleBypassCharging(it) }
            )
        }
    }
}
