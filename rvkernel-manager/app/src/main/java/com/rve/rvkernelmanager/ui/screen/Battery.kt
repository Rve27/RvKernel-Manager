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
import androidx.compose.ui.res.stringResource
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
    val hasEnableCharging by viewModel.hasEnableCharging.collectAsState()
    val hasFastCharging by viewModel.hasFastCharging.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.loadBatteryInfo(context)
                    viewModel.registerBatteryListeners(context)
                    viewModel.checkChargingFiles()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.unregisterBatteryListeners(context)
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadBatteryInfo(context)
        viewModel.registerBatteryListeners(context)
        viewModel.checkChargingFiles()
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
            if (hasEnableCharging || hasFastCharging) {
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
    val battTech by viewModel.battTech.collectAsState()
    val battHealth by viewModel.battHealth.collectAsState()
    val battTemp by viewModel.battTemp.collectAsState()
    val battVoltage by viewModel.battVoltage.collectAsState()
    val battDesignCapacity by viewModel.battDesignCapacity.collectAsState()
    val battMaximumCapacity by viewModel.battMaximumCapacity.collectAsState()

    Card(
        shape = CardDefaults.shape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.batt_info_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))

            InfoRow(label = stringResource(R.string.batt_tech), value = battTech)
            InfoRow(label = stringResource(R.string.batt_health), value = battHealth)
            InfoRow(label = stringResource(R.string.batt_temp), value = battTemp)
            InfoRow(label = stringResource(R.string.batt_volt), value = battVoltage)
            InfoRow(label = stringResource(R.string.batt_design_capacity), value = battDesignCapacity)
            InfoRow(label = stringResource(R.string.batt_max_capacity), value = battMaximumCapacity)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ChargingCard(viewModel: BatteryViewModel) {
    val isEnableChargingChecked by viewModel.isEnableChargingChecked.collectAsState()
    val hasEnableCharging by viewModel.hasEnableCharging.collectAsState()
    val isFastChargingChecked by viewModel.isFastChargingChecked.collectAsState()
    val hasFastCharging by viewModel.hasFastCharging.collectAsState()

    Card(
        shape = CardDefaults.shape
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.charging_title),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))

            if (hasEnableCharging) {
                SwitchRow(
                    label = stringResource(R.string.enable_charging),
                    checked = isEnableChargingChecked,
                    onCheckedChange = { viewModel.toggleEnableCharging(it) }
                )
            }

            if (hasFastCharging) {
                SwitchRow(
                    label = stringResource(R.string.fast_charging),
                    checked = isFastChargingChecked,
                    onCheckedChange = { viewModel.toggleFastCharging(it) }
                )
            }
        }
    }
}

@Composable
fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            modifier = Modifier.semantics { contentDescription = label },
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
