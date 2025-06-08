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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.ui.ViewModel.BatteryViewModel
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryScreen(
    viewModel: BatteryViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val hasFastCharging by viewModel.hasFastCharging.collectAsState()
    val hasBypassCharging by viewModel.hasBypassCharging.collectAsState()

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
            TopBar(
                scrollBehavior = scrollBehavior
            )
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
                BatteryInfoCard(viewModel)
	    }
            if (hasFastCharging || hasBypassCharging) {
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
fun BatteryInfoCard(viewModel: BatteryViewModel) {
    Card(
        shape = CardDefaults.shape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Battery Information",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

            InfoRow(label = "Technology", value = viewModel.battTech.collectAsState().value)
            InfoRow(label = "Health", value = viewModel.battHealth.collectAsState().value)
            InfoRow(label = "Temperature", value = viewModel.battTemp.collectAsState().value)
            InfoRow(label = "Voltage", value = viewModel.battVoltage.collectAsState().value)
            InfoRow(label = "Design capacity", value = viewModel.battDesignCapacity.collectAsState().value)
            InfoRow(label = "Maximum capacity", value = viewModel.battMaximumCapacity.collectAsState().value, withSpacer = false)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, withSpacer: Boolean = true) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
        if (withSpacer) {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ChargingCard(viewModel: BatteryViewModel) {
    val isFastChargingChecked by viewModel.isFastChargingChecked.collectAsState()
    val hasFastCharging by viewModel.hasFastCharging.collectAsState()
    val isBypassChargingChecked by viewModel.isBypassChargingChecked.collectAsState()
    val hasBypassCharging by viewModel.hasBypassCharging.collectAsState()

    Card(
        shape = CardDefaults.shape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Charging",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

            if (hasFastCharging) {
                SwitchRow(
                    label = "Fast charging",
		    summary = "Enable force fast charging",
                    checked = isFastChargingChecked,
                    onCheckedChange = { viewModel.toggleFastCharging(it) }
                )
            }

	    if (hasBypassCharging) {
                SwitchRow(
                    label = "Bypass charging",
		    summary = "Make sure your kernel is support this feature!",
                    checked = isBypassChargingChecked,
                    onCheckedChange = { viewModel.toggleBypassCharging(it) }
                )
            }
        }
    }
}

@Composable
fun SwitchRow(
    label: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
	Column(
	    modifier = Modifier.weight(1f)
	) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall
            )
	    Text(
	        text = summary,
	        style = MaterialTheme.typography.bodySmall,
	        modifier = Modifier.padding(top = 4.dp, end = 4.dp)
	    )
	}
	Switch(
            modifier = Modifier.semantics { contentDescription = label },
            checked = checked,
            onCheckedChange = onCheckedChange
	)
    }
}
