/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.settings

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_android_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_build_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_dark_mode_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_light_mode_rounded_filled
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.ListItem
import com.rve.rvkernelmanager.ui.components.TopAppBarWithBackButton
import com.rve.rvkernelmanager.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val pollingInterval by viewModel.pollingInterval.collectAsStateWithLifecycle()
    var value by remember { mutableStateOf((pollingInterval / 1000).toString()) }
    val intervalSeconds = value.toLongOrNull()

    var openThemeDialog by remember { mutableStateOf(false) }
    var openPollingDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarWithBackButton(
                text = stringResource(R.string.settings),
                onBack = { (context as? Activity)?.finish() },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            ListItem(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.app_theme),
                summary = stringResource(R.string.theme_summary),
                onClick = { openThemeDialog = true },
            )
            ListItem(
                icon = Icons.Default.Timer,
                title = stringResource(R.string.soc_polling),
                summary = stringResource(R.string.soc_polling_summary),
                onClick = { openPollingDialog = true },
            )
            ListItem(
                icon = painterResource(materialsymbols_ic_build_rounded_filled),
                title = stringResource(R.string.developer_options),
                summary = stringResource(R.string.developer_options_summary),
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                        context.startActivity(intent)
                    }
                },
            )
        }
    }

    if (openThemeDialog) {
        AlertDialog(
            onDismissRequest = { openThemeDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.select_theme),
                    style = MaterialTheme.typography.titleMedium,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                Column {
                    Button(
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.LIGHT)
                            openThemeDialog = false
                        },
                        shapes = ButtonDefaults.shapes(),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(materialsymbols_ic_light_mode_rounded_filled),
                                contentDescription = null,
                            )
                            Text(stringResource(R.string.theme_light))
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.DARK)
                            openThemeDialog = false
                        },
                        shapes = ButtonDefaults.shapes(),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(materialsymbols_ic_dark_mode_rounded_filled),
                                contentDescription = null,
                            )
                            Text(stringResource(R.string.theme_dark))
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.setThemeMode(ThemeMode.SYSTEM_DEFAULT)
                            openThemeDialog = false
                        },
                        shapes = ButtonDefaults.shapes(),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(materialsymbols_ic_android_rounded_filled),
                                contentDescription = null,
                            )
                            Text(stringResource(R.string.theme_system))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openThemeDialog = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    if (openPollingDialog) {
        AlertDialog(
            onDismissRequest = { openPollingDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.soc_polling),
                    style = MaterialTheme.typography.titleMedium,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.polling_dialog_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text(stringResource(R.string.polling_hint)) },
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
                    Text(stringResource(R.string.apply))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openPollingDialog = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}
