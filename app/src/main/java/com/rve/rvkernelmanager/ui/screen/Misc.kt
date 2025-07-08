package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.ui.navigation.*
import com.rve.rvkernelmanager.ui.component.*
import com.rve.rvkernelmanager.ui.viewmodel.MiscViewModel
import com.rve.rvkernelmanager.utils.*
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiscScreen(
    viewModel: MiscViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.loadMiscData()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold {
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refresh() }
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
		state = rememberLazyListState()
            ) {
		item {
		    Spacer(Modifier.height(16.dp))
                    MiscCard(viewModel)
		}
		item {
                    Spacer(Modifier)
		}
            }
        }
    }
}

@Composable
fun MiscCard(viewModel: MiscViewModel) {
    var openTPD by remember { mutableStateOf(false) }

    val thermalSconfig by viewModel.thermalSconfig.collectAsState()
    val hasThermalSconfig by viewModel.hasThermalSconfig.collectAsState()    

    val schedAutogroup by viewModel.schedAutogroup.collectAsState()
    val hasSchedAutogroup by viewModel.hasSchedAutogroup.collectAsState()
    val schedAutogroupStatus = remember(schedAutogroup) { schedAutogroup == "1" }

    val swappiness by viewModel.swappiness.collectAsState()
    val hasSwappiness by viewModel.hasSwappiness.collectAsState()
    val showSwappinessDialog by viewModel.showSwappinessDialog.collectAsState()

    val printk by viewModel.printk.collectAsState()
    val hasPrintk by viewModel.hasPrintk.collectAsState()
    val showPrintkDialog by viewModel.showPrintkDialog.collectAsState()

    Card {
	CustomListItem(
	    title = "Miscellaneous",
	    titleLarge = true
	)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        if (hasThermalSconfig) {
            ButtonListItem(
                title = "Thermal profiles",
		summary = "Adjust thermal profiles for optimum performance",
		value = remember(thermalSconfig) {
		    when (thermalSconfig) {
			"0" -> "Default"
			"10" -> "Benchmark"
			"11" -> "Browser"
			"12" -> "Camera"
			"8" -> "Dialer"
			"13" -> "Gaming"
			"14" -> "Streaming"
			else -> thermalSconfig
		    }
		},
		onClick = { openTPD = true }
	    )

	    if (openTPD) {
		AlertDialog(
		    onDismissRequest = { openTPD = false },
		    title = {
			Text(
			    text = "Thermal profiles"
			)
		    },
		    text = {
			Column(
			    modifier = Modifier.fillMaxWidth()
			) {
			    DialogTextButton(
				icon = painterResource(R.drawable.ic_mode_cool),
				text = "Default",
				onClick = {
				    viewModel.updateThermalSconfig("0")
				    openTPD = false
				}
			    )
			    DialogTextButton(
				icon = Icons.Default.Speed,
				text = "Benchmark",
				onClick = {
				    viewModel.updateThermalSconfig("10")
				    openTPD = false
				}
			    )
			    DialogTextButton(
				icon = Icons.Default.Language,
				text = "Browser",
				onClick = {
				    viewModel.updateThermalSconfig("11")
				    openTPD = false
				}
			    )
			    DialogTextButton(
                                icon = Icons.Default.PhotoCamera,
                                text = "Camera",
                                onClick = {
                                    viewModel.updateThermalSconfig("12")
                                    openTPD = false
                                }
                            )
			    DialogTextButton(
                                icon = Icons.Default.Call,
                                text = "Dialer",
                                onClick = {
                                    viewModel.updateThermalSconfig("8")
                                    openTPD = false
                                }
                            )
			    DialogTextButton(
                                icon = Icons.Default.SportsEsports,
                                text = "Gaming",
                                onClick = {
                                    viewModel.updateThermalSconfig("13")
                                    openTPD = false
                                }
                            )
			    DialogTextButton(
                                icon = Icons.Default.Videocam,
                                text = "Streaming",
                                onClick = {
                                    viewModel.updateThermalSconfig("14")
                                    openTPD = false
                                }
                            )
			}
		    },
		    confirmButton = {
			TextButton(
			    onClick = { openTPD = false }
			) {
			    Text("Close")
			}
		    }
		)
            }
        }

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
		onClick = { viewModel.showSwappinessDialog() }
	    )
	}

	if (hasPrintk) {
            ButtonListItem(
                title = "printk",
		summary = "Controls kernel message logging level",
		value = printk,
		onClick = { viewModel.showPrintkDialog() }
            )
        }
    }

    if (showSwappinessDialog) {
        var newSwappinessValue by remember { mutableStateOf(swappiness) }
        AlertDialog(
            onDismissRequest = { viewModel.hideSwappinessDialog() },
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
                        viewModel.hideSwappinessDialog()
                    }
                ) {
                    Text(text = "Change")
                }
            }
        )
    }

    if (showPrintkDialog) {
        var newPrintkValue by remember { mutableStateOf(printk) }
        AlertDialog(
            onDismissRequest = { viewModel.hidePrintkDialog() },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPrintkValue,
			onValueChange = { newPrintkValue = it },
                        label = { Text(MiscUtils.PRINTK.substringAfterLast("/")) },
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
                        viewModel.hidePrintkDialog()
                    }
                ) {
                    Text(text = "Change")
                }
            }
        )
    }
}
