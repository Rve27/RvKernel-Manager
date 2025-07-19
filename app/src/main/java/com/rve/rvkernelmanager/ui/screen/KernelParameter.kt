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

@Composable
fun KernelParameterScreen(
    viewModel: KernelParameterViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner,
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
	modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
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
		item {
		    Spacer(Modifier.height(16.dp))
                    KernelParameterCard(viewModel)
	        }
		item {
                    Spacer(Modifier)
		}
            }
        }
    }
}

@Composable
fun KernelParameterCard(viewModel: KernelParameterViewModel) {
    val schedAutogroup by viewModel.schedAutogroup.collectAsState()
    val hasSchedAutogroup by viewModel.hasSchedAutogroup.collectAsState()
    val schedAutogroupStatus = remember(schedAutogroup) { schedAutogroup == "1" }

    val swappiness by viewModel.swappiness.collectAsState()
    val hasSwappiness by viewModel.hasSwappiness.collectAsState()
    // SD = Swappiness Dialog
    var openSD by remember { mutableStateOf(false) }

    val printk by viewModel.printk.collectAsState()
    val hasPrintk by viewModel.hasPrintk.collectAsState()
    // PD = Printk Dialog
    var openPD by remember { mutableStateOf(false) }

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

        if (hasSwappiness) {
	    ButtonListItem(
		title = "Swappiness",
		summary = "Controls how aggressively the system uses swap memory",
		value = "$swappiness%",
		onClick = { openSD = true }
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
}
