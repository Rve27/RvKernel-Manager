/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.*
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
import androidx.compose.ui.text.input.*
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
import com.rve.rvkernelmanager.preference.SettingsPreference

@Composable
fun KernelParameterScreen(
    viewModel: KernelParameterViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner,
    navController: NavController
) {
    val context = LocalContext.current
    val settingsPreference = remember { SettingsPreference.getInstance(context) }
    val blurEnabled by settingsPreference.blurEnabled.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var isDialogOpen by remember { mutableStateOf(false) }

    val kernelParameters by viewModel.kernelParameters.collectAsState()

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
		if (kernelParameters.hasSchedAutogroup || kernelParameters.hasPrintk || kernelParameters.hasTcpCongestionAlgorithm) {
		    item {
		        Spacer(Modifier.height(16.dp))
                        KernelParameterCard(viewModel = viewModel, onDialogStateChange = { isOpen -> isDialogOpen = isOpen })
	            }
		}
		if (kernelParameters.hasZramSize || kernelParameters.hasZramCompAlgorithm) {
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
    val kernelParameters by viewModel.kernelParameters.collectAsState()
    val schedAutogroupStatus = remember(kernelParameters.schedAutogroup) { kernelParameters.schedAutogroup == "1" }

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

        if (kernelParameters.hasSchedAutogroup) {
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

	if (kernelParameters.hasPrintk) {
            ButtonListItem(
                title = "printk",
		summary = "Controls kernel message logging level",
		value = kernelParameters.printk,
		onClick = { openPD = true }
            )
        }

	if (kernelParameters.hasTcpCongestionAlgorithm && kernelParameters.availableTcpCongestionAlgorithm.isNotEmpty()) {
            ButtonListItem(
                title = "TCP congestion algorithm",
                summary = "Transmission Control Protocol is one of the core protocols of the Internet protocol suite (IP), and is so common that the entire suite is often called TCP/IP.",
                value = kernelParameters.tcpCongestionAlgorithm,
                onClick = { openTCD = true }
            )
        }
    }

    if (openPD) {
        var value by remember { mutableStateOf(kernelParameters.printk) }
        AlertDialog(
            onDismissRequest = { openPD = false },
            text = {
                OutlinedTextField(
                    value = value,
		    onValueChange = { value = it },
                    label = { Text(KernelUtils.PRINTK.substringAfterLast("/")) },
		    singleLine = true,
		    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
			imeAction = ImeAction.Done
                    ),
		    keyboardActions = KeyboardActions(
			onDone = {
			    viewModel.updatePrintk(value)
			    openPD = false
			}
		    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updatePrintk(value)
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
                    kernelParameters.availableTcpCongestionAlgorithm.forEach { algorithm ->
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
    val kernelParameters by viewModel.kernelParameters.collectAsState()

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

	if (kernelParameters.hasZramSize || kernelParameters.hasZramCompAlgorithm) {
	    CustomListItem(
		summary = "NOTE: It may take a few minutes to change the ZRAM size, etc."
	    )
	}

	if (kernelParameters.hasZramSize) {
            ButtonListItem(
                title = "ZRAM size",
                summary = "Change the ZRAM size",
                value = kernelParameters.zramSize,
                onClick = { openZD = true }
            )
        }

	if (kernelParameters.hasZramCompAlgorithm && kernelParameters.availableZramCompAlgorithms.isNotEmpty()) {
            ButtonListItem(
                title = "ZRAM compression algorithm",
                summary = "Different algorithms offer different compression ratios and performance",
                value = kernelParameters.zramCompAlgorithm,
                onClick = { openZCD = true }
            )
        }

	if (kernelParameters.hasSwappiness) {
	    ButtonListItem(
		title = "Swappiness",
		summary = "Controls how aggressively the system uses swap memory",
		value = "${kernelParameters.swappiness}%",
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
                    kernelParameters.availableZramCompAlgorithms.forEach { algorithm ->
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
        var value by remember { mutableStateOf(kernelParameters.swappiness) }
        AlertDialog(
            onDismissRequest = { openSD = false },
            text = {
                OutlinedTextField(
                    value = value,
		    onValueChange = { value = it },
                    label = { Text("Swappiness") },
		    singleLine = true,
		    keyboardOptions = KeyboardOptions.Default.copy(
			keyboardType = KeyboardType.Number,
			imeAction = ImeAction.Done
		    ),
		    keyboardActions = KeyboardActions(
			onDone = {
			    viewModel.updateSwappiness(value)
			    openSD = false
			}
		    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateSwappiness(value)
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
