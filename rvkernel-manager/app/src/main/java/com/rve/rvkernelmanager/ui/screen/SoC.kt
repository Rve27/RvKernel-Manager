package com.rve.rvkernelmanager.ui.screen

import java.io.File
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
import com.rve.rvkernelmanager.utils.GOV_CPU0_PATH
import com.rve.rvkernelmanager.utils.readFile
import com.rve.rvkernelmanager.utils.writeFile
import com.rve.rvkernelmanager.utils.readFreqFile
import com.rve.rvkernelmanager.utils.writeFreqFile
import com.rve.rvkernelmanager.utils.readAvailableFreqCPU0
import com.rve.rvkernelmanager.utils.readAvailableGovCPU0
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
	    CPUCard()
	}
    }
}

@Composable
fun CPUCard() {
    var minFreqCPU0 by remember { mutableStateOf("0") }
    var maxFreqCPU0 by remember { mutableStateOf("0") }
    var availableFreqCPU0 by remember { mutableStateOf(listOf<String>()) }
    var showAvailableFreqCPU0 by remember { mutableStateOf(false) }
    var govCPU0 by remember { mutableStateOf("Loading...") }
    var availableGovCPU0 by remember { mutableStateOf(listOf<String>()) }
    var showAvailableGovCPU0 by remember { mutableStateOf(false) }
    var currentFileTarget by remember { mutableStateOf("") }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                minFreqCPU0 = readFreqFile(MIN_FREQ_CPU0_PATH)
                maxFreqCPU0 = readFreqFile(MAX_FREQ_CPU0_PATH)
                availableFreqCPU0 = readAvailableFreqCPU0()
		govCPU0 = readFile(GOV_CPU0_PATH)
		availableGovCPU0 = readAvailableGovCPU0()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ElevatedCard(
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.little_cluster),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
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
                ElevatedButton(onClick = {
                    currentFileTarget = "$MIN_FREQ_CPU0_PATH"
                    showAvailableFreqCPU0 = true
                }) {
                    Text("$minFreqCPU0 MHz")
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
                ElevatedButton(onClick = {
                    currentFileTarget = "$MAX_FREQ_CPU0_PATH"
                    showAvailableFreqCPU0 = true
                }) {
                    Text("$maxFreqCPU0 MHz")
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
                ElevatedButton(onClick = {
                    currentFileTarget = "$GOV_CPU0_PATH"
                    showAvailableGovCPU0 = true
                }) {
                    Text("$govCPU0")
                }
            }

            if (showAvailableFreqCPU0) {
                AvailableFreqCPU0Dialog(
                    availableFreqCPU0 = availableFreqCPU0,
                    onDismiss = { showAvailableFreqCPU0 = false },
                    onFreqSelected = { selectedFreq ->
                        writeFreqFile(currentFileTarget, selectedFreq)
                        showAvailableFreqCPU0 = false
                        minFreqCPU0 = readFreqFile("$MIN_FREQ_CPU0_PATH")
                        maxFreqCPU0 = readFreqFile("$MAX_FREQ_CPU0_PATH")
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
                        govCPU0 = readFreqFile("$GOV_CPU0_PATH")
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
        title = { Text("Available frequencies") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(rememberScrollState())
            ) {
                availableFreqCPU0.forEach { freq ->
                    TextButton(
                        onClick = { onFreqSelected(freq) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$freq MHz",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
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
        title = { Text("Available governors") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .verticalScroll(rememberScrollState())
            ) {
                availableGovCPU0.forEach { gov ->
                    TextButton(
                        onClick = { onGovSelected(gov) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$gov",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
