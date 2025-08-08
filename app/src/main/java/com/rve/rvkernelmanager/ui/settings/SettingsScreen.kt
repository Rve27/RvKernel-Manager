/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.settings

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.ui.components.CustomListItem
import com.rve.rvkernelmanager.ui.components.Dialog
import com.rve.rvkernelmanager.ui.components.DialogTextButton
import com.rve.rvkernelmanager.ui.components.TopAppBarWithBackButton
import com.rve.rvkernelmanager.ui.theme.ThemeMode

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel(), lifecycleOwner: LifecycleOwner) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val themeMode by viewModel.themeMode.collectAsState()
    val pollingInterval by viewModel.pollingInterval.collectAsState()
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarWithBackButton(
                text = "Settings",
                onBack = { (context as? Activity)?.finish() },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            CustomListItem(
                icon = Icons.Default.Palette,
                title = "App theme",
                summary = "Choose between light, dark, or system default theme",
                onClick = { openThemeDialog = true },
            )
            CustomListItem(
                icon = Icons.Default.Timer,
                title = "SoC polling interval",
                summary = "Set how often SoC data is updated (in seconds)",
                onClick = { openPollingDialog = true },
            )
            CustomListItem(
                icon = Icons.Default.Info,
                title = "RvKernel Manager version",
                summary = appVersion,
                onLongClick = { clipboardManager.setText(AnnotatedString(appVersion)) },
            )
        }
    }

    if (openThemeDialog) {
        Dialog(
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
                        },
                    )
                    DialogTextButton(
                        icon = Icons.Default.DarkMode,
                        text = "Dark mode",
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.DARK)
                            openThemeDialog = false
                        },
                    )
                    DialogTextButton(
                        icon = Icons.Default.Android,
                        text = "System default",
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.SYSTEM_DEFAULT)
                            openThemeDialog = false
                        },
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openThemeDialog = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Close")
                }
            },
        )
    }

    if (openPollingDialog) {
        var value by remember { mutableStateOf((pollingInterval / 1000).toString()) }
        val intervalSeconds = value.toLongOrNull()

        Dialog(
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
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (intervalSeconds != null && intervalSeconds in 1..30) {
                                    viewModel.setPollingInterval(intervalSeconds * 1000)
                                    openPollingDialog = false
                                }
                            },
                        ),
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
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openPollingDialog = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}
