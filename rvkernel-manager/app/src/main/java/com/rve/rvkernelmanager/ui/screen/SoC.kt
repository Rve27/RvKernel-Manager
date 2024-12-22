package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.only
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.utils.MIN_FREQ_CPU0_PATH
import com.rve.rvkernelmanager.utils.MAX_FREQ_CPU0_PATH
import com.rve.rvkernelmanager.utils.AVAILABLE_FREQ_CPU0_PATH
import com.rve.rvkernelmanager.utils.AVAILABLE_GOV_CPU0_PATH
import com.rve.rvkernelmanager.utils.GOV_CPU0_PATH
import com.rve.rvkernelmanager.utils.MIN_FREQ_CPU4_PATH
import com.rve.rvkernelmanager.utils.MAX_FREQ_CPU4_PATH
import com.rve.rvkernelmanager.utils.AVAILABLE_FREQ_CPU4_PATH
import com.rve.rvkernelmanager.utils.AVAILABLE_BOOST_CPU4_PATH
import com.rve.rvkernelmanager.utils.AVAILABLE_GOV_CPU4_PATH
import com.rve.rvkernelmanager.utils.GOV_CPU4_PATH
import com.rve.rvkernelmanager.utils.setPermissions
import com.rve.rvkernelmanager.utils.testFile
import com.rve.rvkernelmanager.utils.readFile
import com.rve.rvkernelmanager.utils.writeFile
import com.rve.rvkernelmanager.utils.readFreqFile
import com.rve.rvkernelmanager.utils.writeFreqFile
import com.rve.rvkernelmanager.utils.readAvailableFreq
import com.rve.rvkernelmanager.utils.readAvailableFreqBoost
import com.rve.rvkernelmanager.utils.readAvailableGov
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoCScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
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
	    LittleClusterCard()

	    val hasBigCluster by remember { mutableStateOf(testFile(AVAILABLE_FREQ_CPU4_PATH)) }
	    if (hasBigCluster) {
	        BigClusterCard()
	    }
	    Spacer(Modifier)
	}
    }
}

@Composable
fun LittleClusterCard() {
    setPermissions(644, AVAILABLE_FREQ_CPU0_PATH)
    setPermissions(644, MIN_FREQ_CPU0_PATH)
    setPermissions(644, MAX_FREQ_CPU0_PATH)
    setPermissions(644, AVAILABLE_GOV_CPU0_PATH)
    setPermissions(644, GOV_CPU0_PATH)

    var minFreqCPU0 by remember { mutableStateOf(readFreqFile(MIN_FREQ_CPU0_PATH)) }
    var maxFreqCPU0 by remember { mutableStateOf(readFreqFile(MAX_FREQ_CPU0_PATH)) }
    var availableFreqCPU0 by remember { mutableStateOf(listOf<String>()) }
    var showAvailableFreqCPU0 by remember { mutableStateOf(false) }

    var govCPU0 by remember { mutableStateOf(readFile(GOV_CPU0_PATH)) }
    var availableGovCPU0 by remember { mutableStateOf(listOf<String>()) }
    var showAvailableGovCPU0 by remember { mutableStateOf(false) }

    var currentFileTarget by remember { mutableStateOf("") }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                minFreqCPU0 = readFreqFile(MIN_FREQ_CPU0_PATH)
                maxFreqCPU0 = readFreqFile(MAX_FREQ_CPU0_PATH)
                availableFreqCPU0 = readAvailableFreq(AVAILABLE_FREQ_CPU0_PATH)
                govCPU0 = readFile(GOV_CPU0_PATH)
                availableGovCPU0 = readAvailableGov(AVAILABLE_GOV_CPU0_PATH)
            } else if (event == Lifecycle.Event.ON_PAUSE) { }
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
                text = stringResource(R.string.little_cluster),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.min_freq),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                val minFreqCPU0Display = if (minFreqCPU0.isEmpty()) {
                    "error"
                } else {
                    "$minFreqCPU0 MHz"
                }
                ElevatedButton(
		    onClick = {
                        currentFileTarget = MIN_FREQ_CPU0_PATH
                        showAvailableFreqCPU0 = true
                    },
		    colors = ButtonDefaults.elevatedButtonColors(
			containerColor = MaterialTheme.colorScheme.primary
		    ) 
		) {
                    Text(
			text = "$minFreqCPU0Display",
			color = MaterialTheme.colorScheme.onPrimary
		    )
                }
            }
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.max_freq),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                val maxFreqCPU0Display = if (maxFreqCPU0.isEmpty()) {
                    "error"
                } else {
                    "$maxFreqCPU0 MHz"
                }
                ElevatedButton(
		    onClick = {
                        currentFileTarget = MAX_FREQ_CPU0_PATH
                        showAvailableFreqCPU0 = true
                    },
		    colors = ButtonDefaults.elevatedButtonColors(
			containerColor = MaterialTheme.colorScheme.primary
		    ) 
		) {
                    Text(
			text = "$maxFreqCPU0Display",
			color = MaterialTheme.colorScheme.onPrimary
		    )
                }
            }
            Spacer(Modifier.height(4.dp))

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
		    onClick = {
                        currentFileTarget = GOV_CPU0_PATH
                        showAvailableGovCPU0 = true
                    },
		    colors = ButtonDefaults.elevatedButtonColors(
			containerColor = MaterialTheme.colorScheme.primary
		    )
		) {
                    Text(
			text = "$govCPU0",
			color = MaterialTheme.colorScheme.onPrimary
		    )
                }
            }

            if (showAvailableFreqCPU0) {
                AvailableFreqCPU0Dialog(
                    availableFreqCPU0 = availableFreqCPU0,
                    onDismiss = { showAvailableFreqCPU0 = false },
                    onFreqSelected = { selectedFreq ->
                        writeFreqFile(currentFileTarget, selectedFreq)
                        showAvailableFreqCPU0 = false
                        minFreqCPU0 = readFreqFile(MIN_FREQ_CPU0_PATH)
                        maxFreqCPU0 = readFreqFile(MAX_FREQ_CPU0_PATH)
                    }
                )
            }

            if (showAvailableGovCPU0) {
                AvailableGovCPU0Dialog(
                    availableGovCPU0 = availableGovCPU0,
                    onDismiss = { showAvailableGovCPU0 = false },
                    onGovSelected = { selectedGov ->
                        writeFile(currentFileTarget, selectedGov)
                        showAvailableGovCPU0 = false
                        govCPU0 = readFile(GOV_CPU0_PATH)
                    }
                )
            }
        }
    }
}

@Composable
fun AvailableFreqCPU0Dialog(
    availableFreqCPU0: List<String>,
    onDismiss: () -> Unit,
    onFreqSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
	containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp,
        title = {
	    Text("Available frequencies",
	        color = MaterialTheme.colorScheme.onBackground
	    )
	},
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(rememberScrollState())
            ) {
                if (availableFreqCPU0.isEmpty()) {
                    Text(
                        text = "Failed to read available frequencies",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    availableFreqCPU0.forEach { freq ->
                        TextButton(
                            onClick = { onFreqSelected(freq) },
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
fun AvailableGovCPU0Dialog(
    availableGovCPU0: List<String>,
    onDismiss: () -> Unit,
    onGovSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
	containerColor = MaterialTheme.colorScheme.background,
	tonalElevation = 8.dp,
        title = {
	    Text("Available governors",
	        color = MaterialTheme.colorScheme.onBackground
	    )
	},
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
		if (availableGovCPU0.isEmpty()) {
		Text(
                        text = "Failed to read available governors",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    availableGovCPU0.forEach { gov ->
                        TextButton(
                            onClick = { onGovSelected(gov) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$gov",
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
fun BigClusterCard() {
    setPermissions(644, AVAILABLE_FREQ_CPU4_PATH)
    setPermissions(644, MIN_FREQ_CPU4_PATH)
    setPermissions(644, MAX_FREQ_CPU4_PATH)
    setPermissions(644, AVAILABLE_GOV_CPU4_PATH)
    setPermissions(644, GOV_CPU4_PATH)

    var minFreqCPU4 by remember { mutableStateOf(readFreqFile(MIN_FREQ_CPU4_PATH)) }
    var maxFreqCPU4 by remember { mutableStateOf(readFreqFile(MAX_FREQ_CPU4_PATH)) }
    var availableFreqCPU4 by remember { mutableStateOf(listOf<String>()) }
    var showAvailableFreqCPU4 by remember { mutableStateOf(false) }

    var govCPU4 by remember { mutableStateOf(readFile(GOV_CPU4_PATH)) }
    var availableGovCPU4 by remember { mutableStateOf(listOf<String>()) }
    var showAvailableGovCPU4 by remember { mutableStateOf(false) }

    var currentFileTarget by remember { mutableStateOf("") }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                minFreqCPU4 = readFreqFile(MIN_FREQ_CPU4_PATH)
                maxFreqCPU4 = readFreqFile(MAX_FREQ_CPU4_PATH)
                govCPU4 = readFile(GOV_CPU4_PATH)
                availableGovCPU4 = readAvailableGov(AVAILABLE_GOV_CPU4_PATH)
		availableFreqCPU4 = readAvailableFreqBoost(
                    AVAILABLE_FREQ_CPU4_PATH,
                    AVAILABLE_BOOST_CPU4_PATH
                )
            } else if (event == Lifecycle.Event.ON_PAUSE) { }
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.min_freq),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                val minFreqCPU4Display = if (minFreqCPU4.isEmpty()) {
                    "error"
                } else {
                    "$minFreqCPU4 MHz"
                }
                ElevatedButton(
		    onClick = {
                        currentFileTarget = MIN_FREQ_CPU4_PATH
                        showAvailableFreqCPU4 = true
                    },
		    colors = ButtonDefaults.elevatedButtonColors(
			containerColor = MaterialTheme.colorScheme.primary
		    ) 
		) {
                    Text(
			text = "$minFreqCPU4Display",
			color = MaterialTheme.colorScheme.onPrimary
		    )
                }
            }
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.max_freq),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                val maxFreqCPU4Display = if (maxFreqCPU4.isEmpty()) {
                    "error"
                } else {
                    "$maxFreqCPU4 MHz"
                }
                ElevatedButton(
		    onClick = {
                        currentFileTarget = MAX_FREQ_CPU4_PATH
                        showAvailableFreqCPU4 = true
                    },
		    colors = ButtonDefaults.elevatedButtonColors(
			containerColor = MaterialTheme.colorScheme.primary
		    ) 
		) {
                    Text(
			text = "$maxFreqCPU4Display",
			color = MaterialTheme.colorScheme.onPrimary
		    )
                }
            }
            Spacer(Modifier.height(4.dp))

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
		    onClick = {
                        currentFileTarget = GOV_CPU4_PATH
                        showAvailableGovCPU4 = true
                    },
		    colors = ButtonDefaults.elevatedButtonColors(
			containerColor = MaterialTheme.colorScheme.primary
		    )
		) {
                    Text(
			text = "$govCPU4",
			color = MaterialTheme.colorScheme.onPrimary
		    )
                }
            }

            if (showAvailableFreqCPU4) {
                AvailableFreqCPU4Dialog(
                    availableFreqCPU4 = availableFreqCPU4,
                    onDismiss = { showAvailableFreqCPU4 = false },
                    onFreqSelected = { selectedFreq ->
                        writeFreqFile(currentFileTarget, selectedFreq)
                        showAvailableFreqCPU4 = false
                        minFreqCPU4 = readFreqFile(MIN_FREQ_CPU4_PATH)
                        maxFreqCPU4 = readFreqFile(MAX_FREQ_CPU4_PATH)
                    }
                )
            }

            if (showAvailableGovCPU4) {
                AvailableGovCPU4Dialog(
                    availableGovCPU4 = availableGovCPU4,
                    onDismiss = { showAvailableGovCPU4 = false },
                    onGovSelected = { selectedGov ->
                        writeFile(currentFileTarget, selectedGov)
                        showAvailableGovCPU4 = false
                        govCPU4 = readFile(GOV_CPU4_PATH)
                    }
                )
            }
        }
    }
}

@Composable
fun AvailableFreqCPU4Dialog(
    availableFreqCPU4: List<String>,
    onDismiss: () -> Unit,
    onFreqSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
	containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp,
        title = {
	    Text("Available frequencies",
	        color = MaterialTheme.colorScheme.onBackground
	    )
	},
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(rememberScrollState())
            ) {
                if (availableFreqCPU4.isEmpty()) {
                    Text(
                        text = "Failed to read available frequencies",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    availableFreqCPU4.forEach { freq ->
                        TextButton(
                            onClick = { onFreqSelected(freq) },
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
fun AvailableGovCPU4Dialog(
    availableGovCPU4: List<String>,
    onDismiss: () -> Unit,
    onGovSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
	containerColor = MaterialTheme.colorScheme.background,
	tonalElevation = 8.dp,
        title = {
	    Text("Available governors",
	        color = MaterialTheme.colorScheme.onBackground
	    )
	},
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
		if (availableGovCPU4.isEmpty()) {
		Text(
                        text = "Failed to read available governors",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    availableGovCPU4.forEach { gov ->
                        TextButton(
                            onClick = { onGovSelected(gov) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$gov",
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
