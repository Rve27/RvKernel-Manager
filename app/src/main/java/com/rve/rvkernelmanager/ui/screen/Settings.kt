package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel

import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.component.*
import com.rve.rvkernelmanager.ui.navigation.SettingsTopAppBar
import com.rve.rvkernelmanager.ui.theme.ThemeMode
import com.rve.rvkernelmanager.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val themeMode by viewModel.themeMode.collectAsState()
    val pollingInterval by viewModel.pollingInterval.collectAsState()

    var openThemeDialog by remember { mutableStateOf(false) }
    var openPollingDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { SettingsTopAppBar(scrollBehavior = scrollBehavior) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            CustomListItem(
		icon = Icons.Default.Palette,
                title = "App theme",
                summary = "Choose between light, dark, or system default theme",
                onClick = { openThemeDialog = true }
            )
	    CustomListItem(
                icon = Icons.Default.Timer,
                title = "SoC polling interval",
                summary = "Set how often SoC data is updated (in seconds)",
                onClick = { openPollingDialog = true }
            )
        }
    }
    
    if (openThemeDialog) {
	AlertDialog(
	    onDismissRequest = { openThemeDialog = false },
	    title = { Text("Select Theme") },
	    text = {
		Column {
		    DialogTextButton(
			icon = Icons.Default.LightMode,
			text = "Light mode",
			onClick = {
			    viewModel.setThemeMode(ThemeMode.LIGHT)
			    openThemeDialog = false
			}
		    )
		    DialogTextButton(
			icon = Icons.Default.DarkMode,
			text = "Dark mode",
			onClick = {
			    viewModel.setThemeMode(ThemeMode.DARK)
			    openThemeDialog = false
			}
		    )
		    DialogTextButton(
			icon = painterResource(R.drawable.ic_android),
			text = "System default",
			onClick = {
			    viewModel.setThemeMode(ThemeMode.SYSTEM_DEFAULT)
			    openThemeDialog = false
			}
		    )
		}
	    },
	    confirmButton = {
		TextButton(
		    onClick = { openThemeDialog = false }
		) {
		    Text("Close")
		}
	    }
	)
    }

    if (openPollingDialog) {
        var newPollingValue by remember { mutableStateOf((pollingInterval / 1000).toString()) }
        
        AlertDialog(
            onDismissRequest = { openPollingDialog = false },
            title = { Text("SoC Polling Interval") },
            text = {
                Column {
                    Text("Set the polling interval for SoC data (1-30 seconds)")
		    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newPollingValue,
                        onValueChange = { newPollingValue = it },
                        label = { Text("Interval (seconds)") },
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
                        val intervalSeconds = newPollingValue.toLongOrNull()
                        if (intervalSeconds != null && intervalSeconds in 1..30) {
                            viewModel.setPollingInterval(intervalSeconds * 1000)
                            openPollingDialog = false
                        }
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openPollingDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
