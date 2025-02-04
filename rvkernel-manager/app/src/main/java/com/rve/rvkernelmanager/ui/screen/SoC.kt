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
    val hasPrimeCluster = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            hasBigCluster.value = testFile(AVAILABLE_FREQ_CPU4_PATH)
	    hasPrimeCluster.value = testFile(AVAILABLE_FREQ_CPU7_PATH)
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
	    if (hasPrimeCluster.value) {
                PrimeClusterCard(scope)
            }
            MiscCard(scope)
            GPUCard(scope)
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
    currentFreq: String,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
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
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp,
        title = {
            Text(
                text = stringResource(R.string.adreno_boost),
                color = MaterialTheme.colorScheme.onBackground
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
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
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
fun LittleClusterCard(scope: CoroutineScope = rememberCoroutineScope()) {
    var minFreqCPU0 by remember { mutableStateOf("0") }
    var maxFreqCPU0 by remember { mutableStateOf("0") }
    var govCPU0 by remember { mutableStateOf("loading") }
    var availableFreqCPU0 by remember { mutableStateOf(listOf<String>()) }
    var availableGovCPU0 by remember { mutableStateOf(listOf<String>()) }
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

    SoCBaseCard(
        title = if (hasBigCluster.value) stringResource(R.string.little_cluster) 
                else stringResource(R.string.cpu),
        minFreq = minFreqCPU0,
        maxFreq = maxFreqCPU0,
        gov = govCPU0,
        availableFreq = availableFreqCPU0,
        availableGov = availableGovCPU0,
        onFreqSelected = { target, selectedFreq ->
            scope.launch(Dispatchers.IO) {
                val path = when (target) {
                    "min" -> MIN_FREQ_CPU0_PATH
                    "max" -> MAX_FREQ_CPU0_PATH
                    else -> return@launch
                }
                writeFreqCPU(path, selectedFreq)
                minFreqCPU0 = readFreqCPU(MIN_FREQ_CPU0_PATH)
                maxFreqCPU0 = readFreqCPU(MAX_FREQ_CPU0_PATH)
            }
        },
        onGovSelected = { selectedGov ->
            scope.launch(Dispatchers.IO) {
                writeFile(GOV_CPU0_PATH, selectedGov)
                govCPU0 = readFile(GOV_CPU0_PATH)
            }
        }
    )
}

@Composable
fun BigClusterCard(scope: CoroutineScope = rememberCoroutineScope()) {
    var minFreqCPU4 by remember { mutableStateOf("0") }
    var maxFreqCPU4 by remember { mutableStateOf("0") }
    var govCPU4 by remember { mutableStateOf("loading") }
    var availableFreqCPU4 by remember { mutableStateOf(listOf<String>()) }
    var availableGovCPU4 by remember { mutableStateOf(listOf<String>()) }

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

    SoCBaseCard(
        title = stringResource(R.string.big_cluster),
        minFreq = minFreqCPU4,
        maxFreq = maxFreqCPU4,
        gov = govCPU4,
        availableFreq = availableFreqCPU4,
        availableGov = availableGovCPU4,
        onFreqSelected = { target, selectedFreq ->
            scope.launch(Dispatchers.IO) {
                val path = when (target) {
                    "min" -> MIN_FREQ_CPU4_PATH
                    "max" -> MAX_FREQ_CPU4_PATH
                    else -> return@launch
                }
                writeFreqCPU(path, selectedFreq)
                minFreqCPU4 = readFreqCPU(MIN_FREQ_CPU4_PATH)
                maxFreqCPU4 = readFreqCPU(MAX_FREQ_CPU4_PATH)
            }
        },
        onGovSelected = { selectedGov ->
            scope.launch(Dispatchers.IO) {
                writeFile(GOV_CPU4_PATH, selectedGov)
                govCPU4 = readFile(GOV_CPU4_PATH)
            }
        }
    )
}

@Composable
fun PrimeClusterCard(scope: CoroutineScope = rememberCoroutineScope()) {
    var minFreqCPU7 by remember { mutableStateOf("0") }
    var maxFreqCPU7 by remember { mutableStateOf("0") }
    var govCPU7 by remember { mutableStateOf("loading") }
    var availableFreqCPU7 by remember { mutableStateOf(listOf<String>()) }
    var availableGovCPU7 by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            setPermissions(644, AVAILABLE_FREQ_CPU7_PATH)
            setPermissions(644, MIN_FREQ_CPU7_PATH)
            setPermissions(644, MAX_FREQ_CPU7_PATH)
            setPermissions(644, AVAILABLE_GOV_CPU7_PATH)
            setPermissions(644, GOV_CPU7_PATH)

            minFreqCPU7 = readFreqCPU(MIN_FREQ_CPU7_PATH)
            maxFreqCPU7 = readFreqCPU(MAX_FREQ_CPU7_PATH)
            govCPU7 = readFile(GOV_CPU7_PATH)
            availableGovCPU7 = readAvailableGovCPU(AVAILABLE_GOV_CPU7_PATH)
            availableFreqCPU7 = readAvailableFreqBoost(
                AVAILABLE_FREQ_CPU7_PATH,
                AVAILABLE_BOOST_CPU7_PATH
            )
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch(Dispatchers.IO) {
                    val newMinFreq = readFreqCPU(MIN_FREQ_CPU7_PATH)
                    val newMaxFreq = readFreqCPU(MAX_FREQ_CPU7_PATH)
                    val newGov = readFile(GOV_CPU7_PATH)
                    val newAvailableGov = readAvailableGovCPU(AVAILABLE_GOV_CPU7_PATH)
                    val newAvailableFreq = readAvailableFreqBoost(
                        AVAILABLE_FREQ_CPU7_PATH,
                        AVAILABLE_BOOST_CPU7_PATH
                    )
                    
                    withContext(Dispatchers.Main) {
                        minFreqCPU7 = newMinFreq
                        maxFreqCPU7 = newMaxFreq
                        govCPU7 = newGov
                        availableGovCPU7 = newAvailableGov
                        availableFreqCPU7 = newAvailableFreq
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    SoCBaseCard(
        title = stringResource(R.string.prime_cluster),
        minFreq = minFreqCPU7,
        maxFreq = maxFreqCPU7,
        gov = govCPU7,
        availableFreq = availableFreqCPU7,
        availableGov = availableGovCPU7,
        onFreqSelected = { target, selectedFreq ->
            scope.launch(Dispatchers.IO) {
                val path = when (target) {
                    "min" -> MIN_FREQ_CPU7_PATH
                    "max" -> MAX_FREQ_CPU7_PATH
                    else -> return@launch
                }
                writeFreqCPU(path, selectedFreq)
                minFreqCPU7 = readFreqCPU(MIN_FREQ_CPU7_PATH)
                maxFreqCPU7 = readFreqCPU(MAX_FREQ_CPU7_PATH)
            }
        },
        onGovSelected = { selectedGov ->
            scope.launch(Dispatchers.IO) {
                writeFile(GOV_CPU7_PATH, selectedGov)
                govCPU7 = readFile(GOV_CPU7_PATH)
            }
        }
    )
}

@Composable
fun MiscCard(scope: CoroutineScope = rememberCoroutineScope()) {
    var thermalSconfig by remember { mutableStateOf("0") }
    val hasThermalSconfig = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            setPermissions(644, THERMAL_SCONFIG_PATH)
            thermalSconfig = readFile(THERMAL_SCONFIG_PATH)
            hasThermalSconfig.value = testFile(THERMAL_SCONFIG_PATH)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                scope.launch(Dispatchers.IO) {
                    val isThermalSconfigExists = testFile(THERMAL_SCONFIG_PATH)
                    
                    withContext(Dispatchers.Main) {
                        hasThermalSconfig.value = isThermalSconfigExists
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val thermalSconfigStatus = thermalSconfig == "10"

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
                text = stringResource(R.string.misc_category),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))

            if (hasThermalSconfig.value) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.unlock_cpu_freq),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = thermalSconfigStatus,
                        onCheckedChange = { isChecked ->
                            scope.launch(Dispatchers.IO) {
                                setPermissions(644, THERMAL_SCONFIG_PATH)  // Set to 644 before writing
                                val newValue = if (isChecked) "10" else "0"
                                writeFile(THERMAL_SCONFIG_PATH, newValue)
                                setPermissions(444, THERMAL_SCONFIG_PATH)  // Set to 444 after writing
                                thermalSconfig = readFile(THERMAL_SCONFIG_PATH)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GPUCard(scope: CoroutineScope = rememberCoroutineScope()) {
    var minFreqGPU by remember { mutableStateOf("0") }
    var maxFreqGPU by remember { mutableStateOf("0") }
    var govGPU by remember { mutableStateOf("loading") }
    var adrenoBoost by remember { mutableStateOf("loading") }
    var gpuThrottling by remember { mutableStateOf("0") }
    var availableFreqGPU by remember { mutableStateOf(listOf<String>()) }
    var availableGovGPU by remember { mutableStateOf(listOf<String>()) }
    var showAvailableFreqGPU by remember { mutableStateOf(false) }
    var showAvailableGovGPU by remember { mutableStateOf(false) }
    var showAdrenoBoost by remember { mutableStateOf(false) }
    var currentFileTarget by remember { mutableStateOf("") }
    val hasAdrenoBoost = remember { mutableStateOf(false) }
    val hasGPUThrottling = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            setPermissions(644, AVAILABLE_FREQ_GPU_PATH)
            setPermissions(644, MIN_FREQ_GPU_PATH)
            setPermissions(644, MAX_FREQ_GPU_PATH)
            setPermissions(644, AVAILABLE_GOV_GPU_PATH)
            setPermissions(644, GOV_GPU_PATH)
            setPermissions(644, ADRENO_BOOST_PATH)
            setPermissions(644, GPU_THROTTLING_PATH)
            
            minFreqGPU = readFile(MIN_FREQ_GPU_PATH)
            maxFreqGPU = readFile(MAX_FREQ_GPU_PATH)
            govGPU = readFile(GOV_GPU_PATH)
            availableGovGPU = readAvailableGovGPU(AVAILABLE_GOV_GPU_PATH)
            availableFreqGPU = readAvailableFreqGPU(AVAILABLE_FREQ_GPU_PATH)
            adrenoBoost = readFile(ADRENO_BOOST_PATH)
            gpuThrottling = readFile(GPU_THROTTLING_PATH)
            hasAdrenoBoost.value = testFile(ADRENO_BOOST_PATH)
            hasGPUThrottling.value = testFile(GPU_THROTTLING_PATH)
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
                    val newAdrenoBoost = readFile(ADRENO_BOOST_PATH)
                    val newGPUThrottling = readFile(GPU_THROTTLING_PATH)
                    val isAdrenoBoostExists = testFile(ADRENO_BOOST_PATH)
                    val isGPUThrottlingExists = testFile(GPU_THROTTLING_PATH)
                    
                    withContext(Dispatchers.Main) {
                        minFreqGPU = newMinFreq
                        maxFreqGPU = newMaxFreq
                        govGPU = newGov
                        availableGovGPU = newAvailableGov
                        availableFreqGPU = newAvailableFreq
                        adrenoBoost = newAdrenoBoost
                        gpuThrottling = newGPUThrottling
                        hasAdrenoBoost.value = isAdrenoBoostExists
                        hasGPUThrottling.value = isGPUThrottlingExists
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val adrenoBoostText = when (adrenoBoost) {
        "0" -> "Off"
        "1" -> "Low"
        "2" -> "Medium"
        "3" -> "High"
        else -> "Unknown"
    }

    val gpuThrottlingStatus = gpuThrottling == "1"

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

            if (hasAdrenoBoost.value) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.adreno_boost),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    ElevatedButton(
                        onClick = {
                            currentFileTarget = ADRENO_BOOST_PATH
                            showAdrenoBoost = true
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = adrenoBoostText,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            if (hasGPUThrottling.value) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.gpu_throttling),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = gpuThrottlingStatus,
                        onCheckedChange = { isChecked ->
                            scope.launch(Dispatchers.IO) {
                                val newValue = if (isChecked) "1" else "0"
                                writeFile(GPU_THROTTLING_PATH, newValue)
                                gpuThrottling = readFile(GPU_THROTTLING_PATH)
                            }
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
                    currentGov = govGPU,
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

            if (showAdrenoBoost) {
                AdrenoBoostDialog(
                    currentBoost = adrenoBoost,
                    onDismiss = { showAdrenoBoost = false },
                    onSelected = { selectedBoost ->
                        scope.launch(Dispatchers.IO) {
                            writeFile(currentFileTarget, selectedBoost)
                            val newAdrenoBoost = readFile(ADRENO_BOOST_PATH)
                            withContext(Dispatchers.Main) {
                                showAdrenoBoost = false
                                adrenoBoost = newAdrenoBoost
                            }
                        }
                    }
                )
            }
        }
    }
}
