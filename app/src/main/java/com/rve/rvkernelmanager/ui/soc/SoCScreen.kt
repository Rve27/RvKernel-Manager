/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.soc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Dvr
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.component.TitleExpandable
import com.rve.rvkernelmanager.ui.component.appBar.PinnedTopAppBar
import com.rve.rvkernelmanager.ui.component.listItem.ButtonListItem
import com.rve.rvkernelmanager.ui.component.listItem.CustomListItem
import com.rve.rvkernelmanager.ui.component.listItem.DialogTextButtonListItem
import com.rve.rvkernelmanager.ui.component.listItem.MonitorListItem
import com.rve.rvkernelmanager.ui.component.listItem.SwitchListItem
import com.rve.rvkernelmanager.ui.component.navigation.BottomNavigationBar
import com.rve.rvkernelmanager.ui.settings.SettingsPreference

@Composable
fun SoCScreen(viewModel: SoCViewModel = viewModel(), lifecycleOwner: LifecycleOwner, navController: NavController) {
    val context = LocalContext.current
    val settingsPreference = remember { SettingsPreference.getInstance(context) }
    val blurEnabled by settingsPreference.blurEnabled.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var isDialogOpen by remember { mutableStateOf(false) }

    val hasCpuInputBoostMs by viewModel.hasCpuInputBoostMs.collectAsState()
    val hasCpuSchedBoostOnInput by viewModel.hasCpuSchedBoostOnInput.collectAsState()
    val hasBigCluster by viewModel.hasBigCluster.collectAsState()
    val hasPrimeCluster by viewModel.hasPrimeCluster.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.startJob()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.stopJob()
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = { PinnedTopAppBar(scrollBehavior = scrollBehavior) },
        bottomBar = { BottomNavigationBar(navController) },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .then(if (isDialogOpen && blurEnabled) Modifier.blur(4.dp) else Modifier),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = rememberLazyListState(),
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                SoCMonitorCard(viewModel)
            }
            item {
                LittleClusterCard(viewModel = viewModel, onDialogStateChange = { isOpen -> isDialogOpen = isOpen })
            }
            if (hasBigCluster) {
                item {
                    BigClusterCard(viewModel = viewModel, onDialogStateChange = { isOpen -> isDialogOpen = isOpen })
                }
            }
            if (hasPrimeCluster) {
                item {
                    PrimeClusterCard(viewModel = viewModel, onDialogStateChange = { isOpen -> isDialogOpen = isOpen })
                }
            }
            if (hasCpuInputBoostMs || hasCpuSchedBoostOnInput) {
                item {
                    CPUBoostCard(viewModel = viewModel, onDialogStateChange = { isOpen -> isDialogOpen = isOpen })
                }
            }
            item {
                GPUCard(viewModel = viewModel, onDialogStateChange = { isOpen -> isDialogOpen = isOpen })
            }
            item {
                Spacer(Modifier)
            }
        }
    }
}

@Composable
fun SoCMonitorCard(viewModel: SoCViewModel) {
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

    LaunchedEffect(Unit) {
        viewModel.setActiveState("monitor", true)
    }

    Card {
        CustomListItem(
            title = "SoC Monitor",
            titleLarge = true,
            icon = Icons.AutoMirrored.Default.Dvr,
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp,
                ),
            ) {
                CustomListItem(
                    title = "CPU",
                    titleLarge = true,
                    icon = painterResource(R.drawable.ic_cpu),
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    MonitorListItem(
                        title = "Usage",
                        summary = if (cpuUsage == "N/A") "N/A" else "$cpuUsage%",
                    )

                    Spacer(Modifier.height(8.dp))
                    MonitorListItem(
                        title = "Temperature",
                        summary = if (cpuTemp == "N/A") "N/A" else "$cpuTemp°C",
                    )

                    Spacer(Modifier.height(8.dp))
                    MonitorListItem(
                        title = if (hasBigCluster) "Little cluster" else "Current freq",
                        summary = if (cpu0State.currentFreq.isEmpty()) "N/A" else "${cpu0State.currentFreq} MHz",
                    )

                    if (hasBigCluster) {
                        Spacer(Modifier.height(8.dp))
                        MonitorListItem(
                            title = "Big cluster",
                            summary = if (bigClusterState.currentFreq.isEmpty()) "N/A" else "${bigClusterState.currentFreq} MHz",
                        )
                    }

                    if (hasPrimeCluster) {
                        Spacer(Modifier.height(8.dp))
                        MonitorListItem(
                            title = "Prime cluster",
                            summary = if (primeClusterState.currentFreq.isEmpty()) "N/A" else "${primeClusterState.currentFreq} MHz",
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp,
                ),
            ) {
                CustomListItem(
                    title = "GPU",
                    titleLarge = true,
                    icon = painterResource(R.drawable.ic_video_card),
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    MonitorListItem(
                        title = "Usage",
                        summary = if (gpuUsage == "N/A") "N/A" else "$gpuUsage%",
                    )

                    Spacer(Modifier.height(8.dp))
                    MonitorListItem(
                        title = "Temperature",
                        summary = if (gpuTemp == "N/A") "N/A" else "$gpuTemp°C",
                    )

                    Spacer(Modifier.height(8.dp))
                    MonitorListItem(
                        title = "Current freq",
                        summary = if (gpuState.currentFreq.isEmpty()) "N/A" else "${gpuState.currentFreq} MHz",
                    )
                }
            }
        }
    }
}

@Composable
fun LittleClusterCard(viewModel: SoCViewModel, onDialogStateChange: (Boolean) -> Unit = {}) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    var targetFreqCPU0 by remember { mutableStateOf<String?>(null) }
    // ACG = Available CPU Governor
    var openACG by remember { mutableStateOf(false) }

    val cpu0State by viewModel.cpu0State.collectAsState()
    val hasBigCluster by viewModel.hasBigCluster.collectAsState()

    LaunchedEffect(isExpanded) {
        viewModel.setActiveState("little", isExpanded)
    }

    LaunchedEffect(targetFreqCPU0, openACG) {
        onDialogStateChange(targetFreqCPU0 != null || openACG)
    }

    Card {
        TitleExpandable(
            leadingIcon = painterResource(R.drawable.ic_cpu),
            text = if (hasBigCluster) "Little Cluster" else "CPU",
            titleLarge = true,
            trailingIcon = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            onClick = { isExpanded = !isExpanded },
        )

        AnimatedVisibility(isExpanded) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ButtonListItem(
                    title = "Minimum frequency",
                    summary = if (hasBigCluster)
                        "The lowest speed the Little Cluster can run at"
                    else
                        "The lowest speed the CPU can run at",
                    value = cpu0State.minFreq,
                    isFreq = true,
                    onClick = { targetFreqCPU0 = "min" },
                )

                ButtonListItem(
                    title = "Maximum frequency",
                    summary = if (hasBigCluster)
                        "The highest speed the Little Cluster can run at"
                    else
                        "The highest speed the CPU can run at",
                    value = cpu0State.maxFreq,
                    isFreq = true,
                    onClick = { targetFreqCPU0 = "max" },
                )

                ButtonListItem(
                    title = "Governor",
                    summary = if (hasBigCluster)
                        "Controls how the Little Cluster scales between min and max frequencies"
                    else
                        "Controls how the CPU scales between min and max frequencies",
                    value = cpu0State.gov,
                    onClick = { openACG = true },
                )
            }
        }

        if (targetFreqCPU0 != null) {
            AlertDialog(
                onDismissRequest = { targetFreqCPU0 = null },
                title = { Text("Available frequencies") },
                text = {
                    if (cpu0State.availableFreq.isNotEmpty()) {
                        LazyColumn {
                            items(cpu0State.availableFreq) { freq ->
                                DialogTextButtonListItem(
                                    text = "$freq MHz",
                                    onClick = {
                                        targetFreqCPU0?.let { targetFreq ->
                                            viewModel.updateFreq(targetFreq, freq, "little")
                                        }
                                        targetFreqCPU0 = null
                                    },
                                )
                            }
                        }
                    } else {
                        Text("No available frequencies found.")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { targetFreqCPU0 = null },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text("Close")
                    }
                },
            )
        }

        if (openACG) {
            AlertDialog(
                onDismissRequest = { openACG = false },
                title = { Text("Available governor") },
                text = {
                    if (cpu0State.availableGov.isNotEmpty()) {
                        LazyColumn {
                            items(cpu0State.availableGov) { gov ->
                                DialogTextButtonListItem(
                                    text = gov,
                                    onClick = {
                                        viewModel.updateGov(gov, "little")
                                        openACG = false
                                    },
                                )
                            }
                        }
                    } else {
                        Text("No available governor found.")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { openACG = false },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text("Close")
                    }
                },
            )
        }
    }
}

@Composable
fun BigClusterCard(viewModel: SoCViewModel, onDialogStateChange: (Boolean) -> Unit = {}) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    var targetBigFreq by remember { mutableStateOf<String?>(null) }
    // ACG = Available CPU Governor
    var openACG by remember { mutableStateOf(false) }

    val bigClusterState by viewModel.bigClusterState.collectAsState()

    LaunchedEffect(isExpanded) {
        viewModel.setActiveState("big", isExpanded)
    }

    LaunchedEffect(targetBigFreq, openACG) {
        onDialogStateChange(targetBigFreq != null || openACG)
    }

    Card {
        TitleExpandable(
            leadingIcon = painterResource(R.drawable.ic_cpu),
            text = "Big Cluster",
            titleLarge = true,
            onClick = { isExpanded = !isExpanded },
            trailingIcon = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
        )

        AnimatedVisibility(isExpanded) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ButtonListItem(
                    title = "Minimum frequency",
                    summary = "The lowest speed the Big Cluster can run at",
                    value = bigClusterState.minFreq,
                    isFreq = true,
                    onClick = { targetBigFreq = "min" },
                )

                ButtonListItem(
                    title = "Maximum frequency",
                    summary = "The highest speed the Big Cluster can run at",
                    value = bigClusterState.maxFreq,
                    isFreq = true,
                    onClick = { targetBigFreq = "max" },
                )

                ButtonListItem(
                    title = "Governor",
                    summary = "Controls how the Big Cluster scales between min and max frequencies",
                    value = bigClusterState.gov,
                    onClick = { openACG = true },
                )
            }
        }

        if (targetBigFreq != null) {
            AlertDialog(
                onDismissRequest = { targetBigFreq = null },
                title = { Text("Available frequencies") },
                text = {
                    if (bigClusterState.availableFreq.isNotEmpty()) {
                        LazyColumn {
                            items(bigClusterState.availableFreq) { freq ->
                                DialogTextButtonListItem(
                                    text = "$freq MHz",
                                    onClick = {
                                        targetBigFreq?.let { targetFreq ->
                                            viewModel.updateFreq(targetFreq, freq, "big")
                                        }
                                        targetBigFreq = null
                                    },
                                )
                            }
                        }
                    } else {
                        Text("No available frequencies found.")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { targetBigFreq = null },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text("Close")
                    }
                },
            )
        }

        if (openACG) {
            AlertDialog(
                onDismissRequest = { openACG = false },
                title = { Text("Available governor") },
                text = {
                    if (bigClusterState.availableGov.isNotEmpty()) {
                        LazyColumn {
                            items(bigClusterState.availableGov) { gov ->
                                DialogTextButtonListItem(
                                    text = gov,
                                    onClick = {
                                        viewModel.updateGov(gov, "big")
                                        openACG = false
                                    },
                                )
                            }
                        }
                    } else {
                        Text("No available governor found.")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { openACG = false },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text("Close")
                    }
                },
            )
        }
    }
}

@Composable
fun PrimeClusterCard(viewModel: SoCViewModel, onDialogStateChange: (Boolean) -> Unit = {}) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    var targetPrimeFreq by remember { mutableStateOf<String?>(null) }
    // ACG = Available CPU Governor
    var openACG by remember { mutableStateOf(false) }

    val primeClusterState by viewModel.primeClusterState.collectAsState()

    LaunchedEffect(isExpanded) {
        viewModel.setActiveState("prime", isExpanded)
    }

    LaunchedEffect(targetPrimeFreq, openACG) {
        onDialogStateChange(targetPrimeFreq != null || openACG)
    }

    Card {
        TitleExpandable(
            leadingIcon = painterResource(R.drawable.ic_cpu),
            text = "Prime Cluster",
            titleLarge = true,
            onClick = { isExpanded = !isExpanded },
            trailingIcon = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
        )

        AnimatedVisibility(isExpanded) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ButtonListItem(
                    title = "Minimum frequency",
                    summary = "The lowest speed the Prime Cluster can run at",
                    value = primeClusterState.minFreq,
                    isFreq = true,
                    onClick = { targetPrimeFreq = "min" },
                )

                ButtonListItem(
                    title = "Maximum frequency",
                    summary = "The highest speed the Prime Cluster can run at",
                    value = primeClusterState.maxFreq,
                    isFreq = true,
                    onClick = { targetPrimeFreq = "max" },
                )

                ButtonListItem(
                    title = "Governor",
                    summary = "Controls how the Prime Cluster scales between min and max frequencies",
                    value = primeClusterState.gov,
                    onClick = { openACG = true },
                )
            }
        }

        if (targetPrimeFreq != null) {
            AlertDialog(
                onDismissRequest = { targetPrimeFreq = null },
                title = { Text("Available frequencies") },
                text = {
                    if (primeClusterState.availableFreq.isNotEmpty()) {
                        LazyColumn {
                            items(primeClusterState.availableFreq) { freq ->
                                DialogTextButtonListItem(
                                    text = "$freq MHz",
                                    onClick = {
                                        targetPrimeFreq?.let { targetFreq ->
                                            viewModel.updateFreq(targetFreq, freq, "prime")
                                        }
                                        targetPrimeFreq = null
                                    },
                                )
                            }
                        }
                    } else {
                        Text("No available frequencies found.")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { targetPrimeFreq = null },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text("Close")
                    }
                },
            )
        }

        if (openACG) {
            AlertDialog(
                onDismissRequest = { openACG = false },
                title = { Text("Available governor") },
                text = {
                    if (primeClusterState.availableGov.isNotEmpty()) {
                        LazyColumn {
                            items(primeClusterState.availableGov) { gov ->
                                DialogTextButtonListItem(
                                    text = gov,
                                    onClick = {
                                        viewModel.updateGov(gov, "prime")
                                        openACG = false
                                    },
                                )
                            }
                        }
                    } else {
                        Text("No available governor found.")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { openACG = false },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text("Close")
                    }
                },
            )
        }
    }
}

@Composable
fun CPUBoostCard(viewModel: SoCViewModel, onDialogStateChange: (Boolean) -> Unit = {}) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    // CPU Input Boost Dialog
    var openCIBD by remember { mutableStateOf(false) }

    val hasCpuInputBoostMs by viewModel.hasCpuInputBoostMs.collectAsState()
    val cpuInputBoostMs by viewModel.cpuInputBoostMs.collectAsState()

    val hasCpuSchedBoostOnInput by viewModel.hasCpuSchedBoostOnInput.collectAsState()
    val cpuSchedBoostOnInput by viewModel.cpuSchedBoostOnInput.collectAsState()
    val cpuSchedBoostOnInputChecked = cpuSchedBoostOnInput == "1"

    LaunchedEffect(isExpanded) {
        viewModel.setActiveState("cpuBoost", isExpanded)
    }

    LaunchedEffect(openCIBD) {
        onDialogStateChange(openCIBD)
    }

    Card {
        TitleExpandable(
            leadingIcon = painterResource(R.drawable.ic_cpu),
            text = "CPU Boost",
            titleLarge = true,
            onClick = { isExpanded = !isExpanded },
            trailingIcon = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
        )

        AnimatedVisibility(isExpanded) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                if (hasCpuInputBoostMs) {
                    ButtonListItem(
                        title = "Input boost ms",
                        summary = "Time boost is held after input",
                        value = "$cpuInputBoostMs ms",
                        onClick = { openCIBD = true },
                    )
                }

                if (hasCpuSchedBoostOnInput) {
                    SwitchListItem(
                        title = "Sched boost on input",
                        summary = "Boost scheduler when receiving user input",
                        checked = cpuSchedBoostOnInputChecked,
                        onCheckedChange = { isChecked -> viewModel.updateCpuSchedBoostOnInput(isChecked) },
                    )
                }
            }
        }
    }

    if (openCIBD) {
        var value by remember { mutableStateOf(cpuInputBoostMs) }
        AlertDialog(
            onDismissRequest = { openCIBD = false },
            text = {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(text = "Input boost ms") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.updateCpuInputBoostMs(value)
                            openCIBD = false
                        },
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateCpuInputBoostMs(value)
                        openCIBD = false
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(text = "Change")
                }
            },
        )
    }
}

@Composable
fun GPUCard(viewModel: SoCViewModel, onDialogStateChange: (Boolean) -> Unit = {}) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    var targetGpuFreq by remember { mutableStateOf<String?>(null) }
    // AGG = Available GPU Governor
    var openAGG by remember { mutableStateOf(false) }
    // ABD = Adreno Boost Dialog
    var openABD by remember { mutableStateOf(false) }
    // DPD = Default Pwrlevel Dialog
    var openDPD by remember { mutableStateOf(false) }

    val gpuState by viewModel.gpuState.collectAsState()
    val hasDefaultPwrlevel by viewModel.hasDefaultPwrlevel.collectAsState()
    val hasAdrenoBoost by viewModel.hasAdrenoBoost.collectAsState()
    val hasGPUThrottling by viewModel.hasGPUThrottling.collectAsState()
    val gpuThrottlingStatus = remember(gpuState.gpuThrottling) { gpuState.gpuThrottling == "1" }

    LaunchedEffect(isExpanded) {
        viewModel.setActiveState("gpu", isExpanded)
    }

    LaunchedEffect(targetGpuFreq, openAGG, openABD, openDPD) {
        onDialogStateChange(targetGpuFreq != null || openAGG || openABD || openDPD)
    }

    Card {
        TitleExpandable(
            leadingIcon = painterResource(R.drawable.ic_video_card),
            text = "GPU",
            titleLarge = true,
            onClick = { isExpanded = !isExpanded },
            trailingIcon = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
        )

        AnimatedVisibility(isExpanded) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ButtonListItem(
                    title = "Minimum frequency",
                    summary = "The lowest speed the GPU can run at",
                    value = gpuState.minFreq,
                    isFreq = true,
                    onClick = { targetGpuFreq = "min" },
                )

                ButtonListItem(
                    title = "Maximum frequency",
                    summary = "The highest speed the GPU can run at",
                    value = gpuState.maxFreq,
                    isFreq = true,
                    onClick = { targetGpuFreq = "max" },
                )

                ButtonListItem(
                    title = "Governor",
                    summary = "Controls how the CPU scales between min and max frequencies",
                    value = gpuState.gov,
                    onClick = { openAGG = true },
                )

                if (hasDefaultPwrlevel) {
                    ButtonListItem(
                        title = "Default pwrlevel",
                        summary = "The lower the level, the higher the performance. Set it to 0 for the highest performance",
                        value = gpuState.defaultPwrlevel,
                        onClick = { openDPD = true },
                    )
                }

                if (hasAdrenoBoost) {
                    ButtonListItem(
                        title = "Adreno boost",
                        summary = "Boosts GPU performance for a short period",
                        value = remember(gpuState.adrenoBoost) {
                            when (gpuState.adrenoBoost) {
                                "0" -> "Off"
                                "1" -> "Low"
                                "2" -> "Medium"
                                "3" -> "High"
                                else -> "Unknown"
                            }
                        },
                        onClick = { openABD = true },
                    )
                }

                if (hasGPUThrottling) {
                    SwitchListItem(
                        title = "GPU throttling",
                        summary = "Reduces GPU performance to prevent overheating",
                        checked = gpuThrottlingStatus,
                        onCheckedChange = { isChecked ->
                            viewModel.updateGPUThrottling(isChecked)
                        },
                    )
                }
            }
        }

        if (targetGpuFreq != null) {
            AlertDialog(
                onDismissRequest = { targetGpuFreq = null },
                title = { Text("Available frequencies") },
                text = {
                    if (gpuState.availableFreq.isNotEmpty()) {
                        LazyColumn {
                            items(gpuState.availableFreq.sortedBy { it.toInt() }) { freq ->
                                DialogTextButtonListItem(
                                    text = "$freq MHz",
                                    onClick = {
                                        targetGpuFreq?.let { targetFreq ->
                                            viewModel.updateFreq(targetFreq, freq, "gpu")
                                        }
                                        targetGpuFreq = null
                                    },
                                )
                            }
                        }
                    } else {
                        Text("No available frequencies found.")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { targetGpuFreq = null },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text("Close")
                    }
                },
            )
        }

        if (openAGG) {
            AlertDialog(
                onDismissRequest = { openAGG = false },
                title = { Text("Available governor") },
                text = {
                    if (gpuState.availableGov.isNotEmpty()) {
                        LazyColumn {
                            items(gpuState.availableGov) { gov ->
                                DialogTextButtonListItem(
                                    text = gov,
                                    onClick = {
                                        viewModel.updateGov(gov, "gpu")
                                        openAGG = false
                                    },
                                )
                            }
                        }
                    } else {
                        Text("No available governor found.")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { openAGG = false },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text("Close")
                    }
                },
            )
        }

        if (openDPD) {
            var value by remember { mutableStateOf(gpuState.defaultPwrlevel) }
            AlertDialog(
                onDismissRequest = { openDPD = false },
                text = {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text("Default pwrlevel") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.updateDefaultPwrlevel(value)
                                openDPD = false
                            },
                        ),
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.updateDefaultPwrlevel(value)
                            openDPD = false
                        },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text(text = "Change")
                    }
                },
            )
        }

        if (openABD) {
            AlertDialog(
                onDismissRequest = { openABD = false },
                title = { Text("Adreno boost") },
                text = {
                    Column {
                        DialogTextButtonListItem(
                            text = "Off",
                            onClick = {
                                viewModel.updateAdrenoBoost("0")
                                openABD = false
                            },
                        )
                        DialogTextButtonListItem(
                            text = "Low",
                            onClick = {
                                viewModel.updateAdrenoBoost("1")
                                openABD = false
                            },
                        )
                        DialogTextButtonListItem(
                            text = "Medium",
                            onClick = {
                                viewModel.updateAdrenoBoost("2")
                                openABD = false
                            },
                        )
                        DialogTextButtonListItem(
                            text = "High",
                            onClick = {
                                viewModel.updateAdrenoBoost("3")
                                openABD = false
                            },
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { openABD = false },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text("Close")
                    }
                },
            )
        }
    }
}
