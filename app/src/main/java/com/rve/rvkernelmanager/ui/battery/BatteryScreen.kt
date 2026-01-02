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

package com.rve.rvkernelmanager.ui.battery

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.composables.core.rememberDialogState
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.DialogTextButton
import com.rve.rvkernelmanager.ui.components.DialogUnstyled
import com.rve.rvkernelmanager.ui.components.SimpleTopAppBar
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationBar
import com.rve.rvkernelmanager.utils.BatteryUtils
import kotlinx.coroutines.launch

@Composable
fun BatteryScreen(viewModel: BatteryViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val chargingState by viewModel.chargingState.collectAsStateWithLifecycle()
    val rvkernels = listOf(
        "RvKernel-Alioth-v1.2",
        "RvKernel-Alioth-v1.3",
    )
    val hasThermalSconfig by viewModel.hasThermalSconfig.collectAsStateWithLifecycle()

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
        topBar = { SimpleTopAppBar() },
        bottomBar = { BottomNavigationBar(navController) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    BatteryMonitorCard(viewModel)
                }
                item {
                    BatteryInfoCard(viewModel)
                }
                if (hasThermalSconfig) {
                    item {
                        ThermalProfilesCard(viewModel)
                    }
                }
                if (chargingState.hasFastCharging) {
                    item {
                        ForceFastChargingCard(viewModel)
                    }
                }
                if (rvkernels.any { chargingState.kernelVersion.contains(it) }) {
                    item {
                        BypassChargingCard(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun BatteryMonitorCard(viewModel: BatteryViewModel) {
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    val batteryInfo by viewModel.batteryInfo.collectAsStateWithLifecycle()
    val uptime by viewModel.uptime.collectAsStateWithLifecycle()

    val batteryLevelProgress = remember(batteryInfo.level) {
        batteryInfo.level.removeSuffix("%").toFloatOrNull()?.div(100f) ?: 0f
    }

    val animatedBatteryLevelProgress by animateFloatAsState(
        targetValue = batteryLevelProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    val batteryTemp = batteryInfo.temp.toIntOrNull() ?: 0

    OutlinedCard(
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(
            width = 2.0.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_dvr),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null,
                    )
                    Text(
                        text = "Battery Monitor",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier.clip(CircleShape).combinedClickable(
                            onClick = { /* do nothing */ },
                            onLongClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "Battery voltage",
                                                batteryInfo.voltage,
                                            ),
                                        ),
                                    )
                                }
                            },
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_bolt),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null,
                            )
                            Text(
                                text = batteryInfo.voltage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier.clip(CircleShape).combinedClickable(
                            onClick = { /* do nothing */ },
                            onLongClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "Battery temperature",
                                                batteryInfo.temp,
                                            ),
                                        ),
                                    )
                                }
                            },
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Crossfade(
                                targetState = batteryTemp,
                                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                            ) { temp ->
                                if (temp <= 45) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_cool),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        contentDescription = null,
                                    )
                                } else if (temp >= 45) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_heat),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        contentDescription = null,
                                    )
                                } else if (temp >= 55) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_emergency_heat),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        contentDescription = null,
                                    )
                                }
                            }
                            Text(
                                text = batteryInfo.temp,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }

            Card(
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.clip(MaterialTheme.shapes.extraLarge).combinedClickable(
                    onClick = { /* do nothing */ },
                    onLongClick = {
                        coroutineScope.launch {
                            clipboard.setClipEntry(
                                ClipEntry(
                                    ClipData.newPlainText(
                                        "Battery level",
                                        batteryInfo.level,
                                    ),
                                ),
                            )
                        }
                    },
                ),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Crossfade(
                            targetState = batteryLevelProgress,
                            animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                        ) { batteryLevel ->
                            if (batteryLevel == 0f) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_battery_android_alert),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null,
                                )
                            } else if (batteryLevel <= 0.15f) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_battery_android_frame_1),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null,
                                )
                            } else if (batteryLevel <= 0.30f) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_battery_android_frame_2),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null,
                                )
                            } else if (batteryLevel <= 0.45f) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_battery_android_frame_3),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null,
                                )
                            } else if (batteryLevel <= 0.60f) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_battery_android_frame_4),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null,
                                )
                            } else if (batteryLevel <= 0.75f) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_battery_android_frame_5),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null,
                                )
                            } else if (batteryLevel <= 0.90f) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_battery_android_frame_6),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null,
                                )
                            } else if (batteryLevel <= 1f) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_battery_android_frame_full),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null,
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Battery level",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = batteryInfo.level,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                        }
                    }
                    LinearWavyProgressIndicator(
                        progress = { animatedBatteryLevelProgress },
                        modifier = Modifier.fillMaxWidth(),
                        trackColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .weight(1f)
                        .combinedClickable(
                            onClick = { /* do nothing */ },
                            onLongClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "Device uptime",
                                                uptime,
                                            ),
                                        ),
                                    )
                                }
                            },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_history),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = null,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Uptime",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = uptime,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .weight(1f)
                        .combinedClickable(
                            onClick = { /* do nothing */ },
                            onLongClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "Deep sleep",
                                                batteryInfo.deepSleep,
                                            ),
                                        ),
                                    )
                                }
                            },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_nightlight),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = null,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Deep sleep",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = batteryInfo.deepSleep,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BatteryInfoCard(viewModel: BatteryViewModel) {
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // MDC = Manual Design Capacity
    val openMDC = rememberDialogState(initiallyVisible = false)

    val batteryInfo by viewModel.batteryInfo.collectAsStateWithLifecycle()
    val manualDesignCapacity = batteryInfo.manualDesignCapacity.toString()
    var visible: Boolean = manualDesignCapacity == "0"
    var value by remember { mutableStateOf(batteryInfo.manualDesignCapacity.toString()) }

    OutlinedCard(
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(
            width = 2.0.dp,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_battery_android_frame_alert),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null,
                    )
                    Text(
                        text = "Battery Information",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier.clip(CircleShape).combinedClickable(
                            onClick = { /* do nothing */ },
                            onLongClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "Battery technology",
                                                batteryInfo.tech,
                                            ),
                                        ),
                                    )
                                }
                            },
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_biotech),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null,
                            )
                            Text(
                                text = batteryInfo.tech,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = CircleShape,
                        modifier = Modifier.clip(CircleShape).combinedClickable(
                            onClick = { /* do nothing */ },
                            onLongClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "Battery health",
                                                batteryInfo.health,
                                            ),
                                        ),
                                    )
                                }
                            },
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_heart_plus),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null,
                            )
                            Text(
                                text = batteryInfo.health,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }

            Card(
                shape = CircleShape,
                modifier = Modifier.clip(CircleShape).combinedClickable(
                    onClick = {
                        if (batteryInfo.designCapacity == "N/A") {
                            openMDC.visible = true
                        }
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            clipboard.setClipEntry(
                                ClipEntry(
                                    ClipData.newPlainText(
                                        "Battery design capacity",
                                        batteryInfo.designCapacity,
                                    ),
                                ),
                            )
                        }
                    },
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_battery_android_frame_full),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Design capacity",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = if (batteryInfo.designCapacity == "N/A") {
                                "$manualDesignCapacity mAh"
                            } else {
                                batteryInfo.designCapacity
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (batteryInfo.designCapacity == "unknown") {
                            AnimatedVisibility(visible) {
                                Text(
                                    text = "(Tap to set design capacity manually)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }

            Card(
                shape = CircleShape,
                modifier = Modifier.clip(CircleShape).combinedClickable(
                    onClick = { /* do nothing */ },
                    onLongClick = {
                        coroutineScope.launch {
                            clipboard.setClipEntry(
                                ClipEntry(
                                    ClipData.newPlainText(
                                        "Battery maximum capacity",
                                        batteryInfo.maximumCapacity,
                                    ),
                                ),
                            )
                        }
                    },
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_battery_android_frame_full),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Maximum capacity",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = batteryInfo.maximumCapacity,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    DialogUnstyled(
        state = openMDC,
        title = {
            Text(
                text = "Set manual design capacity",
                style = MaterialTheme.typography.titleMedium,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Design capacity (mAh)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.setManualDesignCapacity(context, value.toInt())
                            openMDC.visible = false
                        },
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setManualDesignCapacity(context, value.toInt())
                    openMDC.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Apply")
            }
        },
    )
}

@Composable
fun ThermalProfilesCard(viewModel: BatteryViewModel) {
    // TPD = Thermal Profiles Dialog
    val openTPD = rememberDialogState(initiallyVisible = false)

    val thermalSconfig by viewModel.thermalSconfig.collectAsStateWithLifecycle()

    Button(
        onClick = { openTPD.visible = true },
        shapes = ButtonDefaults.shapes(),
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_battery_profile),
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = null,
            )
            Column {
                Text(
                    text = "Thermal profiles",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
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
                            else -> "Unknown"
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }

    DialogUnstyled(
        state = openTPD,
        title = {
            Text(
                text = "Thermal profiles",
                style = MaterialTheme.typography.titleMedium,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Column {
                DialogTextButton(
                    icon = painterResource(R.drawable.ic_cool),
                    text = "Default",
                    onClick = {
                        viewModel.updateThermalSconfig("0")
                        openTPD.visible = false
                    },
                )
                DialogTextButton(
                    icon = Icons.Default.Speed,
                    text = "Benchmark",
                    onClick = {
                        viewModel.updateThermalSconfig("10")
                        openTPD.visible = false
                    },
                )
                DialogTextButton(
                    icon = Icons.Default.Language,
                    text = "Browser",
                    onClick = {
                        viewModel.updateThermalSconfig("11")
                        openTPD.visible = false
                    },
                )
                DialogTextButton(
                    icon = Icons.Default.PhotoCamera,
                    text = "Camera",
                    onClick = {
                        viewModel.updateThermalSconfig("12")
                        openTPD.visible = false
                    },
                )
                DialogTextButton(
                    icon = Icons.Default.Call,
                    text = "Dialer",
                    onClick = {
                        viewModel.updateThermalSconfig("8")
                        openTPD.visible = false
                    },
                )
                DialogTextButton(
                    icon = Icons.Default.SportsEsports,
                    text = "Gaming",
                    onClick = {
                        viewModel.updateThermalSconfig("13")
                        openTPD.visible = false
                    },
                )
                DialogTextButton(
                    icon = Icons.Default.Videocam,
                    text = "Streaming",
                    onClick = {
                        viewModel.updateThermalSconfig("14")
                        openTPD.visible = false
                    },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { openTPD.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )
}

@Composable
fun ForceFastChargingCard(viewModel: BatteryViewModel) {
    val chargingState by viewModel.chargingState.collectAsStateWithLifecycle()

    Button(
        onClick = { viewModel.updateCharging(filePath = BatteryUtils.FAST_CHARGING, checked = !chargingState.isFastChargingChecked) },
        shapes = ButtonDefaults.shapes(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(
            width = 2.0.dp,
            color = MaterialTheme.colorScheme.primary,
        ),
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_electric_bolt),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = null,
            )
            Text(
                text = "Force fast charging",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = chargingState.isFastChargingChecked,
                onCheckedChange = { viewModel.updateCharging(filePath = BatteryUtils.FAST_CHARGING, checked = it) },
                thumbContent = {
                    Crossfade(
                        targetState = chargingState.isFastChargingChecked,
                        animationSpec = tween(durationMillis = 500),
                    ) { isChecked ->
                        if (isChecked) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    }
                },
            )
        }
    }
}

@Composable
fun BypassChargingCard(viewModel: BatteryViewModel) {
    val chargingState by viewModel.chargingState.collectAsStateWithLifecycle()

    Button(
        onClick = { viewModel.updateCharging(filePath = BatteryUtils.BYPASS_CHARGING, checked = !chargingState.isBypassChargingChecked) },
        shapes = ButtonDefaults.shapes(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(
            width = 2.0.dp,
            color = MaterialTheme.colorScheme.primary,
        ),
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_battery_android_frame_shield),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = null,
            )
            Text(
                text = "Bypass charging",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = chargingState.isBypassChargingChecked,
                onCheckedChange = { viewModel.updateCharging(filePath = BatteryUtils.BYPASS_CHARGING, checked = it) },
                thumbContent = {
                    Crossfade(
                        targetState = chargingState.isBypassChargingChecked,
                        animationSpec = tween(durationMillis = 500),
                    ) { isChecked ->
                        if (isChecked) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BatteryScreenPreview() {
    val navController = rememberNavController()
    BatteryScreen(navController = navController)
}
