package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rve.rvkernelmanager.ui.navigation.*
import com.rve.rvkernelmanager.ui.viewmodel.MiscViewModel
import com.rve.rvkernelmanager.utils.Utils
import com.rve.rvkernelmanager.utils.MiscUtils
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiscScreen(
    viewModel: MiscViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner,
    navController: NavController
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

    Scaffold(
        topBar = {
	    PinnedTopAppBar(scrollBehavior = scrollBehavior)
	},
	bottomBar = {
	    BottomNavigationBar(navController = navController)
	},
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refresh() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
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
    var expanded by remember { mutableStateOf(false) }

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

    Card(
        shape = CardDefaults.shape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Miscellaneous",
                style = MaterialTheme.typography.titleLarge
            )
	}
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        if (hasThermalSconfig) {
            Row(
                modifier = Modifier
		    .clickable(
			onClick = { expanded = true }
		    )
		    .padding(16.dp)
            ) {
		Column(
		    modifier = Modifier.weight(1f)
		) {
                    Text(
                        text = "Thermal profiles",
                        style = MaterialTheme.typography.titleSmall
                    )
		    Text(
		        text = "Adjust thermal profiles for optimum performance",
		        style = MaterialTheme.typography.bodySmall,
			modifier = Modifier.alpha(0.7f)
		    )
		}
		Box {
	            Button(
			onClick = {
			    expanded = true
			}
		    ) {
			Text(
			    text = remember(thermalSconfig) {
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
			    }
			)
		    }
		    DropdownMenu(
		        expanded = expanded,
		        onDismissRequest = {
		            expanded = false
	                },
			offset = DpOffset((5).dp, 0.dp)
	            ) {
			DropdownMenuItem(
			    text = {
				Text("Default")
			    },
			    onClick = {
				viewModel.updateThermalSconfig("0")
				expanded = false
			    }
			)
			DropdownMenuItem(
			    text = {
				Text("Benchmark")
			    },
			    onClick = {
				viewModel.updateThermalSconfig("10")
				expanded = false
			    }
			)
			DropdownMenuItem(
			    text = {
				Text("Browser")
			    },
			    onClick = {
				viewModel.updateThermalSconfig("11")
				expanded = false
			    }
			)
			DropdownMenuItem(
			    text = {
				Text("Camera")
			    },
			    onClick = {
				viewModel.updateThermalSconfig("12")
				expanded = false
			    }
			)
			DropdownMenuItem(
			    text = {
				Text("Dialer")
			    },
			    onClick = {
				viewModel.updateThermalSconfig("8")
				expanded = false
			    }
			)
			DropdownMenuItem(
			    text = {
				Text("Gaming")
			    },
			    onClick = {
				viewModel.updateThermalSconfig("13")
				expanded = false
			    }
			)
			DropdownMenuItem(
			    text = {
				Text("Streaming")
			    },
			    onClick = {
				viewModel.updateThermalSconfig("14")
				expanded = false
			    }
			)
		    }
		}
            }
        }

        if (hasSchedAutogroup) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    MiscUtils.SCHED_AUTOGROUP.substringAfterLast("/"),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = schedAutogroupStatus,
                    onCheckedChange = { isChecked ->
                        viewModel.updateSchedAutogroup(isChecked)
                    }
                )
            }
        }

        if (hasSwappiness) {
            Row(
		modifier = Modifier
		    .clickable(
			onClick = { viewModel.showSwappinessDialog() }
		    )
		    .padding(16.dp)
            ) {
		Column(
		    modifier = Modifier.weight(1f)
		) {
                    Text(
                        text = "Swappiness",
                        style = MaterialTheme.typography.titleSmall
                    )
		    Text(
			text = "Controls how aggressively the system uses swap memory",
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier.alpha(0.7f)
		    )
		}
                Button(
		    onClick = { viewModel.showSwappinessDialog() }
		) {
                    Text(
			text = "$swappiness%"
		    )
                }
            }
        }

	if (hasPrintk) {
            Row(
		modifier = Modifier
		    .clickable(
			onClick = { viewModel.showPrintkDialog() }
		    )
		    .padding(16.dp)
            ) {
		Column(
		    modifier = Modifier.weight(1f)
		) {
                    Text(
                        text = "printk",
                        style = MaterialTheme.typography.titleSmall
                    )
		    Text(
			text = "Controls kernel message logging level",
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier.alpha(0.7f)
		    )
		}
                Button(
		    onClick = { viewModel.showPrintkDialog() }
		) {
                    Text(
			text = printk
		    )
                }
            }
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
