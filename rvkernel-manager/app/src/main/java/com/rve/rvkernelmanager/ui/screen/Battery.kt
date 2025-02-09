package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.ui.ViewModel.BatteryViewModel
import com.rve.rvkernelmanager.utils.*
import com.rve.rvkernelmanager.utils.BatteryUtils
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryScreen(viewModel: BatteryViewModel = viewModel()) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val hasEnableCharging by viewModel.hasEnableCharging.collectAsState()
    val hasFastCharging by viewModel.hasFastCharging.collectAsState()

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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BatteryInfoCard(viewModel)
            if (hasEnableCharging || hasFastCharging) {
                ChargingCard(viewModel)
            }
            Spacer(Modifier)
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

    ElevatedCard(
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.batt_info_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))
                
                Text(
                    text = stringResource(R.string.batt_tech),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = battTech,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))
                
                Text(
                    text = stringResource(R.string.batt_health),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = battHealth,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))
                
                Text(
                    text = stringResource(R.string.batt_temp),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = battTemp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.batt_volt),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = battVoltage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.batt_design_capacity),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = battDesignCapacity,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.batt_max_capacity),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = battMaximumCapacity,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun ChargingCard(viewModel: BatteryViewModel) {
    val isEnableChargingChecked by viewModel.isEnableChargingChecked.collectAsState()
    val hasEnableCharging by viewModel.hasEnableCharging.collectAsState()
    val isFastChargingChecked by viewModel.isFastChargingChecked.collectAsState()
    val hasFastCharging by viewModel.hasFastCharging.collectAsState()

    ElevatedCard(
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.charging_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))

            if (hasEnableCharging) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.enable_charging),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        modifier = Modifier.semantics { contentDescription = "Enable Charging" },
                        checked = isEnableChargingChecked,
                        onCheckedChange = { checked ->
                            viewModel.toggleEnableCharging(checked)
                        }
                    )
                }
            }

            if (hasFastCharging) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.fast_charging),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        modifier = Modifier.semantics { contentDescription = "Fast Charging" },
                        checked = isFastChargingChecked,
                        onCheckedChange = { checked ->
                            viewModel.toggleFastCharging(checked)
                        }
                    )
                }
            }
        }
    }
}
