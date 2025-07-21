/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.animation.core.*
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.utils.*
import com.rve.rvkernelmanager.ui.component.*
import com.rve.rvkernelmanager.ui.navigation.*
import com.rve.rvkernelmanager.ui.viewmodel.KernelParameterViewModel
import com.rve.rvkernelmanager.preference.BlurPreference

@Composable
fun KernelParameterScreen(
    viewModel: KernelParameterViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner,
    navController: NavController
) {
    val context = LocalContext.current
    val blurPreference = remember { BlurPreference.getInstance(context) }
    val blurEnabled by blurPreference.blurEnabled.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var isDialogOpen by remember { mutableStateOf(false) }

    val hasSchedAutogroup by viewModel.hasSchedAutogroup.collectAsState()
    val hasPrintk by viewModel.hasPrintk.collectAsState()
    val hasTcpCongestionAlgorithm by viewModel.hasTcpCongestionAlgorithm.collectAsState()

    val hasZramSize by viewModel.hasZramSize.collectAsState()
    val hasZramCompAlgorithm by viewModel.hasZramCompAlgorithm.collectAsState()

    val pullToRefreshState = remember {
	object : PullToRefreshState {
            private val anim = Animatable(0f, Float.VectorConverter)

            override val distanceFraction
                get() = anim.value

            override val isAnimating: Boolean
                get() = anim.isRunning

            override suspend fun animateToThreshold() {
                anim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
            }

            override suspend fun animateToHidden() {
                anim.animateTo(0f)
            }

            override suspend fun snapTo(targetValue: Float) {
                anim.snapTo(targetValue)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.loadKernelParameter()
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
	modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).then(if (isDialogOpen && blurEnabled) Modifier.blur(4.dp) else Modifier)
    ) { innerPadding ->
        PullToRefreshBox(
	    modifier = Modifier.padding(innerPadding),
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refresh() },
	    state = pullToRefreshState,
	    indicator = {
                PullToRefreshDefaults.LoadingIndicator(
		    state = pullToRefreshState,
                    isRefreshing = viewModel.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
		if (hasSchedAutogroup || hasPrintk || hasTcpCongestionAlgorithm) {
		    item {
		        Spacer(Modifier.height(16.dp))
                        KernelParameterCard(viewModel = viewModel, onDialogStateChange = { isOpen -> isDialogOpen = isOpen })
	            }
		}
		if (hasZramSize || hasZramCompAlgorithm) {
		    item {
			MemoryCard(viewModel = viewModel, onDialogStateChange = { isOpen -> isDialogOpen = isOpen })
		    }
		}
		item {
                    Spacer(Modifier)
		}
            }
        }
    }
}

@Composable
fun KernelParameterCard(viewModel: KernelParameterViewModel, onDialogStateChange: (Boolean) -> Unit = {}) {
    val schedAutogroup by viewModel.schedAutogroup.collectAsState()
    val hasSchedAutogroup by viewModel.hasSchedAutogroup.collectAsState()
    val schedAutogroupStatus = remember(schedAutogroup) { schedAutogroup == "1" }

    val printk by viewModel.printk.collectAsState()
    val hasPrintk by viewModel.hasPrintk.collectAsState()

    val tcpCongestionAlgorithm by viewModel.tcpCongestionAlgorithm.collectAsState()
    val hasTcpCongestionAlgorithm by viewModel.hasTcpCongestionAlgorithm.collectAsState()
    val availableTcpCongestionAlgorithm by viewModel.availableTcpCongestionAlgorithm.collectAsState()

    // PD = Printk Dialog
    var openPD by remember { mutableStateOf(false) }
    // TCD = TCP Congestion Dialog
    var openTCD by remember { mutableStateOf(false) }

    LaunchedEffect(openPD, openTCD) {
	onDialogStateChange(openPD || openTCD)
    }

    Card {
	CustomListItem(
	    title = "Kernel Parameter",
	    titleLarge = true
	)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        if (hasSchedAutogroup) {
	    SwitchListItem(
		titleSmall = true,
		title = "Sched auto group",
		bodySmall = true,
		summary = "Automatically groups related tasks for better CPU scheduling",
		checked = schedAutogroupStatus,
		onCheckedChange = { isChecked ->
		    viewModel.updateSchedAutogroup(isChecked)
		}
	    )
        }

	if (hasPrintk) {
            ButtonListItem(
                title = "printk",
		summary = "Controls kernel message logging level",
		value = printk,
		onClick = { openPD = true }
            )
        }

	if (hasTcpCongestionAlgorithm && availableTcpCongestionAlgorithm.isNotEmpty()) {
            ButtonListItem(
                title = "TCP congestion algorithm",
                summary = "Transmission Control Protocol is one of the core protocols of the Internet protocol suite (IP), and is so common that the entire suite is often called TCP/IP.",
                value = tcpCongestionAlgorithm,
                onClick = { openTCD = true }
            )
        }
    }

    if (openPD) {
        var newPrintkValue by remember { mutableStateOf(printk) }
        AlertDialog(
            onDismissRequest = { openPD = false },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPrintkValue,
			onValueChange = { newPrintkValue = it },
                        label = { Text(KernelUtils.PRINTK.substringAfterLast("/")) },
                        modifier = Modifier.fillMaxWidth(),
			keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updatePrintk(newPrintkValue)
                        openPD = false
                    },
		    shapes = ButtonDefaults.shapes()
                ) {
                    Text(text = "Change")
                }
            }
        )
    }

    if (openTCD) {
        AlertDialog(
            onDismissRequest = { openTCD = false },
            title = { Text("TCP congestion algorithm") },
            text = {
                Column {
                    availableTcpCongestionAlgorithm.forEach { algorithm ->
                        DialogTextButton(
                            text = algorithm,
                            onClick = {
                                viewModel.updateTcpCongestionAlgorithm(algorithm)
                                openTCD = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openTCD = false },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MemoryCard(viewModel: KernelParameterViewModel, onDialogStateChange: (Boolean) -> Unit = {}) {
    val zramSize by viewModel.zramSize.collectAsState()
    val hasZramSize by viewModel.hasZramSize.collectAsState()

    val zramCompAlgorithm by viewModel.zramCompAlgorithm.collectAsState()
    val hasZramCompAlgorithm by viewModel.hasZramCompAlgorithm.collectAsState()
    val availableZramCompAlgorithms by viewModel.availableZramCompAlgorithms.collectAsState()

    val swappiness by viewModel.swappiness.collectAsState()
    val hasSwappiness by viewModel.hasSwappiness.collectAsState()

    // ZD = ZRAM Dialog
    var openZD by remember { mutableStateOf(false) }
    // ZCD = ZRAM Compression Dialog
    var openZCD by remember { mutableStateOf(false) }
    // SD = Swappiness Dialog
    var openSD by remember { mutableStateOf(false) }

    LaunchedEffect(openZD, openZCD, openSD) {
	onDialogStateChange(openZD || openZCD || openSD)
    }

    Card {
	CustomListItem(
	    title = "Memory",
	    titleLarge = true
	)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

	if (hasZramSize || hasZramCompAlgorithm) {
	    CustomListItem(
		summary = "NOTE: It may take a few minutes to change the ZRAM size, etc."
	    )
	}

	if (hasZramSize) {
            ButtonListItem(
                title = "ZRAM size",
                summary = "Change the ZRAM size",
                value = zramSize,
                onClick = { openZD = true }
            )
        }

	if (hasZramCompAlgorithm && availableZramCompAlgorithms.isNotEmpty()) {
            ButtonListItem(
                title = "ZRAM compression algorithm",
                summary = "Different algorithms offer different compression ratios and performance",
                value = zramCompAlgorithm,
                onClick = { openZCD = true }
            )
        }

	if (hasSwappiness) {
	    ButtonListItem(
		title = "Swappiness",
		summary = "Controls how aggressively the system uses swap memory",
		value = "$swappiness%",
		onClick = { openSD = true }
	    )
	}
    }

    if (openZD) {
        val zramSizeOptions = listOf("1 GB", "2 GB", "3 GB", "4 GB", "5 GB", "6 GB")
        
        AlertDialog(
            onDismissRequest = { openZD = false },
            title = { Text("ZRAM size") },
            text = {
                Column {
                    zramSizeOptions.forEach { size ->
                        DialogTextButton(
			    text = size,
                            onClick = {
                                val sizeInGb = size.substringBefore(" GB").toInt()
                                viewModel.updateZramSize(sizeInGb)
                                openZD = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openZD = false },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (openZCD) {
        AlertDialog(
            onDismissRequest = { openZCD = false },
            title = { Text("ZRAM compression algorithm") },
            text = {
                Column {
                    availableZramCompAlgorithms.forEach { algorithm ->
                        DialogTextButton(
                            text = algorithm,
                            onClick = {
                                viewModel.updateZramCompAlgorithm(algorithm)
                                openZCD = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openZCD = false },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (openSD) {
        var newSwappinessValue by remember { mutableStateOf(swappiness) }
        AlertDialog(
            onDismissRequest = { openSD = false },
            text = {
                Column {
                    OutlinedTextField(
                        value = newSwappinessValue,
			onValueChange = { newSwappinessValue = it },
                        label = { Text("Swappiness") },
                        modifier = Modifier.fillMaxWidth(),
			keyboardOptions = KeyboardOptions.Default.copy(
			    keyboardType = KeyboardType.Number
			)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateSwappiness(newSwappinessValue)
                        openSD = false
                    },
		    shapes = ButtonDefaults.shapes()
                ) {
                    Text(text = "Change")
                }
            }
        )
    }
}
