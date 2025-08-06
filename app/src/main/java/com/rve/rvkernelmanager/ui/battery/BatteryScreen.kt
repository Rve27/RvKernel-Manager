/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.battery
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Dvr
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.ButtonListItem
import com.rve.rvkernelmanager.ui.components.CustomListItem
import com.rve.rvkernelmanager.ui.components.DialogTextButton
import com.rve.rvkernelmanager.ui.components.MonitorListItem
import com.rve.rvkernelmanager.ui.components.PinnedTopAppBar
import com.rve.rvkernelmanager.ui.components.SwitchListItem
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationBar
import com.rve.rvkernelmanager.ui.settings.SettingsPreference

@Composable
fun BatteryScreen(viewModel: BatteryViewModel = viewModel(), lifecycleOwner: LifecycleOwner, navController: NavController) {
    val context = LocalContext.current

    val settingsPreference = remember { SettingsPreference.getInstance(context) }
    val blurEnabled by settingsPreference.blurEnabled.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val chargingState by viewModel.chargingState.collectAsState()
    val hasThermalSconfig by viewModel.hasThermalSconfig.collectAsState()

    var isDialogOpen by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.initializeBatteryInfo(context)
                    viewModel.startJob()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.unregisterBatteryListeners(context)
                    viewModel.stopJob()
                } else -> {}
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).then(
            if (isDialogOpen &&
                blurEnabled
            ) Modifier.blur(4.dp) else Modifier,
        ),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = rememberLazyListState(),
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                BatteryMonitorCard(viewModel)
            }
            item {
                BatteryInfoCard(viewModel)
            }
            if (hasThermalSconfig) {
                item {
                    ThermalProfilesCard(viewModel = viewModel, onDialogStateChange = { isOpen -> isDialogOpen = isOpen })
                }
            }
            if (chargingState.hasFastCharging) {
                item {
                    ChargingCard(viewModel)
                }
            }
            item {
                Spacer(Modifier)
            }
        }
    }
}

@Composable
fun BatteryMonitorCard(viewModel: BatteryViewModel) {
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val uptime by viewModel.uptime.collectAsState()

    Card {
        CustomListItem(
            title = "Battery Monitor",
            titleLarge = true,
            icon = Icons.AutoMirrored.Default.Dvr,
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        Column(modifier = Modifier.padding(16.dp)) {
            MonitorListItem(
                title = "Level",
                summary = batteryInfo.level,
            )
            Spacer(Modifier.height(8.dp))
            MonitorListItem(
                title = "Voltage",
                summary = batteryInfo.voltage,
            )
            Spacer(Modifier.height(8.dp))
            MonitorListItem(
                title = "Temperature",
                summary = batteryInfo.temp,
            )
            Spacer(Modifier.height(8.dp))
            MonitorListItem(
                title = "Uptime",
                summary = uptime,
            )
            Spacer(Modifier.height(8.dp))
            MonitorListItem(
                title = "Deep sleep",
                summary = batteryInfo.deepSleep,
            )
        }
    }
}

@Composable
fun BatteryInfoCard(viewModel: BatteryViewModel) {
    val clipboardManager = LocalClipboardManager.current

    val batteryInfo by viewModel.batteryInfo.collectAsState()

    Card {
        CustomListItem(
            title = "Battery Information",
            titleLarge = true,
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        CustomListItem(
            title = "Technology",
            summary = batteryInfo.tech,
            icon = painterResource(R.drawable.ic_technology),
            onLongClick = { clipboardManager.setText(AnnotatedString(batteryInfo.tech)) },
        )

        CustomListItem(
            title = "Health",
            summary = batteryInfo.health,
            icon = painterResource(R.drawable.ic_health),
            onLongClick = { clipboardManager.setText(AnnotatedString(batteryInfo.health)) },
        )

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        ) {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp,
                ),
            ) {
                CustomListItem(
                    title = "Design capacity",
                    summary = batteryInfo.designCapacity,
                    onLongClick = { clipboardManager.setText(AnnotatedString(batteryInfo.designCapacity)) },
                )

                CustomListItem(
                    title = "Maximum capacity",
                    summary = batteryInfo.maximumCapacity,
                    onLongClick = { clipboardManager.setText(AnnotatedString(batteryInfo.maximumCapacity)) },
                )
            }
        }
    }
}

@Composable
fun ThermalProfilesCard(viewModel: BatteryViewModel, onDialogStateChange: (Boolean) -> Unit = {}) {
    // TPD = Thermal Profiles Dialog
    var openTPD by remember { mutableStateOf(false) }

    val thermalSconfig by viewModel.thermalSconfig.collectAsState()

    LaunchedEffect(openTPD) {
        onDialogStateChange(openTPD)
    }

    Card {
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
                    else -> "Unknown"
                }
            },
            onClick = { openTPD = true },
        )

        if (openTPD) {
            AlertDialog(
                onDismissRequest = { openTPD = false },
                title = {
                    Text(
                        text = "Thermal profiles",
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        DialogTextButton(
                            icon = painterResource(R.drawable.ic_mode_cool),
                            text = "Default",
                            onClick = {
                                viewModel.updateThermalSconfig("0")
                                openTPD = false
                            },
                        )
                        DialogTextButton(
                            icon = Icons.Default.Speed,
                            text = "Benchmark",
                            onClick = {
                                viewModel.updateThermalSconfig("10")
                                openTPD = false
                            },
                        )
                        DialogTextButton(
                            icon = Icons.Default.Language,
                            text = "Browser",
                            onClick = {
                                viewModel.updateThermalSconfig("11")
                                openTPD = false
                            },
                        )
                        DialogTextButton(
                            icon = Icons.Default.PhotoCamera,
                            text = "Camera",
                            onClick = {
                                viewModel.updateThermalSconfig("12")
                                openTPD = false
                            },
                        )
                        DialogTextButton(
                            icon = Icons.Default.Call,
                            text = "Dialer",
                            onClick = {
                                viewModel.updateThermalSconfig("8")
                                openTPD = false
                            },
                        )
                        DialogTextButton(
                            icon = Icons.Default.SportsEsports,
                            text = "Gaming",
                            onClick = {
                                viewModel.updateThermalSconfig("13")
                                openTPD = false
                            },
                        )
                        DialogTextButton(
                            icon = Icons.Default.Videocam,
                            text = "Streaming",
                            onClick = {
                                viewModel.updateThermalSconfig("14")
                                openTPD = false
                            },
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { openTPD = false },
                        shapes = ButtonDefaults.shapes(),
                    ) {
                        Text("Close")
                    }
                },
            )
        }
    }
}

@Composable
fun ChargingCard(viewModel: BatteryViewModel) {
    val chargingState by viewModel.chargingState.collectAsState()

    Card {
        CustomListItem(
            title = "Charging",
            titleLarge = true,
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        if (chargingState.hasFastCharging) {
            SwitchListItem(
                title = "Fast charging",
                summary = "Enable force fast charging",
                checked = chargingState.isFastChargingChecked,
                onCheckedChange = { viewModel.toggleFastCharging(it) },
            )
        }
    }
}
