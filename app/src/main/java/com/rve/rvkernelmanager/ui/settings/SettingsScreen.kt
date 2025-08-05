/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.settings

import android.app.Activity

import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel

import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.utils.Utils
import com.rve.rvkernelmanager.ui.component.appBar.TopAppBarWithBackButton
import com.rve.rvkernelmanager.ui.component.listItem.*
import com.rve.rvkernelmanager.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val themeMode by viewModel.themeMode.collectAsState()
    val pollingInterval by viewModel.pollingInterval.collectAsState()
    val blurEnabled by viewModel.blurEnabled.collectAsState()
    val appVersion by viewModel.appVersion.collectAsState()

    var openThemeDialog by remember { mutableStateOf(false) }
    var openPollingDialog by remember { mutableStateOf(false) }

    val isDialogOpen = openThemeDialog || openPollingDialog

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.loadSettingsData(context)
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
	    TopAppBarWithBackButton(
		text = "Settings",
		onBack = { (context as? Activity)?.finish() }
	    )
	},
	modifier = if (isDialogOpen && blurEnabled) Modifier.blur(4.dp) else Modifier
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
	    SwitchListItem(
                icon = Icons.Default.BlurOn,
                title = "Blur effect",
                summary = "Enable blur effect when dialogs are open",
                checked = blurEnabled,
                onCheckedChange = { viewModel.setBlurEnabled(it) }
            )
	    CustomListItem(
                icon = Icons.Default.Info,
                title = "RvKernel Manager version",
                summary = appVersion,
		onLongClick = { clipboardManager.setText(AnnotatedString(appVersion)) }
            )
        }
    }
    
    if (openThemeDialog) {
	AlertDialog(
	    onDismissRequest = { openThemeDialog = false },
	    title = { Text("Select Theme") },
	    text = {
		Column {
		    DialogTextButtonListItem(
			icon = Icons.Default.LightMode,
			text = "Light mode",
			onClick = {
			    viewModel.setThemeMode(ThemeMode.LIGHT)
			    openThemeDialog = false
			}
		    )
		    DialogTextButtonListItem(
			icon = Icons.Default.DarkMode,
			text = "Dark mode",
			onClick = {
			    viewModel.setThemeMode(ThemeMode.DARK)
			    openThemeDialog = false
			}
		    )
		    DialogTextButtonListItem(
			icon = Icons.Default.Android,
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
		    onClick = { openThemeDialog = false },
		    shapes = ButtonDefaults.shapes()
		) {
		    Text("Close")
		}
	    }
	)
    }

    if (openPollingDialog) {
        var value by remember { mutableStateOf((pollingInterval / 1000).toString()) }
	val intervalSeconds = value.toLongOrNull()
        
        AlertDialog(
            onDismissRequest = { openPollingDialog = false },
            title = { Text("SoC Polling Interval") },
            text = {
                Column {
                    Text("Set the polling interval for SoC data (1-30 seconds)")
		    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text("Interval (seconds)") },
                        modifier = Modifier.fillMaxWidth(),
			singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
			    imeAction = ImeAction.Done
			),
			keyboardActions = KeyboardActions(
			    onDone = {
				if (intervalSeconds != null && intervalSeconds in 1..30) {
				    viewModel.setPollingInterval(intervalSeconds * 1000)
				    openPollingDialog = false
				}
			    }
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (intervalSeconds != null && intervalSeconds in 1..30) {
                            viewModel.setPollingInterval(intervalSeconds * 1000)
                            openPollingDialog = false
                        }
                    },
		    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openPollingDialog = false },
		    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
