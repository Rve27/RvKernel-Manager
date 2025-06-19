package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.ui.navigation.PinnedTopAppBar
import com.rve.rvkernelmanager.ui.viewmodel.MiscViewModel
import com.rve.rvkernelmanager.utils.MiscUtils
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiscScreen(
    viewModel: MiscViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
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
    val thermalSconfig by viewModel.thermalSconfig.collectAsState()
    val hasThermalSconfig by viewModel.hasThermalSconfig.collectAsState()
    val thermalSconfigStatus = remember(thermalSconfig) { thermalSconfig == "10" }

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

            HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))

            if (hasThermalSconfig) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Unlock CPU frequency",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = thermalSconfigStatus,
                        onCheckedChange = { isChecked ->
                            viewModel.updateThermalSconfig(isChecked)
                        }
                    )
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = MiscUtils.SWAPPINESS.substringAfterLast("/"),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { viewModel.showSwappinessDialog() }) {
                        Text(text = swappiness)
                    }
                }
            }

	    if (hasPrintk) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = MiscUtils.PRINTK.substringAfterLast("/"),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { viewModel.showPrintkDialog() }) {
                        Text(text = printk)
                    }
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
                        label = { Text(MiscUtils.SWAPPINESS.substringAfterLast("/")) },
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
