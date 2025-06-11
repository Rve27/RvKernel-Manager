package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.utils.SoCUtils
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.ui.viewmodel.SoCViewModel
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.utils.Utils.CustomItem

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
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = rememberLazyListState()
        ) {
	    item {
                SoCMonitorCard(viewModel)
            }
            item {
                LittleClusterCard(viewModel)
            }
            if (hasBigCluster) {
                item {
                    BigClusterCard(viewModel)
                }
            }
            if (hasPrimeCluster) {
                item {
                    PrimeClusterCard(viewModel)
                }
            }
            item {
                GPUCard(viewModel)
            }
            item {
                Spacer(Modifier)
            }
        }
    }
}

@Composable
private fun SoCMonitorCard(viewModel: SoCViewModel) {
    val cpu0State by viewModel.cpu0State.collectAsState()
    val cpuUsage by viewModel.cpuUsage.collectAsState()
    val cpuTemp by viewModel.cpuTemp.collectAsState()
    val hasBigCluster by viewModel.hasBigCluster.collectAsState()
    val bigClusterState by viewModel.bigClusterState.collectAsState()
    val hasPrimeCluster by viewModel.hasPrimeCluster.collectAsState()
    val primeClusterState by viewModel.primeClusterState.collectAsState()
    val gpuState by viewModel.gpuState.collectAsState()
    val gpuTemp by viewModel.gpuTemp.collectAsState()
    val gpuUsage by viewModel.gpuUsage.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            CustomItem(
                title = "SoC Monitor",
                titleLarge = true,
		icon = painterResource(R.drawable.ic_monitor)
            )

            HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))

	    Card(
		elevation = CardDefaults.cardElevation(
		    defaultElevation = 8.dp
		)
	    ) {
		Column(
		   modifier = Modifier.padding(20.dp)
		) {
		    CustomItem(
			title = "CPU",
			titleLarge = true,
			icon = painterResource(R.drawable.ic_cpu)
		    )

		    HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))

		    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Usage",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (cpuUsage == "N/A") "N/A" else "$cpuUsage%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
		    Spacer(Modifier.height(8.dp))

		    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Temperature",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (cpuTemp == "N/A") "N/A" else "$cpuTemp°C",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (hasBigCluster) "Little cluster" else "Current freq",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
		        )
                        Text(
                            text = if (cpu0State.currentFreq.isEmpty()) "N/A" else "${cpu0State.currentFreq} MHz",
                            style = MaterialTheme.typography.bodyMedium
		        )
                    }

                    if (hasBigCluster) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Big cluster",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
			    )
                            Text(
                                text = if (bigClusterState.currentFreq.isEmpty()) "N/A" else "${bigClusterState.currentFreq} MHz",
                                style = MaterialTheme.typography.bodyMedium
			    )
                        }
                    }

                    if (hasPrimeCluster) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Prime cluster",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
			    )
                            Text(
                                text = if (primeClusterState.currentFreq.isEmpty()) "N/A" else "${primeClusterState.currentFreq} MHz",
                                style = MaterialTheme.typography.bodyMedium
			    )
                        }
                    }
                }
            }
	    Spacer(Modifier.height(20.dp))

	    Card(
		elevation = CardDefaults.cardElevation(
		    defaultElevation = 8.dp
		)
	    ) {
		Column(
		   modifier = Modifier.padding(20.dp)
		) {
		    CustomItem(
			title = "GPU",
			titleLarge = true,
			icon = painterResource(R.drawable.ic_video_card)
		    )

		    HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))

		    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Usage",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (gpuUsage == "N/A") "N/A" else "$gpuUsage%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
		    Spacer(Modifier.height(8.dp))

		    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Temperature",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (gpuTemp == "N/A") "N/A" else "$gpuTemp°C",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current freq",
			    style = MaterialTheme.typography.bodyMedium,
			    modifier = Modifier.weight(1f)
		        )
                        Text(
                            text = if (gpuState.currentFreq.isEmpty()) "N/A" else "${gpuState.currentFreq} MHz",
			    style = MaterialTheme.typography.bodyMedium
		        )
                    }
                }
            }
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
            text = "Governor",
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
    isFreqDialog: Boolean = false,
    isAdrenoBoostDialog: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        tonalElevation = 8.dp,
        title = { Text(title) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                if (items.isEmpty()) {
                    item {
                        Text(
                            text = "No items available",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    items(items) { item ->
                        TextButton(
                            onClick = { onSelected(item) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = when {
                                    isFreqDialog -> "$item MHz"
                                    isAdrenoBoostDialog -> when (item) {
                                        "0" -> "Off"
                                        "1" -> "Low"
                                        "2" -> "Medium"
                                        "3" -> "High"
                                        else -> item
                                    }
                                    else -> item
                                },
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
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))

            FreqRow(
                label = "Minimum frequency",
                value = minFreq,
                onClick = {
                    currentFileTarget = "min"
                    showFreqDialog = true
                }
            )
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = "Maximum frequency",
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
        title = if (viewModel.hasBigCluster.value) "Little Cluster" else "CPU",
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
        title = "Big Cluster",
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
        title = "Prime Cluster",
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
    val gpuThrottlingStatus = gpuState.gpuThrottling == "1"
    val sortedFreq = gpuState.availableFreq.sortedBy { it.toInt() }

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

    Card(
        shape = CardDefaults.shape,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "GPU",
                style = MaterialTheme.typography.titleLarge,
		modifier = Modifier
                    .clickable { expanded = !expanded }
            )
            
            HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))

            FreqRow(
                label = "Minimum frequency",
                value = gpuState.minFreq,
                onClick = {
                    currentFileTarget = "min"
                    showFreqDialog = true
                }
            )
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = "Maximum frequency",
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
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Column {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Adreno boost",
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
                        text = "GPU throttling",
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
                    title = "Available frequencies",
                    items = sortedFreq,
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
                    title = "Available governor",
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
                    title = "Adreno boost",
                    items = listOf("0", "1", "2", "3"),
                    currentItem = gpuState.adrenoBoost,
                    onDismiss = { showAdrenoBoostDialog = false },
                    onSelected = { selectedBoost ->
                        viewModel.updateAdrenoBoost(selectedBoost)
                        showAdrenoBoostDialog = false
                    },
		    isAdrenoBoostDialog = true
                )
            }
        }
    }
}
