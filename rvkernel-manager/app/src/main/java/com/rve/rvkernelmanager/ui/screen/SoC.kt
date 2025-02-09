package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.ui.ViewModel.SoCViewModel
import com.rve.rvkernelmanager.utils.*
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoCScreen(viewModel: SoCViewModel = viewModel()) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val hasBigCluster by viewModel.hasBigCluster.collectAsState()
    val hasPrimeCluster by viewModel.hasPrimeCluster.collectAsState()

    Scaffold(
        topBar = {
            TopBar(scrollBehavior = scrollBehavior)
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Top + WindowInsetsSides.Horizontal
        )
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
            LittleClusterCard(viewModel)
            if (hasBigCluster) {
                BigClusterCard(viewModel)
            }
            if (hasPrimeCluster) {
                PrimeClusterCard(viewModel)
            }
            MiscCard(viewModel)
            GPUCard(viewModel)
            Spacer(Modifier)
        }
    }
}

@Composable
private fun FreqRow(
    label: String,
    value: String,
    onClick: () -> Unit
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
        ElevatedButton(
            onClick = onClick
        ) {
            Text(
                text = if (value.isEmpty()) "error" else "$value MHz"
            )
        }
    }
}

@Composable
private fun GovRow(
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.gov),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        ElevatedButton(
            onClick = onClick,
        ) {
            Text(
                text = if (value.trim().isEmpty()) "error" else value
            )
        }
    }
}

@Composable
private fun FreqDialog(
    frequencies: List<String>,
    currentFreq: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        tonalElevation = 8.dp,
        title = {
            Text(
                "Available frequencies"
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (frequencies.isEmpty()) {
                    Text(
                        text = "Failed to read available frequencies",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    frequencies.forEach { freq ->
                        TextButton(
                            onClick = { onSelected(freq) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$freq MHz",
                                modifier = Modifier.fillMaxWidth(),
                                color = if (freq == currentFreq) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun GovDialog(
    governors: List<String>,
    currentGov: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        tonalElevation = 8.dp,
        title = {
            Text(
                "Available governors"
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (governors.isEmpty()) {
                    Text(
                        text = "Failed to read available governors",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    governors.forEach { gov ->
                        TextButton(
                            onClick = { onSelected(gov) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = gov,
                                modifier = Modifier.fillMaxWidth(),
                                color = if (gov == currentGov) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AdrenoBoostDialog(
    currentBoost: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    val boostOptions = listOf("0" to "Off", "1" to "Low", "2" to "Medium", "3" to "High")

    AlertDialog(
        onDismissRequest = onDismiss,
        tonalElevation = 8.dp,
        title = {
            Text(
                text = stringResource(R.string.adreno_boost)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                boostOptions.forEach { (value, label) ->
                    TextButton(
                        onClick = { onSelected(value) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.fillMaxWidth(),
                            color = if (value == currentBoost) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun SoCBaseCard(
    title: String,
    minFreq: String,
    maxFreq: String,
    gov: String,
    availableFreq: List<String>,
    availableGov: List<String>,
    onFreqSelected: (String, String) -> Unit,
    onGovSelected: (String) -> Unit
) {
    var showFreqDialog by remember { mutableStateOf(false) }
    var showGovDialog by remember { mutableStateOf(false) }
    var currentFileTarget by remember { mutableStateOf("") }

    ElevatedCard(
        shape = CardDefaults.shape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = stringResource(R.string.min_freq),
                value = minFreq,
                onClick = {
                    currentFileTarget = "min"
                    showFreqDialog = true
                }
            )
            
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = stringResource(R.string.max_freq),
                value = maxFreq,
                onClick = {
                    currentFileTarget = "max"
                    showFreqDialog = true
                }
            )
            
            Spacer(Modifier.height(4.dp))

            GovRow(
                value = gov,
                onClick = {
                    showGovDialog = true
                }
            )

            if (showFreqDialog) {
                FreqDialog(
                    frequencies = availableFreq,
                    currentFreq = if (currentFileTarget == "min") minFreq else maxFreq,
                    onDismiss = { showFreqDialog = false },
                    onSelected = { selectedFreq ->
                        onFreqSelected(currentFileTarget, selectedFreq)
                        showFreqDialog = false
                    }
                )
            }

            if (showGovDialog) {
                GovDialog(
                    governors = availableGov,
                    currentGov = gov,
                    onDismiss = { showGovDialog = false },
                    onSelected = { selectedGov ->
                        onGovSelected(selectedGov)
                        showGovDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun LittleClusterCard(viewModel: SoCViewModel) {
    val minFreqCPU0 by viewModel.minFreqCPU0.collectAsState()
    val maxFreqCPU0 by viewModel.maxFreqCPU0.collectAsState()
    val govCPU0 by viewModel.govCPU0.collectAsState()
    val availableFreqCPU0 by viewModel.availableFreqCPU0.collectAsState()
    val availableGovCPU0 by viewModel.availableGovCPU0.collectAsState()
    val hasBigCluster by viewModel.hasBigCluster.collectAsState()

    SoCBaseCard(
        title = if (hasBigCluster) stringResource(R.string.little_cluster) 
                else stringResource(R.string.cpu),
        minFreq = minFreqCPU0,
        maxFreq = maxFreqCPU0,
        gov = govCPU0,
        availableFreq = availableFreqCPU0,
        availableGov = availableGovCPU0,
        onFreqSelected = { target, selectedFreq ->
            viewModel.updateFreq(target, selectedFreq, "little")
        },
        onGovSelected = { selectedGov ->
            viewModel.updateGov(selectedGov, "little")
        }
    )
}

@Composable
fun BigClusterCard(viewModel: SoCViewModel) {
    val minFreqCPU4 by viewModel.minFreqCPU4.collectAsState()
    val maxFreqCPU4 by viewModel.maxFreqCPU4.collectAsState()
    val govCPU4 by viewModel.govCPU4.collectAsState()
    val availableFreqCPU4 by viewModel.availableFreqCPU4.collectAsState()
    val availableGovCPU4 by viewModel.availableGovCPU4.collectAsState()

    SoCBaseCard(
        title = stringResource(R.string.big_cluster),
        minFreq = minFreqCPU4,
        maxFreq = maxFreqCPU4,
        gov = govCPU4,
        availableFreq = availableFreqCPU4,
        availableGov = availableGovCPU4,
        onFreqSelected = { target, selectedFreq ->
            viewModel.updateFreq(target, selectedFreq, "big")
        },
        onGovSelected = { selectedGov ->
            viewModel.updateGov(selectedGov, "big")
        }
    )
}

@Composable
fun PrimeClusterCard(viewModel: SoCViewModel) {
    val minFreqCPU7 by viewModel.minFreqCPU7.collectAsState()
    val maxFreqCPU7 by viewModel.maxFreqCPU7.collectAsState()
    val govCPU7 by viewModel.govCPU7.collectAsState()
    val availableFreqCPU7 by viewModel.availableFreqCPU7.collectAsState()
    val availableGovCPU7 by viewModel.availableGovCPU7.collectAsState()

    SoCBaseCard(
        title = stringResource(R.string.prime_cluster),
        minFreq = minFreqCPU7,
        maxFreq = maxFreqCPU7,
        gov = govCPU7,
        availableFreq = availableFreqCPU7,
        availableGov = availableGovCPU7,
        onFreqSelected = { target, selectedFreq ->
            viewModel.updateFreq(target, selectedFreq, "prime")
        },
        onGovSelected = { selectedGov ->
            viewModel.updateGov(selectedGov, "prime")
        }
    )
}

@Composable
fun MiscCard(viewModel: SoCViewModel) {
    val thermalSconfig by viewModel.thermalSconfig.collectAsState()
    val hasThermalSconfig by viewModel.hasThermalSconfig.collectAsState()
    val thermalSconfigStatus = thermalSconfig == "10"

    ElevatedCard(
        shape = CardDefaults.shape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.misc_category),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))

            if (hasThermalSconfig) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.unlock_cpu_freq),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = thermalSconfigStatus,
                        onCheckedChange = { isChecked ->
                            viewModel.updateThermalSconfig(isChecked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GPUCard(viewModel: SoCViewModel) {
    val minFreqGPU by viewModel.minFreqGPU.collectAsState()
    val maxFreqGPU by viewModel.maxFreqGPU.collectAsState()
    val govGPU by viewModel.govGPU.collectAsState()
    val adrenoBoost by viewModel.adrenoBoost.collectAsState()
    val gpuThrottling by viewModel.gpuThrottling.collectAsState()
    val availableFreqGPU by viewModel.availableFreqGPU.collectAsState()
    val availableGovGPU by viewModel.availableGovGPU.collectAsState()
    val hasAdrenoBoost by viewModel.hasAdrenoBoost.collectAsState()
    val hasGPUThrottling by viewModel.hasGPUThrottling.collectAsState()

    var showAvailableFreqGPU by remember { mutableStateOf(false) }
    var showAvailableGovGPU by remember { mutableStateOf(false) }
    var showAdrenoBoost by remember { mutableStateOf(false) }
    var currentFileTarget by remember { mutableStateOf("") }

    val adrenoBoostText = when (adrenoBoost) {
        "0" -> "Off"
        "1" -> "Low"
        "2" -> "Medium"
        "3" -> "High"
        else -> "Unknown"
    }

    val gpuThrottlingStatus = gpuThrottling == "1"

    ElevatedCard(
        shape = CardDefaults.shape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.gpu),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = stringResource(R.string.min_freq),
                value = minFreqGPU,
                onClick = {
                    currentFileTarget = MIN_FREQ_GPU_PATH
                    showAvailableFreqGPU = true
                }
            )
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = stringResource(R.string.max_freq),
                value = maxFreqGPU,
                onClick = {
                    currentFileTarget = MAX_FREQ_GPU_PATH
                    showAvailableFreqGPU = true
                }
            )
            Spacer(Modifier.height(4.dp))

            GovRow(
                value = govGPU,
                onClick = {
                    currentFileTarget = GOV_GPU_PATH
                    showAvailableGovGPU = true
                }
            )

            if (hasAdrenoBoost) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.adreno_boost),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    ElevatedButton(
                        onClick = {
                            currentFileTarget = ADRENO_BOOST_PATH
                            showAdrenoBoost = true
                        }
                    ) {
                        Text(
                            text = adrenoBoostText
                        )
                    }
                }
            }

            if (hasGPUThrottling) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.gpu_throttling),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = gpuThrottlingStatus,
                        onCheckedChange = { isChecked ->
                            viewModel.updateGPUThrottling(isChecked)
                        }
                    )
                }
            }

            if (showAvailableFreqGPU) {
                FreqDialog(
                    frequencies = availableFreqGPU,
                    currentFreq = if (currentFileTarget == MIN_FREQ_GPU_PATH) minFreqGPU else maxFreqGPU,
                    onDismiss = { showAvailableFreqGPU = false },
                    onSelected = { selectedFreq ->
                        viewModel.updateFreq(currentFileTarget, selectedFreq, "gpu")
                        showAvailableFreqGPU = false
                    }
                )
            }

            if (showAvailableGovGPU) {
                GovDialog(
                    governors = availableGovGPU,
                    currentGov = govGPU,
                    onDismiss = { showAvailableGovGPU = false },
                    onSelected = { selectedGov ->
                        viewModel.updateGov(selectedGov, "gpu")
                        showAvailableGovGPU = false
                    }
                )
            }

            if (showAdrenoBoost) {
                AdrenoBoostDialog(
                    currentBoost = adrenoBoost,
                    onDismiss = { showAdrenoBoost = false },
                    onSelected = { selectedBoost ->
                        viewModel.updateAdrenoBoost(selectedBoost)
                        showAdrenoBoost = false
                    }
                )
            }
        }
    }
}
