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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.composables.core.rememberDialogState
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.ButtonListItem
import com.rve.rvkernelmanager.ui.components.CustomListItem
import com.rve.rvkernelmanager.ui.components.DialogTextButton
import com.rve.rvkernelmanager.ui.components.DialogUnstyled
import com.rve.rvkernelmanager.ui.components.MonitorListItem
import com.rve.rvkernelmanager.ui.components.PinnedTopAppBar
import com.rve.rvkernelmanager.ui.components.SwitchListItem
import com.rve.rvkernelmanager.ui.components.TitleExpandable
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationBar

@Composable
fun SoCScreen(viewModel: SoCViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                LittleClusterCard(viewModel = viewModel)
            }
            if (hasBigCluster) {
                item {
                    BigClusterCard(viewModel = viewModel)
                }
            }
            if (hasPrimeCluster) {
                item {
                    PrimeClusterCard(viewModel = viewModel)
                }
            }
            if (hasCpuInputBoostMs || hasCpuSchedBoostOnInput) {
                item {
                    CPUBoostCard(viewModel = viewModel)
                }
            }
            item {
                GPUCard(viewModel = viewModel)
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
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
                    defaultElevation = 16.dp,
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
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
                    defaultElevation = 16.dp,
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
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
fun LittleClusterCard(viewModel: SoCViewModel) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    // AMXF = Available Max Frequencies
    val openAMXF = rememberDialogState(initiallyVisible = false)
    // AMNF = Available Min Frequencies
    val openAMNF = rememberDialogState(initiallyVisible = false)
    // ACG = Available CPU Governor
    val openACG = rememberDialogState(initiallyVisible = false)

    val cpu0State by viewModel.cpu0State.collectAsState()
    val hasBigCluster by viewModel.hasBigCluster.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
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
                    onClick = { openAMNF.visible = true },
                )

                ButtonListItem(
                    title = "Maximum frequency",
                    summary = if (hasBigCluster)
                        "The highest speed the Little Cluster can run at"
                    else
                        "The highest speed the CPU can run at",
                    value = cpu0State.maxFreq,
                    isFreq = true,
                    onClick = { openAMXF.visible = true },
                )

                ButtonListItem(
                    title = "Governor",
                    summary = if (hasBigCluster)
                        "Controls how the Little Cluster scales between min and max frequencies"
                    else
                        "Controls how the CPU scales between min and max frequencies",
                    value = cpu0State.gov,
                    onClick = { openACG.visible = true },
                )
            }
        }

        DialogUnstyled(
            state = openAMNF,
            title = "Available frequencies",
            text = {
                if (cpu0State.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(cpu0State.availableFreq) { freq ->
                            DialogTextButton(
                                text = "$freq MHz",
                                onClick = {
                                    viewModel.updateFreq("min", freq, "little")
                                    openAMNF.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available frequencies found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openAMNF.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )

        DialogUnstyled(
            state = openAMXF,
            title = "Available frequencies",
            text = {
                if (cpu0State.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(cpu0State.availableFreq) { freq ->
                            DialogTextButton(
                                text = "$freq MHz",
                                onClick = {
                                    viewModel.updateFreq("max", freq, "little")
                                    openAMXF.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available frequencies found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openAMXF.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )

        DialogUnstyled(
            state = openACG,
            title = "Available governor",
            text = {
                if (cpu0State.availableGov.isNotEmpty()) {
                    LazyColumn {
                        items(cpu0State.availableGov) { gov ->
                            DialogTextButton(
                                text = gov,
                                onClick = {
                                    viewModel.updateGov(gov, "little")
                                    openACG.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available governor found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openACG.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )
    }
}

@Composable
fun BigClusterCard(viewModel: SoCViewModel) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    // Available Max Frequencies
    val openAMXF = rememberDialogState(initiallyVisible = false)
    // Available Min Frequencies
    val openAMNF = rememberDialogState(initiallyVisible = false)
    // ACG = Available CPU Governor
    val openACG = rememberDialogState(initiallyVisible = false)

    val bigClusterState by viewModel.bigClusterState.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
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
                    onClick = { openAMNF.visible = true },
                )

                ButtonListItem(
                    title = "Maximum frequency",
                    summary = "The highest speed the Big Cluster can run at",
                    value = bigClusterState.maxFreq,
                    isFreq = true,
                    onClick = { openAMXF.visible = true },
                )

                ButtonListItem(
                    title = "Governor",
                    summary = "Controls how the Big Cluster scales between min and max frequencies",
                    value = bigClusterState.gov,
                    onClick = { openACG.visible = true },
                )
            }
        }

        DialogUnstyled(
            state = openAMNF,
            title = "Available frequencies",
            text = {
                if (bigClusterState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(bigClusterState.availableFreq) { freq ->
                            DialogTextButton(
                                text = "$freq MHz",
                                onClick = {
                                    viewModel.updateFreq("min", freq, "big")
                                    openAMNF.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available frequencies found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openAMNF.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )

        DialogUnstyled(
            state = openAMXF,
            title = "Available frequencies",
            text = {
                if (bigClusterState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(bigClusterState.availableFreq) { freq ->
                            DialogTextButton(
                                text = "$freq MHz",
                                onClick = {
                                    viewModel.updateFreq("max", freq, "big")
                                    openAMXF.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available frequencies found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openAMXF.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )

        DialogUnstyled(
            state = openACG,
            title = "Available governor",
            text = {
                if (bigClusterState.availableGov.isNotEmpty()) {
                    LazyColumn {
                        items(bigClusterState.availableGov) { gov ->
                            DialogTextButton(
                                text = gov,
                                onClick = {
                                    viewModel.updateGov(gov, "big")
                                    openACG.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available governor found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openACG.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )
    }
}

@Composable
fun PrimeClusterCard(viewModel: SoCViewModel) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    // Available Max Frequencies
    val openAMXF = rememberDialogState(initiallyVisible = false)
    // Available Min Frequencies
    val openAMNF = rememberDialogState(initiallyVisible = false)
    // ACG = Available CPU Governor
    val openACG = rememberDialogState(initiallyVisible = false)

    val primeClusterState by viewModel.primeClusterState.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
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
                    onClick = { openAMNF.visible = true },
                )

                ButtonListItem(
                    title = "Maximum frequency",
                    summary = "The highest speed the Prime Cluster can run at",
                    value = primeClusterState.maxFreq,
                    isFreq = true,
                    onClick = { openAMXF.visible = true },
                )

                ButtonListItem(
                    title = "Governor",
                    summary = "Controls how the Prime Cluster scales between min and max frequencies",
                    value = primeClusterState.gov,
                    onClick = { openACG.visible = true },
                )
            }
        }

        DialogUnstyled(
            state = openAMNF,
            title = "Available frequencies",
            text = {
                if (primeClusterState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(primeClusterState.availableFreq) { freq ->
                            DialogTextButton(
                                text = "$freq MHz",
                                onClick = {
                                    viewModel.updateFreq("min", freq, "prime")
                                    openAMNF.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available frequencies found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openAMNF.visible = true },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )

        DialogUnstyled(
            state = openAMXF,
            title = "Available frequencies",
            text = {
                if (primeClusterState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(primeClusterState.availableFreq) { freq ->
                            DialogTextButton(
                                text = "$freq MHz",
                                onClick = {
                                    viewModel.updateFreq("max", freq, "prime")
                                    openAMXF.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available frequencies found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openAMXF.visible = true },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )

        DialogUnstyled(
            state = openACG,
            title = "Available governor",
            text = {
                if (primeClusterState.availableGov.isNotEmpty()) {
                    LazyColumn {
                        items(primeClusterState.availableGov) { gov ->
                            DialogTextButton(
                                text = gov,
                                onClick = {
                                    viewModel.updateGov(gov, "prime")
                                    openACG.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available governor found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openACG.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )
    }
}

@Composable
fun CPUBoostCard(viewModel: SoCViewModel) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    // CPU Input Boost Dialog
    val openCIBD = rememberDialogState(initiallyVisible = false)

    val hasCpuInputBoostMs by viewModel.hasCpuInputBoostMs.collectAsState()
    val cpuInputBoostMs by viewModel.cpuInputBoostMs.collectAsState()
    var cpuInputBoostMsValue by remember { mutableStateOf(cpuInputBoostMs) }

    val hasCpuSchedBoostOnInput by viewModel.hasCpuSchedBoostOnInput.collectAsState()
    val cpuSchedBoostOnInput by viewModel.cpuSchedBoostOnInput.collectAsState()
    val cpuSchedBoostOnInputChecked = cpuSchedBoostOnInput == "1"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
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
                        onClick = { openCIBD.visible = true },
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

    DialogUnstyled(
        state = openCIBD,
        text = {
            OutlinedTextField(
                value = cpuInputBoostMsValue,
                onValueChange = { cpuInputBoostMsValue = it },
                label = { Text(text = "Input boost ms") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.updateCpuInputBoostMs(cpuInputBoostMsValue)
                        openCIBD.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.updateCpuInputBoostMs(cpuInputBoostMsValue)
                    openCIBD.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openCIBD.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
}

@Composable
fun GPUCard(viewModel: SoCViewModel) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    // Available Max Frequencies
    val openAMXF = rememberDialogState(initiallyVisible = false)
    // Available Min Frequencies
    val openAMNF = rememberDialogState(initiallyVisible = false)
    // AGG = Available GPU Governor
    val openAGG = rememberDialogState(initiallyVisible = false)
    // ABD = Adreno Boost Dialog
    val openABD = rememberDialogState(initiallyVisible = false)
    // DPD = Default Pwrlevel Dialog
    val openDPD = rememberDialogState(initiallyVisible = false)

    val gpuState by viewModel.gpuState.collectAsState()
    val hasDefaultPwrlevel by viewModel.hasDefaultPwrlevel.collectAsState()
    var defaultPwrlevel by remember { mutableStateOf(gpuState.defaultPwrlevel) }
    val hasAdrenoBoost by viewModel.hasAdrenoBoost.collectAsState()
    val hasGPUThrottling by viewModel.hasGPUThrottling.collectAsState()
    val gpuThrottlingStatus = remember(gpuState.gpuThrottling) { gpuState.gpuThrottling == "1" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
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
                    onClick = { openAMNF.visible = true },
                )

                ButtonListItem(
                    title = "Maximum frequency",
                    summary = "The highest speed the GPU can run at",
                    value = gpuState.maxFreq,
                    isFreq = true,
                    onClick = { openAMXF.visible = true },
                )

                ButtonListItem(
                    title = "Governor",
                    summary = "Controls how the CPU scales between min and max frequencies",
                    value = gpuState.gov,
                    onClick = { openAGG.visible = true },
                )

                if (hasDefaultPwrlevel) {
                    AnimatedVisibility(hasDefaultPwrlevel) {
                        ButtonListItem(
                            title = "Default pwrlevel",
                            summary = "The lower the level, the higher the performance. Set it to 0 for the highest performance",
                            value = gpuState.defaultPwrlevel,
                            onClick = { openDPD.visible = true },
                        )
                    }
                }

                if (hasAdrenoBoost) {
                    AnimatedVisibility(hasAdrenoBoost) {
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
                            onClick = { openABD.visible = true },
                        )
                    }
                }

                if (hasGPUThrottling) {
                    AnimatedVisibility(hasGPUThrottling) {
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
        }

        DialogUnstyled(
            state = openAMNF,
            title = "Available frequencies",
            text = {
                if (gpuState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(gpuState.availableFreq.sortedBy { it.toInt() }) { freq ->
                            DialogTextButton(
                                text = "$freq MHz",
                                onClick = {
                                    viewModel.updateFreq("min", freq, "gpu")
                                    openAMNF.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available frequencies found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openAMNF.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )

        DialogUnstyled(
            state = openAMXF,
            title = "Available frequencies",
            text = {
                if (gpuState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(gpuState.availableFreq.sortedBy { it.toInt() }) { freq ->
                            DialogTextButton(
                                text = "$freq MHz",
                                onClick = {
                                    viewModel.updateFreq("max", freq, "gpu")
                                    openAMXF.visible = false
                                },
                            )
                        }
                    }
                } else {
                    Text("No available frequencies found.")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openAMXF.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )

        DialogUnstyled(
            state = openAGG,
            title = "Available governor",
            text = {
                if (gpuState.availableGov.isNotEmpty()) {
                    LazyColumn {
                        items(gpuState.availableGov) { gov ->
                            DialogTextButton(
                                text = gov,
                                onClick = {
                                    viewModel.updateGov(gov, "gpu")
                                    openAGG.visible = false
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
                    onClick = { openAGG.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )

        DialogUnstyled(
            state = openDPD,
            text = {
                OutlinedTextField(
                    value = defaultPwrlevel,
                    onValueChange = { defaultPwrlevel = it },
                    label = { Text("Default pwrlevel") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.updateDefaultPwrlevel(defaultPwrlevel)
                            openDPD.visible = false
                        },
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateDefaultPwrlevel(defaultPwrlevel)
                        openDPD.visible = false
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Change")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDPD.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Cancel")
                }
            },
        )

        DialogUnstyled(
            state = openABD,
            title = "Adreno boost",
            text = {
                Column {
                    DialogTextButton(
                        text = "Off",
                        onClick = {
                            viewModel.updateAdrenoBoost("0")
                            openABD.visible = false
                        },
                    )
                    DialogTextButton(
                        text = "Low",
                        onClick = {
                            viewModel.updateAdrenoBoost("1")
                            openABD.visible = false
                        },
                    )
                    DialogTextButton(
                        text = "Medium",
                        onClick = {
                            viewModel.updateAdrenoBoost("2")
                            openABD.visible = false
                        },
                    )
                    DialogTextButton(
                        text = "High",
                        onClick = {
                            viewModel.updateAdrenoBoost("3")
                            openABD.visible = false
                        },
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openABD.visible = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )
    }
}
