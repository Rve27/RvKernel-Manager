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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.utils.*
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoCScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val scope = rememberCoroutineScope()
    val hasBigCluster = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            hasBigCluster.value = testFile(AVAILABLE_FREQ_CPU4_PATH)
        }
    }

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
            LittleClusterCard(scope)
            if (hasBigCluster.value) {
                BigClusterCard(scope)
            }
            GPUCard(scope)
            Spacer(Modifier)
        }
    }
}

@Composable
fun LittleClusterCard(scope: CoroutineScope) {
    var minFreqCPU0 by remember { mutableStateOf("0") }
    var maxFreqCPU0 by remember { mutableStateOf("0") }
    var govCPU0 by remember { mutableStateOf("loading") }
    var availableFreqCPU0 by remember { mutableStateOf(listOf<String>()) }
    var availableGovCPU0 by remember { mutableStateOf(listOf<String>()) }
    var showAvailableFreqCPU0 by remember { mutableStateOf(false) }
    var showAvailableGovCPU0 by remember { mutableStateOf(false) }
    var currentFileTarget by remember { mutableStateOf("") }
    val hasBigCluster = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            setPermissions(644, AVAILABLE_FREQ_CPU0_PATH)
            setPermissions(644, MIN_FREQ_CPU0_PATH)
            setPermissions(644, MAX_FREQ_CPU0_PATH)
            setPermissions(644, AVAILABLE_GOV_CPU0_PATH)
            setPermissions(644, GOV_CPU0_PATH)

            minFreqCPU0 = readFreqCPU(MIN_FREQ_CPU0_PATH)
            maxFreqCPU0 = readFreqCPU(MAX_FREQ_CPU0_PATH)
            govCPU0 = readFile(GOV_CPU0_PATH)
            availableFreqCPU0 = readAvailableFreqCPU(AVAILABLE_FREQ_CPU0_PATH)
            availableGovCPU0 = readAvailableGovCPU(AVAILABLE_GOV_CPU0_PATH)
            hasBigCluster.value = testFile(AVAILABLE_FREQ_CPU4_PATH)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch(Dispatchers.IO) {
                    val newMinFreq = readFreqCPU(MIN_FREQ_CPU0_PATH)
                    val newMaxFreq = readFreqCPU(MAX_FREQ_CPU0_PATH)
                    val newGov = readFile(GOV_CPU0_PATH)
                    val newAvailableFreq = readAvailableFreqCPU(AVAILABLE_FREQ_CPU0_PATH)
                    val newAvailableGov = readAvailableGovCPU(AVAILABLE_GOV_CPU0_PATH)
                    
                    withContext(Dispatchers.Main) {
                        minFreqCPU0 = newMinFreq
                        maxFreqCPU0 = newMaxFreq
                        govCPU0 = newGov
                        availableFreqCPU0 = newAvailableFreq
                        availableGovCPU0 = newAvailableGov
                    }
                }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = if (hasBigCluster.value) {
                    stringResource(R.string.little_cluster)
                } else stringResource(R.string.cpu),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = stringResource(R.string.min_freq),
                value = minFreqCPU0,
                onClick = {
                    currentFileTarget = MIN_FREQ_CPU0_PATH
                    showAvailableFreqCPU0 = true
                }
            )
            
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = stringResource(R.string.max_freq),
                value = maxFreqCPU0,
                onClick = {
                    currentFileTarget = MAX_FREQ_CPU0_PATH
                    showAvailableFreqCPU0 = true
                }
            )
            
            Spacer(Modifier.height(4.dp))

            GovRow(
                value = govCPU0,
                onClick = {
                    currentFileTarget = GOV_CPU0_PATH
                    showAvailableGovCPU0 = true
                }
            )

            if (showAvailableFreqCPU0) {
                FreqDialog(
                    frequencies = availableFreqCPU0,
                    onDismiss = { showAvailableFreqCPU0 = false },
                    onSelected = { selectedFreq ->
                        scope.launch(Dispatchers.IO) {
                            writeFreqCPU(currentFileTarget, selectedFreq)
                            val newMinFreq = readFreqCPU(MIN_FREQ_CPU0_PATH)
                            val newMaxFreq = readFreqCPU(MAX_FREQ_CPU0_PATH)
                            withContext(Dispatchers.Main) {
                                showAvailableFreqCPU0 = false
                                minFreqCPU0 = newMinFreq
                                maxFreqCPU0 = newMaxFreq
                            }
                        }
                    },
                    isClusterCard = true
                )
            }

            if (showAvailableGovCPU0) {
                GovDialog(
                    governors = availableGovCPU0,
                    onDismiss = { showAvailableGovCPU0 = false },
                    onSelected = { selectedGov ->
                        scope.launch(Dispatchers.IO) {
                            writeFile(currentFileTarget, selectedGov)
                            val newGov = readFile(GOV_CPU0_PATH)
                            withContext(Dispatchers.Main) {
                                showAvailableGovCPU0 = false
                                govCPU0 = newGov
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BigClusterCard(scope: CoroutineScope = rememberCoroutineScope()) {
    var minFreqCPU4 by remember { mutableStateOf("0") }
    var maxFreqCPU4 by remember { mutableStateOf("0") }
    var govCPU4 by remember { mutableStateOf("loading") }
    var availableFreqCPU4 by remember { mutableStateOf(listOf<String>()) }
    var availableGovCPU4 by remember { mutableStateOf(listOf<String>()) }
    var showAvailableFreqCPU4 by remember { mutableStateOf(false) }
    var showAvailableGovCPU4 by remember { mutableStateOf(false) }
    var currentFileTarget by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            setPermissions(644, AVAILABLE_FREQ_CPU4_PATH)
            setPermissions(644, MIN_FREQ_CPU4_PATH)
            setPermissions(644, MAX_FREQ_CPU4_PATH)
            setPermissions(644, AVAILABLE_GOV_CPU4_PATH)
            setPermissions(644, GOV_CPU4_PATH)
            
            minFreqCPU4 = readFreqCPU(MIN_FREQ_CPU4_PATH)
            maxFreqCPU4 = readFreqCPU(MAX_FREQ_CPU4_PATH)
            govCPU4 = readFile(GOV_CPU4_PATH)
            availableGovCPU4 = readAvailableGovCPU(AVAILABLE_GOV_CPU4_PATH)
            availableFreqCPU4 = readAvailableFreqBoost(
                AVAILABLE_FREQ_CPU4_PATH,
                AVAILABLE_BOOST_CPU4_PATH
            )
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch(Dispatchers.IO) {
                    val newMinFreq = readFreqCPU(MIN_FREQ_CPU4_PATH)
                    val newMaxFreq = readFreqCPU(MAX_FREQ_CPU4_PATH)
                    val newGov = readFile(GOV_CPU4_PATH)
                    val newAvailableGov = readAvailableGovCPU(AVAILABLE_GOV_CPU4_PATH)
                    val newAvailableFreq = readAvailableFreqBoost(
                        AVAILABLE_FREQ_CPU4_PATH,
                        AVAILABLE_BOOST_CPU4_PATH
                    )
                    
                    withContext(Dispatchers.Main) {
                        minFreqCPU4 = newMinFreq
                        maxFreqCPU4 = newMaxFreq
                        govCPU4 = newGov
                        availableGovCPU4 = newAvailableGov
                        availableFreqCPU4 = newAvailableFreq
                    }
                }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.big_cluster),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = stringResource(R.string.min_freq),
                value = minFreqCPU4,
                onClick = {
                    currentFileTarget = MIN_FREQ_CPU4_PATH
                    showAvailableFreqCPU4 = true
                }
            )
            
            Spacer(Modifier.height(4.dp))

            FreqRow(
                label = stringResource(R.string.max_freq),
                value = maxFreqCPU4,
                onClick = {
                    currentFileTarget = MAX_FREQ_CPU4_PATH
                    showAvailableFreqCPU4 = true
                }
            )
            
            Spacer(Modifier.height(4.dp))

            GovRow(
                value = govCPU4,
                onClick = {
                    currentFileTarget = GOV_CPU4_PATH
                    showAvailableGovCPU4 = true
                }
            )

            if (showAvailableFreqCPU4) {
                FreqDialog(
                    frequencies = availableFreqCPU4,
                    onDismiss = { showAvailableFreqCPU4 = false },
                    onSelected = { selectedFreq ->
                        scope.launch(Dispatchers.IO) {
                            writeFreqCPU(currentFileTarget, selectedFreq)
                            val newMinFreq = readFreqCPU(MIN_FREQ_CPU4_PATH)
                            val newMaxFreq = readFreqCPU(MAX_FREQ_CPU4_PATH)
                            withContext(Dispatchers.Main) {
                                showAvailableFreqCPU4 = false
                                minFreqCPU4 = newMinFreq
                                maxFreqCPU4 = newMaxFreq
                            }
                        }
                    },
                    isClusterCard = true
                )
            }

            if (showAvailableGovCPU4) {
                GovDialog(
                    governors = availableGovCPU4,
                    onDismiss = { showAvailableGovCPU4 = false },
                    onSelected = { selectedGov ->
                        scope.launch(Dispatchers.IO) {
                            writeFile(currentFileTarget, selectedGov)
                            val newGov = readFile(GOV_CPU4_PATH)
                            withContext(Dispatchers.Main) {
                                showAvailableGovCPU4 = false
                                govCPU4 = newGov
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GPUCard(scope: CoroutineScope = rememberCoroutineScope()) {
    var minFreqGPU by remember { mutableStateOf("0") }
    var maxFreqGPU by remember { mutableStateOf("0") }
    var govGPU by remember { mutableStateOf("loading") }
    var availableFreqGPU by remember { mutableStateOf(listOf<String>()) }
    var availableGovGPU by remember { mutableStateOf(listOf<String>()) }
    var showAvailableFreqGPU by remember { mutableStateOf(false) }
    var showAvailableGovGPU by remember { mutableStateOf(false) }
    var currentFileTarget by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            setPermissions(644, AVAILABLE_FREQ_GPU_PATH)
            setPermissions(644, MIN_FREQ_GPU_PATH)
            setPermissions(644, MAX_FREQ_GPU_PATH)
            setPermissions(644, AVAILABLE_GOV_GPU_PATH)
            setPermissions(644, GOV_GPU_PATH)
            
            minFreqGPU = readFile(MIN_FREQ_GPU_PATH)
            maxFreqGPU = readFile(MAX_FREQ_GPU_PATH)
            govGPU = readFile(GOV_GPU_PATH)
            availableGovGPU = readAvailableGovGPU(AVAILABLE_GOV_GPU_PATH)
            availableFreqGPU = readAvailableFreqGPU(AVAILABLE_FREQ_GPU_PATH)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch(Dispatchers.IO) {
                    val newMinFreq = readFile(MIN_FREQ_GPU_PATH)
                    val newMaxFreq = readFile(MAX_FREQ_GPU_PATH)
                    val newGov = readFile(GOV_GPU_PATH)
                    val newAvailableGov = readAvailableGovGPU(AVAILABLE_GOV_GPU_PATH)
                    val newAvailableFreq = readAvailableFreqGPU(AVAILABLE_FREQ_GPU_PATH)
                    
                    withContext(Dispatchers.Main) {
                        minFreqGPU = newMinFreq
                        maxFreqGPU = newMaxFreq
                        govGPU = newGov
                        availableGovGPU = newAvailableGov
                        availableFreqGPU = newAvailableFreq
                    }
                }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.gpu),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
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

            if (showAvailableFreqGPU) {
                FreqDialog(
                    frequencies = availableFreqGPU,
                    onDismiss = { showAvailableFreqGPU = false },
                    onSelected = { selectedFreq ->
                        scope.launch(Dispatchers.IO) {
                            writeFreqGPU(currentFileTarget, selectedFreq)
                            val newMinFreq = readFile(MIN_FREQ_GPU_PATH)
                            val newMaxFreq = readFile(MAX_FREQ_GPU_PATH)
                            withContext(Dispatchers.Main) {
                                showAvailableFreqGPU = false
                                minFreqGPU = newMinFreq
                                maxFreqGPU = newMaxFreq
                            }
                        }
                    }
                )
            }

            if (showAvailableGovGPU) {
                GovDialog(
                    governors = availableGovGPU,
                    onDismiss = { showAvailableGovGPU = false },
                    onSelected = { selectedGov ->
                        scope.launch(Dispatchers.IO) {
                            writeFile(currentFileTarget, selectedGov)
                            val newGov = readFile(GOV_GPU_PATH)
                            withContext(Dispatchers.Main) {
                                showAvailableGovGPU = false
                                govGPU = newGov
                            }
                        }
                    }
                )
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
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
        ElevatedButton(
            onClick = onClick,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (value.isEmpty()) "error" else "$value MHz",
                color = MaterialTheme.colorScheme.onPrimary
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
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
        ElevatedButton(
            onClick = onClick,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (value.trim().isEmpty()) "error" else value,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun FreqDialog(
    frequencies: List<String>,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
    isClusterCard: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp,
        title = {
            Text(
                "Available frequencies",
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isClusterCard) Modifier.fillMaxHeight(0.8f) else Modifier)
                    .verticalScroll(rememberScrollState())
            ) {
                if (frequencies.isEmpty()) {
                    Text(
                        text = "Failed to read available frequencies",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
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
                                color = MaterialTheme.colorScheme.primary
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
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp,
        title = {
            Text(
                "Available governors",
                color = MaterialTheme.colorScheme.onBackground
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
                        color = MaterialTheme.colorScheme.onBackground,
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
                                color = MaterialTheme.colorScheme.primary
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
