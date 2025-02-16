package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.utils.SoCUtils
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.ui.ViewModel.SoCViewModel
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoCScreen(viewModel: SoCViewModel = viewModel(), lifecycleOwner: LifecycleOwner) {

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.startPolling()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.stopPolling()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
        Button(
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
        Button(
            onClick = onClick
        ) {
            Text(
                text = if (value.trim().isEmpty()) "error" else value
            )
        }
    }
}

@Composable
private fun SelectionDialog(
    title: String,
    items: List<String>,
    currentItem: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
    isFreqDialog: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        tonalElevation = 8.dp,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (items.isEmpty()) {
                    Text(
                        text = "No items available",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    items.forEach { item ->
                        TextButton(
                            onClick = { onSelected(item) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isFreqDialog) "$item MHz" else item,
                                modifier = Modifier.fillMaxWidth(),
                                color = if (item == currentItem) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.onBackground
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
private fun ClusterCard(
    title: String,
    minFreq: String,
    maxFreq: String,
    gov: String,
    availableFreq: List<String>,
    availableGov: List<String>,
    onUpdateFreq: (String, String) -> Unit,
    onUpdateGov: (String) -> Unit
) {
    var showFreqDialog by remember { mutableStateOf(false) }
    var showGovDialog by remember { mutableStateOf(false) }
    var currentFileTarget by remember { mutableStateOf("") }

    Card(
        shape = CardDefaults.shape,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
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
                onClick = { showGovDialog = true }
            )

            if (showFreqDialog) {
                SelectionDialog(
                    title = "Available Frequencies",
                    items = availableFreq,
                    currentItem = if (currentFileTarget == "min") minFreq else maxFreq,
                    onDismiss = { showFreqDialog = false },
                    onSelected = { selectedFreq ->
                        onUpdateFreq(currentFileTarget, selectedFreq)
                        showFreqDialog = false
                    },
		    isFreqDialog = true
                )
            }

            if (showGovDialog) {
                SelectionDialog(
                    title = "Available Governors",
                    items = availableGov,
                    currentItem = gov,
                    onDismiss = { showGovDialog = false },
                    onSelected = { selectedGov ->
                        onUpdateGov(selectedGov)
                        showGovDialog = false
                    },
		    isFreqDialog = false
                )
            }
        }
    }
}

@Composable
fun LittleClusterCard(viewModel: SoCViewModel) {
    val cpu0State by viewModel.cpu0State.collectAsState()

    ClusterCard(
        title = if (viewModel.hasBigCluster.value) stringResource(R.string.little_cluster) 
               else stringResource(R.string.cpu),
        minFreq = cpu0State.minFreq,
        maxFreq = cpu0State.maxFreq,
        gov = cpu0State.gov,
        availableFreq = cpu0State.availableFreq,
        availableGov = cpu0State.availableGov,
        onUpdateFreq = { target, freq -> viewModel.updateFreq(target, freq, "little") },
        onUpdateGov = { gov -> viewModel.updateGov(gov, "little") }
    )
}

@Composable
fun BigClusterCard(viewModel: SoCViewModel) {
    val bigClusterState by viewModel.bigClusterState.collectAsState()

    ClusterCard(
        title = stringResource(R.string.big_cluster),
        minFreq = bigClusterState.minFreq,
        maxFreq = bigClusterState.maxFreq,
        gov = bigClusterState.gov,
        availableFreq = bigClusterState.availableFreq,
        availableGov = bigClusterState.availableGov,
        onUpdateFreq = { target, freq -> viewModel.updateFreq(target, freq, "big") },
        onUpdateGov = { gov -> viewModel.updateGov(gov, "big") }
    )
}

@Composable
fun PrimeClusterCard(viewModel: SoCViewModel) {
    val primeClusterState by viewModel.primeClusterState.collectAsState()

    ClusterCard(
        title = stringResource(R.string.prime_cluster),
        minFreq = primeClusterState.minFreq,
        maxFreq = primeClusterState.maxFreq,
        gov = primeClusterState.gov,
        availableFreq = primeClusterState.availableFreq,
        availableGov = primeClusterState.availableGov,
        onUpdateFreq = { target, freq -> viewModel.updateFreq(target, freq, "prime") },
        onUpdateGov = { gov -> viewModel.updateGov(gov, "prime") }
    )
}

@Composable
fun GPUCard(viewModel: SoCViewModel) {
    val gpuState by viewModel.gpuState.collectAsState()
    val hasAdrenoBoost by viewModel.hasAdrenoBoost.collectAsState()
    val hasGPUThrottling by viewModel.hasGPUThrottling.collectAsState()

    var showFreqDialog by remember { mutableStateOf(false) }
    var showGovDialog by remember { mutableStateOf(false) }
    var showAdrenoBoostDialog by remember { mutableStateOf(false) }
    var currentFileTarget by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val adrenoBoostText = when (gpuState.adrenoBoost) {
        "0" -> "Off"
        "1" -> "Low"
        "2" -> "Medium"
        "3" -> "High"
        else -> "Unknown"
    }

    val gpuThrottlingStatus = gpuState.gpuThrottling == "1"

    Card(
        shape = CardDefaults.shape,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.gpu),
                style = MaterialTheme.typography.titleLarge,
		modifier = Modifier.clickable { expanded = !expanded }
            )
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = stringResource(R.string.min_freq),
                value = gpuState.minFreq,
                onClick = {
                    currentFileTarget = "min"
                    showFreqDialog = true
                }
            )
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = stringResource(R.string.max_freq),
                value = gpuState.maxFreq,
                onClick = {
                    currentFileTarget = "max"
                    showFreqDialog = true
                }
            )
            Spacer(Modifier.height(4.dp))

            GovRow(
                value = gpuState.gov,
                onClick = { showGovDialog = true }
            )

            AnimatedVisibility(
                visible = hasAdrenoBoost,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Column {
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
                        Button(
                            onClick = { showAdrenoBoostDialog = true }
                        ) {
                            Text(text = adrenoBoostText)
                        }
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

            if (showFreqDialog) {
                SelectionDialog(
                    title = "Available Frequencies",
                    items = gpuState.availableFreq,
                    currentItem = if (currentFileTarget == "min") gpuState.minFreq else gpuState.maxFreq,
                    onDismiss = { showFreqDialog = false },
                    onSelected = { selectedFreq ->
                        viewModel.updateFreq(currentFileTarget, selectedFreq, "gpu")
                        showFreqDialog = false
                    },
		    isFreqDialog = true
                )
            }

            if (showGovDialog) {
                SelectionDialog(
                    title = "Available Governors",
                    items = gpuState.availableGov,
                    currentItem = gpuState.gov,
                    onDismiss = { showGovDialog = false },
                    onSelected = { selectedGov ->
                        viewModel.updateGov(selectedGov, "gpu")
                        showGovDialog = false
                    },
		    isFreqDialog = false
                )
            }

            if (showAdrenoBoostDialog) {
                SelectionDialog(
                    title = "Adreno Boost",
                    items = listOf("0", "1", "2", "3"),
                    currentItem = gpuState.adrenoBoost,
                    onDismiss = { showAdrenoBoostDialog = false },
                    onSelected = { selectedBoost ->
                        viewModel.updateAdrenoBoost(selectedBoost)
                        showAdrenoBoostDialog = false
                    }
                )
            }
        }
    }
}
