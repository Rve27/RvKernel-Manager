package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.utils.testFile
import com.rve.rvkernelmanager.utils.readFile
import com.rve.rvkernelmanager.utils.writeFile
import com.rve.rvkernelmanager.utils.FAST_CHARGING_PATH
import com.rve.rvkernelmanager.utils.THERMAL_CHARGING_PATH
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
            var hasFastCharging by remember { mutableStateOf(testFile(FAST_CHARGING_PATH)) }
            if (hasFastCharging) {
                ChargingCard()
            }
            Spacer(Modifier)
        }
    }
}

@Composable
fun ChargingCard() {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isFastChargingChecked by remember { mutableStateOf(readFile(FAST_CHARGING_PATH) == "1") }
    var hasFastCharging by remember { mutableStateOf(testFile(FAST_CHARGING_PATH)) }
    var isThermalChargingChecked by remember { mutableStateOf(readFile(THERMAL_CHARGING_PATH) == "0") }
    var hasThermalCharging by remember { mutableStateOf(testFile(THERMAL_CHARGING_PATH)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasFastCharging = testFile(FAST_CHARGING_PATH)
                isFastChargingChecked = readFile(FAST_CHARGING_PATH) == "1"
                hasThermalCharging = testFile(THERMAL_CHARGING_PATH)
                isThermalChargingChecked = readFile(THERMAL_CHARGING_PATH) == "0"
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                            val success = writeFile(FAST_CHARGING_PATH, if (checked) "1" else "0")
                            if (success) {
                                isFastChargingChecked = checked
                            }
                        }
                    )
                }
            }

            if (hasThermalCharging) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.thermal_charging),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        modifier = Modifier.semantics { contentDescription = "Thermal Charging" },
                        checked = isThermalChargingChecked,
                        onCheckedChange = { checked ->
                            val success = writeFile(THERMAL_CHARGING_PATH, if (checked) "0" else "1")
                            if (success) {
                                isThermalChargingChecked = checked
                            }
                        }
                    )
                }
            }
        }
    }
}
