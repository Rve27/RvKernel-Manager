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

// Dear programmer:
// When I wrote this code, only god and
// I knew how it worked.
// Now, only god knows it!
//
// Therefore, if you are trying to optimize
// this routine and it fails (most surely),
// please increase this counter as a
// warning for the next person:
//
// total hours wasted here = 254
//
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.battery

import android.content.ClipData
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.res.stringResource
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
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_airline_seat_flat_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_battery_android_frame_alert_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_battery_android_frame_full_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_call_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_camera_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_dvr_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_globe_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_history_2_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_mode_cool_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_speed_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_sports_esports_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_videocam_rounded_filled
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.Card.ItemCard
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
                }

                else -> {}
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
                .background(MaterialTheme.colorScheme.surfaceContainer),
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
    val context = LocalContext.current
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
            color = MaterialTheme.colorScheme.surfaceBright,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ItemCard(
                shape = CircleShape,
                icon = painterResource(materialsymbols_ic_dvr_rounded_filled),
                title = stringResource(R.string.battery_monitor),
                titleLarge = true,
            )

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
                                                context.getString(R.string.voltage),
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
                                                context.getString(R.string.temperature),
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
                                        context.getString(R.string.level),
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
                                text = stringResource(R.string.level),
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

            ItemCard(
                shape = MaterialTheme.shapes.extraLarge,
                icon = painterResource(materialsymbols_ic_history_2_rounded_filled),
                title = stringResource(R.string.uptime),
                body = uptime,
            )
            ItemCard(
                shape = MaterialTheme.shapes.extraLarge,
                icon = painterResource(materialsymbols_ic_airline_seat_flat_rounded_filled),
                title = stringResource(R.string.deep_sleep),
                body = batteryInfo.deepSleep,
            )
        }
    }
}

@Composable
fun BatteryInfoCard(viewModel: BatteryViewModel) {
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // MDC = Manual Design Capacity
    var openMDC by remember { mutableStateOf(false) }

    val batteryInfo by viewModel.batteryInfo.collectAsStateWithLifecycle()
    var manualDesignCapacity by remember { mutableStateOf(batteryInfo.manualDesignCapacity.toString()) }

    OutlinedCard(
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(
            width = 2.0.dp,
            color = MaterialTheme.colorScheme.surfaceBright,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ItemCard(
                shape = CircleShape,
                icon = painterResource(materialsymbols_ic_battery_android_frame_alert_rounded_filled),
                title = stringResource(R.string.battery_info),
                titleLarge = true,
            )

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
                                                context.getString(R.string.tech),
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
                                                context.getString(R.string.health),
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
            ItemCard(
                shape = MaterialTheme.shapes.extraLarge,
                icon = painterResource(materialsymbols_ic_battery_android_frame_full_rounded_filled),
                title = stringResource(R.string.design_capacity),
                body = if (manualDesignCapacity == "0") batteryInfo.designCapacity else manualDesignCapacity,
                onClick = { openMDC = true }
            )
            ItemCard(
                shape = MaterialTheme.shapes.extraLarge,
                icon = painterResource(materialsymbols_ic_battery_android_frame_full_rounded_filled),
                title = stringResource(R.string.battery_max_capacity),
                body = batteryInfo.maximumCapacity
            )
        }
    }

    if (openMDC) {
        AlertDialog(
            onDismissRequest = { openMDC = false },
            title = {
                Text(
                    text = stringResource(R.string.set_manual_capacity),
                    style = MaterialTheme.typography.titleMedium,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = manualDesignCapacity,
                        onValueChange = { manualDesignCapacity = it },
                        label = { Text(stringResource(R.string.capacity_hint)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.setManualDesignCapacity(context, manualDesignCapacity.toInt())
                                openMDC = false
                            },
                        ),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setManualDesignCapacity(context, manualDesignCapacity.toInt())
                        openMDC = false
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.apply))
                }
            },
        )
    }
}

@Composable
fun ThermalProfilesCard(viewModel: BatteryViewModel) {
    // TPD = Thermal Profiles Dialog
    var openTPD by remember { mutableStateOf(false) }

    val thermalSconfig by viewModel.thermalSconfig.collectAsStateWithLifecycle()

    val thermalProfilesOptions = listOf(
        Triple(
            materialsymbols_ic_mode_cool_rounded_filled,
            R.string.profile_default,
        ) {
            viewModel.updateThermalSconfig("0")
            openTPD = false
        },
        Triple(
            materialsymbols_ic_speed_rounded_filled,
            R.string.profile_benchmark,
        ) {
            viewModel.updateThermalSconfig("10")
            openTPD = false
        },
        Triple(
            materialsymbols_ic_globe_rounded_filled,
            R.string.profile_browser,
        ) {
            viewModel.updateThermalSconfig("11")
            openTPD = false
        },
        Triple(
            materialsymbols_ic_camera_rounded_filled,
            R.string.profile_camera,
        ) {
            viewModel.updateThermalSconfig("12")
            openTPD = false
        },
        Triple(
            materialsymbols_ic_call_rounded_filled,
            R.string.profile_dialer,
        ) {
            viewModel.updateThermalSconfig("8")
            openTPD = false
        },
        Triple(
            materialsymbols_ic_sports_esports_rounded_filled,
            R.string.profile_gaming,
        ) {
            viewModel.updateThermalSconfig("13")
            openTPD = false
        },
        Triple(
            materialsymbols_ic_videocam_rounded_filled,
            R.string.profile_streaming,
        ) {
            viewModel.updateThermalSconfig("14")
            openTPD = false
        },
    )

    Button(
        onClick = { openTPD = true },
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
                    text = stringResource(R.string.thermal_profiles),
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
                    }.let {
                        when (it) {
                            "Default" -> stringResource(R.string.profile_default)
                            "Benchmark" -> stringResource(R.string.profile_benchmark)
                            "Browser" -> stringResource(R.string.profile_browser)
                            "Camera" -> stringResource(R.string.profile_camera)
                            "Dialer" -> stringResource(R.string.profile_dialer)
                            "Gaming" -> stringResource(R.string.profile_gaming)
                            "Streaming" -> stringResource(R.string.profile_streaming)
                            else -> stringResource(R.string.unknown)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }

    if (openTPD) {
        AlertDialog(
            onDismissRequest = { openTPD = false },
            title = {
                Text(
                    text = stringResource(R.string.thermal_profiles),
                    style = MaterialTheme.typography.titleMedium,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                Column {
                    thermalProfilesOptions.forEach { item ->
                        Button(
                            onClick = item.third,
                            shapes = ButtonDefaults.shapes(),
                            contentPadding = PaddingValues(16.dp),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    painter = painterResource(item.first),
                                    contentDescription = stringResource(item.second),
                                )
                                Text(stringResource(item.second))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openTPD = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }
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
                text = stringResource(R.string.force_fast_charging),
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
                text = stringResource(R.string.bypass_charging),
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
