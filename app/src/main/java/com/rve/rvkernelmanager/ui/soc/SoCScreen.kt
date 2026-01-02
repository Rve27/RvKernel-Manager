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

package com.rve.rvkernelmanager.ui.soc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.composables.core.rememberDialogState
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.CustomListItem
import com.rve.rvkernelmanager.ui.components.DialogTextButton
import com.rve.rvkernelmanager.ui.components.DialogUnstyled
import com.rve.rvkernelmanager.ui.components.SimpleTopAppBar
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationBar

@Composable
fun SoCScreen(viewModel: SoCViewModel = viewModel(), navController: NavController) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val hasCpuInputBoostMs by viewModel.hasCpuInputBoostMs.collectAsStateWithLifecycle()
    val hasCpuSchedBoostOnInput by viewModel.hasCpuSchedBoostOnInput.collectAsStateWithLifecycle()
    val hasBigCluster by viewModel.hasBigCluster.collectAsStateWithLifecycle()
    val hasPrimeCluster by viewModel.hasPrimeCluster.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.startJob()
                }
                Lifecycle.Event.ON_PAUSE -> {
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
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    CPUMonitorCard(viewModel)
                }
                item {
                    GPUMonitorCard(viewModel)
                }
                item {
                    CPULittleClusterCard(viewModel)
                }
                if (hasBigCluster) {
                    item {
                        BigClusterCard(viewModel)
                    }
                }
                if (hasPrimeCluster) {
                    item {
                        PrimeClusterCard(viewModel)
                    }
                }
                if (hasCpuInputBoostMs || hasCpuSchedBoostOnInput) {
                    item {
                        CPUBoostCard(viewModel)
                    }
                }
                item {
                    GPUCard(viewModel)
                }
            }
        }
    }
}

@Composable
fun CPUMonitorCard(viewModel: SoCViewModel) {
    val cpuUsage by viewModel.cpuUsage.collectAsStateWithLifecycle()
    val cpuUsageProgress = remember(cpuUsage) {
        if (cpuUsage == "unknown") {
            0f
        } else {
            cpuUsage.replace("%", "").toFloatOrNull()?.div(100f) ?: 0f
        }
    }
    val animatedCpuUsageProgress by animateFloatAsState(
        targetValue = cpuUsageProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    val cpuTemp by viewModel.cpuTemp.collectAsStateWithLifecycle()

    val cpu0State by viewModel.cpu0State.collectAsStateWithLifecycle()
    val bigClusterState by viewModel.bigClusterState.collectAsStateWithLifecycle()
    val primeClusterState by viewModel.primeClusterState.collectAsStateWithLifecycle()

    val hasBigCluster by viewModel.hasBigCluster.collectAsStateWithLifecycle()
    val hasPrimeCluster by viewModel.hasPrimeCluster.collectAsStateWithLifecycle()

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
                        text = "CPU Monitor",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_usage),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null,
                                )
                                Column {
                                    Text(
                                        text = "Usage",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = if (cpuUsage == "N/A") "N/A" else "$cpuUsage%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                    )
                                }
                            }
                            LinearWavyProgressIndicator(
                                progress = { animatedCpuUsageProgress },
                                modifier = Modifier.fillMaxWidth(),
                                trackColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            )
                        }
                    }
                }
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = CircleShape,
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Crossfade(
                                targetState = cpuTemp.toIntOrNull() ?: 0,
                                animationSpec = tween(durationMillis = 500),
                            ) { temp ->
                                if (temp >= 60) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_heat),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        contentDescription = null,
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_cool),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        contentDescription = null,
                                    )
                                }
                            }
                            Text(
                                text = if (cpuTemp == "N/A") "N/A" else "$cpuTemp°C",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }

            Card(
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_speed),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null,
                    )
                    if (!hasBigCluster) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Current frequencies",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = if (cpu0State.currentFreq.isEmpty()) "N/A" else "${cpu0State.currentFreq} MHz",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        Text(
                            text = "Current frequencies",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            if (hasBigCluster) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(Modifier.weight(1f)) {
                        Card(
                            shape = MaterialTheme.shapes.extraLarge,
                        ) {
                            CustomListItem(
                                title = "Little cluster",
                                titleColor = MaterialTheme.colorScheme.onSurface,
                                summary = if (cpu0State.currentFreq.isEmpty()) "N/A" else "${cpu0State.currentFreq} MHz",
                                summaryColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Box(Modifier.weight(1f)) {
                        Card(
                            shape = MaterialTheme.shapes.extraLarge,
                        ) {
                            CustomListItem(
                                title = "Big cluster",
                                titleColor = MaterialTheme.colorScheme.onSurface,
                                summary = if (bigClusterState.currentFreq.isEmpty()) "N/A" else "${bigClusterState.currentFreq} MHz",
                                summaryColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                if (hasPrimeCluster) {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        CustomListItem(
                            title = "Prime cluster",
                            titleColor = MaterialTheme.colorScheme.onSurface,
                            summary = if (primeClusterState.currentFreq.isEmpty()) "N/A" else "${primeClusterState.currentFreq} MHz",
                            summaryColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GPUMonitorCard(viewModel: SoCViewModel) {
    val gpuUsage by viewModel.gpuUsage.collectAsStateWithLifecycle()
    val gpuUsageProgress = remember(gpuUsage) {
        if (gpuUsage == "N/A") {
            0f
        } else {
            gpuUsage.replace("%", "").toFloatOrNull()?.div(100f) ?: 0f
        }
    }
    val animatedGpuUsageProgress by animateFloatAsState(
        targetValue = gpuUsageProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    val gpuTemp by viewModel.gpuTemp.collectAsStateWithLifecycle()
    val gpuState by viewModel.gpuState.collectAsStateWithLifecycle()

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
                        text = "GPU Monitor",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_usage),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null,
                                )
                                Column {
                                    Text(
                                        text = "Usage",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = if (gpuUsage == "N/A") "N/A" else "$gpuUsage%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                    )
                                }
                            }
                            LinearWavyProgressIndicator(
                                progress = { animatedGpuUsageProgress },
                                modifier = Modifier.fillMaxWidth(),
                                trackColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            )
                        }
                    }
                }
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = CircleShape,
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Crossfade(
                                targetState = gpuTemp.toIntOrNull() ?: 0,
                                animationSpec = tween(durationMillis = 500),
                            ) { temp ->
                                if (temp >= 60) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_heat),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        contentDescription = null,
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_cool),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        contentDescription = null,
                                    )
                                }
                            }
                            Text(
                                text = if (gpuTemp == "N/A") "N/A" else "$gpuTemp°C",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }

            Card(
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_speed),
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Current frequencies",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = if (gpuState.currentFreq.isEmpty()) "N/A" else "${gpuState.currentFreq} MHz",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CPULittleClusterCard(viewModel: SoCViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotateArrow by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
    )

    // AMXF = Available Max Frequencies
    val openAMXF = rememberDialogState(initiallyVisible = false)
    // AMNF = Available Min Frequencies
    val openAMNF = rememberDialogState(initiallyVisible = false)
    // ACG = Available CPU Governor
    val openACG = rememberDialogState(initiallyVisible = false)

    val cpu0State by viewModel.cpu0State.collectAsStateWithLifecycle()
    val minFreq = cpu0State.minFreq
    val maxFreq = cpu0State.maxFreq
    val hasBigCluster by viewModel.hasBigCluster.collectAsStateWithLifecycle()

    Card(
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = { expanded = !expanded })
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cpu),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Text(
                text = if (hasBigCluster) "Little Cluster" else "CPU",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = if (expanded) "Expanded" else "Collapsed",
                modifier = Modifier.rotate(rotateArrow),
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            ) + expandVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
            ),
            exit = fadeOut(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            ) + shrinkVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        onClick = { openAMNF.visible = true },
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 8.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_speed),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Min freq",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "$minFreq MHz",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        onClick = { openAMXF.visible = true },
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 28.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        ),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_speed),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Max freq",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "$maxFreq MHz",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
                Button(
                    contentPadding = PaddingValues(16.dp),
                    onClick = { openACG.visible = true },
                    shapes = ButtonDefaults.shapes(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 28.dp,
                            bottomEnd = 28.dp,
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Governor",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                text = cpu0State.gov,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }
        }
    }

    DialogUnstyled(
        state = openAMNF,
        title = {
            Text(
                text = "Available frequencies",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (cpu0State.availableFreq.isNotEmpty()) {
                LazyColumn {
                    items(cpu0State.availableFreq) { freq ->
                        DialogTextButton(
                            text = "$freq MHz",
                            onClick = {
                                viewModel.updateFreq("min", freq, "little")
                                openAMNF.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available frequencies found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openAMNF.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )

    DialogUnstyled(
        state = openAMXF,
        title = {
            Text(
                text = "Available frequencies",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (cpu0State.availableFreq.isNotEmpty()) {
                LazyColumn {
                    items(cpu0State.availableFreq) { freq ->
                        DialogTextButton(
                            text = "$freq MHz",
                            onClick = {
                                viewModel.updateFreq("max", freq, "little")
                                openAMXF.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available frequencies found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openAMXF.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )

    DialogUnstyled(
        state = openACG,
        title = {
            Text(
                text = "Available governor",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (cpu0State.availableGov.isNotEmpty()) {
                LazyColumn {
                    items(cpu0State.availableGov) { gov ->
                        DialogTextButton(
                            text = gov,
                            onClick = {
                                viewModel.updateGov(gov, "little")
                                openACG.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available governor found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openACG.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )
}

@Composable
fun BigClusterCard(viewModel: SoCViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotateArrow by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
    )

    // Available Max Frequencies
    val openAMXF = rememberDialogState(initiallyVisible = false)
    // Available Min Frequencies
    val openAMNF = rememberDialogState(initiallyVisible = false)
    // ACG = Available CPU Governor
    val openACG = rememberDialogState(initiallyVisible = false)

    val bigClusterState by viewModel.bigClusterState.collectAsStateWithLifecycle()
    val minFreq = bigClusterState.minFreq
    val maxFreq = bigClusterState.maxFreq

    Card(
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = { expanded = !expanded })
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cpu),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Text(
                text = "Big Cluster",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = if (expanded) "Expanded" else "Collapsed",
                modifier = Modifier.rotate(rotateArrow)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            ) + expandVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
            ),
            exit = fadeOut(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            ) + shrinkVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 8.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp,
                            )
                        ),
                        onClick = { openAMNF.visible = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_speed),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Min freq",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "$minFreq MHz",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 28.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp,
                            )
                        ),
                        onClick = { openAMXF.visible = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_speed),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Max freq",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "$maxFreq MHz",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
                Button(
                    contentPadding = PaddingValues(16.dp),
                    shapes = ButtonDefaults.shapes(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 28.dp,
                            bottomEnd = 28.dp,
                        )
                    ),
                    onClick = { openACG.visible = true },
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Governor",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                text = bigClusterState.gov,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }
        }
    }

    DialogUnstyled(
        state = openAMNF,
        title = {
            Text(
                text = "Available frequencies",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (bigClusterState.availableFreq.isNotEmpty()) {
                LazyColumn {
                    items(bigClusterState.availableFreq) { freq ->
                        DialogTextButton(
                            text = "$freq MHz",
                            onClick = {
                                viewModel.updateFreq("min", freq, "big")
                                openAMNF.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available frequencies found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openAMNF.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )

    DialogUnstyled(
        state = openAMXF,
        title = {
            Text(
                text = "Available frequencies",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (bigClusterState.availableFreq.isNotEmpty()) {
                LazyColumn {
                    items(bigClusterState.availableFreq) { freq ->
                        DialogTextButton(
                            text = "$freq MHz",
                            onClick = {
                                viewModel.updateFreq("max", freq, "big")
                                openAMXF.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available frequencies found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openAMXF.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )

    DialogUnstyled(
        state = openACG,
        title = {
            Text(
                text = "Available governor",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (bigClusterState.availableGov.isNotEmpty()) {
                LazyColumn {
                    items(bigClusterState.availableGov) { gov ->
                        DialogTextButton(
                            text = gov,
                            onClick = {
                                viewModel.updateGov(gov, "big")
                                openACG.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available governor found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openACG.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )
}

@Composable
fun PrimeClusterCard(viewModel: SoCViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotateArrow by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
    )

    // Available Max Frequencies
    val openAMXF = rememberDialogState(initiallyVisible = false)
    // Available Min Frequencies
    val openAMNF = rememberDialogState(initiallyVisible = false)
    // ACG = Available CPU Governor
    val openACG = rememberDialogState(initiallyVisible = false)

    val primeClusterState by viewModel.primeClusterState.collectAsStateWithLifecycle()
    val minFreq = primeClusterState.minFreq
    val maxFreq = primeClusterState.maxFreq

    Card(
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = { expanded = !expanded })
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cpu),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Text(
                text = "Prime Cluster",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = if (expanded) "Expanded" else "Collapsed",
                modifier = Modifier.rotate(rotateArrow),
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            ) + expandVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
            ),
            exit = fadeOut(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            ) + shrinkVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 8.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp,
                            )
                        ),
                        onClick = { openAMNF.visible = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_speed),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Min freq",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "$minFreq MHz",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 28.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp,
                            )
                        ),
                        onClick = { openAMXF.visible = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_speed),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Max freq",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "$maxFreq MHz",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
                Button(
                    contentPadding = PaddingValues(16.dp),
                    shapes = ButtonDefaults.shapes(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 28.dp,
                            bottomEnd = 28.dp,
                        )
                    ),
                    onClick = { openACG.visible = true },
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Governor",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                text = primeClusterState.gov,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }
        }
    }

    DialogUnstyled(
        state = openAMNF,
        title = {
            Text(
                text = "Available frequencies",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (primeClusterState.availableFreq.isNotEmpty()) {
                LazyColumn {
                    items(primeClusterState.availableFreq) { freq ->
                        DialogTextButton(
                            text = "$freq MHz",
                            onClick = {
                                viewModel.updateFreq("min", freq, "prime")
                                openAMNF.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available frequencies found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openAMNF.visible = true },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )

    DialogUnstyled(
        state = openAMXF,
        title = {
            Text(
                text = "Available frequencies",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (primeClusterState.availableFreq.isNotEmpty()) {
                LazyColumn {
                    items(primeClusterState.availableFreq) { freq ->
                        DialogTextButton(
                            text = "$freq MHz",
                            onClick = {
                                viewModel.updateFreq("max", freq, "prime")
                                openAMXF.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available frequencies found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openAMXF.visible = true },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )

    DialogUnstyled(
        state = openACG,
        title = {
            Text(
                text = "Available governor",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (primeClusterState.availableGov.isNotEmpty()) {
                LazyColumn {
                    items(primeClusterState.availableGov) { gov ->
                        DialogTextButton(
                            text = gov,
                            onClick = {
                                viewModel.updateGov(gov, "prime")
                                openACG.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available governor found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openACG.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )
}

@Composable
fun CPUBoostCard(viewModel: SoCViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotateArrow by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
    )

    // CPU Input Boost Dialog
    val openCIBD = rememberDialogState(initiallyVisible = false)

    val hasCpuInputBoostMs by viewModel.hasCpuInputBoostMs.collectAsStateWithLifecycle()
    val cpuInputBoostMs by viewModel.cpuInputBoostMs.collectAsStateWithLifecycle()
    var cpuInputBoostMsValue by remember { mutableStateOf(cpuInputBoostMs) }

    val hasCpuSchedBoostOnInput by viewModel.hasCpuSchedBoostOnInput.collectAsStateWithLifecycle()
    val cpuSchedBoostOnInput by viewModel.cpuSchedBoostOnInput.collectAsStateWithLifecycle()
    val cpuSchedBoostOnInputChecked = cpuSchedBoostOnInput == "1"

    Card(
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = { expanded = !expanded })
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_rocket_launch),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Text(
                text = "CPU Boost",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = if (expanded) "Expanded" else "Collapsed",
                modifier = Modifier.rotate(rotateArrow),
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            ) + expandVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
            ),
            exit = fadeOut(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            ) + shrinkVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AnimatedVisibility(
                    visible = hasCpuInputBoostMs,
                    enter = fadeIn(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                    ) + expandVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                    exit = fadeOut(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                    ) + shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                ) {
                    Button(
                        contentPadding = PaddingValues(16.dp),
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 28.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp,
                            )
                        ),
                        onClick = { openCIBD.visible = true },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_timer),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Input boost ms",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "$cpuInputBoostMs ms",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = hasCpuSchedBoostOnInput,
                    enter = fadeIn(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                    ) + expandVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                    exit = fadeOut(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                    ) + shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 28.dp,
                            bottomEnd = 28.dp,
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        onClick = { viewModel.updateCpuSchedBoostOnInput(!cpuSchedBoostOnInputChecked) },
                        border = BorderStroke(
                            width = 2.0.dp,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_touch_app),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                contentDescription = null,
                            )
                            Text(
                                text = "Sched boost on input",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f),
                            )
                            Switch(
                                checked = cpuSchedBoostOnInputChecked,
                                onCheckedChange = { isChecked -> viewModel.updateCpuSchedBoostOnInput(isChecked) },
                                thumbContent = {
                                    Crossfade(
                                        targetState = cpuSchedBoostOnInputChecked,
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
            }
        }
    }

    DialogUnstyled(
        state = openCIBD,
        text = {
            OutlinedTextField(
                value = cpuInputBoostMsValue,
                onValueChange = { cpuInputBoostMsValue = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.updateCpuInputBoostMs(cpuInputBoostMsValue)
                        openCIBD.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.updateCpuInputBoostMs(cpuInputBoostMsValue)
                    openCIBD.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openCIBD.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
}

@Composable
fun GPUCard(viewModel: SoCViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotateArrow by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
    )

    // Available Max Frequencies
    val openAMXF = rememberDialogState(initiallyVisible = false)
    // Available Min Frequencies
    val openAMNF = rememberDialogState(initiallyVisible = false)
    // AGG = Available GPU Governor
    val openAGG = rememberDialogState(initiallyVisible = false)
    // ABD = Adreno Boost Dialog
    val openABD = rememberDialogState(initiallyVisible = false)

    val gpuState by viewModel.gpuState.collectAsStateWithLifecycle()
    val hasDefaultPwrlevel by viewModel.hasDefaultPwrlevel.collectAsStateWithLifecycle()
    var defaultPwrlevel by remember { mutableStateOf(gpuState.defaultPwrlevel) }
    val hasAdrenoBoost by viewModel.hasAdrenoBoost.collectAsStateWithLifecycle()
    val hasGPUThrottling by viewModel.hasGPUThrottling.collectAsStateWithLifecycle()
    val gpuThrottlingStatus = remember(gpuState.gpuThrottling) { gpuState.gpuThrottling == "1" }

    val minFreq = gpuState.minFreq
    val maxFreq = gpuState.maxFreq

    val minPwrlevel = gpuState.minPwrlevel.toFloatOrNull() ?: 0f
    val maxPwrlevel = gpuState.maxPwrlevel.toFloatOrNull() ?: 0f

    Card(
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = { expanded = !expanded })
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_video_card),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Text(
                text = "GPU",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = if (expanded) "Expanded" else "Collapsed",
                modifier = Modifier.rotate(rotateArrow),
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            ) + expandVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
            ),
            exit = fadeOut(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
            ) + shrinkVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 8.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp,
                            )
                        ),
                        onClick = { openAMNF.visible = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_speed),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Min freq",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "$minFreq MHz",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 28.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp,
                            )
                        ),
                        onClick = { openAMXF.visible = true },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_speed),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Max freq",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "$maxFreq MHz",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
                Button(
                    contentPadding = PaddingValues(16.dp),
                    shapes = ButtonDefaults.shapes(
                        if (hasAdrenoBoost || hasDefaultPwrlevel || hasGPUThrottling) {
                            RoundedCornerShape(8.dp)
                        } else {
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp,
                                bottomStart = 28.dp,
                                bottomEnd = 28.dp,
                            )
                        }
                    ),
                    onClick = { openAGG.visible = true },
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Governor",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                text = gpuState.gov,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = hasAdrenoBoost,
                    enter = fadeIn(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                    ) + expandVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                    exit = fadeOut(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                    ) + shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                ) {
                    Button(
                        shapes = ButtonDefaults.shapes(
                            if (hasDefaultPwrlevel || hasGPUThrottling) {
                                RoundedCornerShape(8.dp)
                            } else {
                                RoundedCornerShape(
                                    topStart = 8.dp,
                                    topEnd = 8.dp,
                                    bottomStart = 28.dp,
                                    bottomEnd = 28.dp,
                                )
                            }
                        ),
                        onClick = { openABD.visible = true },
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_rocket_launch),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Adreno boost",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = remember(gpuState.adrenoBoost) {
                                        when (gpuState.adrenoBoost) {
                                            "0" -> "Off"
                                            "1" -> "Low"
                                            "2" -> "Medium"
                                            "3" -> "High"
                                            else -> gpuState.adrenoBoost
                                        }
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = hasDefaultPwrlevel,
                    enter = fadeIn(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                    ) + expandVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                    exit = fadeOut(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                    ) + shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                ) {
                    Card(
                        shape = if (hasGPUThrottling) {
                            RoundedCornerShape(8.dp)
                        } else {
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 8.dp,
                                bottomStart = 28.dp,
                                bottomEnd = 28.dp,
                            )
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        border = BorderStroke(
                            width = 2.0.dp,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_tune),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    contentDescription = null,
                                )
                                Column {
                                    Text(
                                        text = "Default pwrlevel",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                    Text(
                                        text = defaultPwrlevel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }
                            Slider(
                                value = defaultPwrlevel.toFloatOrNull() ?: 0f,
                                onValueChange = { newValue ->
                                    defaultPwrlevel = newValue.toInt().toString()
                                },
                                onValueChangeFinished = { viewModel.updateDefaultPwrlevel(defaultPwrlevel) },
                                valueRange = maxPwrlevel..minPwrlevel,
                                colors = SliderDefaults.colors(
                                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                ),
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = hasGPUThrottling,
                    enter = fadeIn(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                    ) + expandVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                    exit = fadeOut(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                    ) + shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 28.dp,
                            bottomEnd = 28.dp,
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        onClick = { viewModel.updateGPUThrottling(!gpuThrottlingStatus) },
                        border = BorderStroke(
                            width = 2.0.dp,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Crossfade(
                                targetState = gpuThrottlingStatus,
                                animationSpec = tween(durationMillis = 500),
                            ) { isChecked ->
                                if (isChecked) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_cool),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        contentDescription = null,
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_heat),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        contentDescription = null,
                                    )
                                }
                            }
                            Text(
                                text = "GPU Throttling",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f),
                            )
                            Switch(
                                checked = gpuThrottlingStatus,
                                onCheckedChange = { isChecked -> viewModel.updateGPUThrottling(isChecked) },
                                thumbContent = {
                                    Crossfade(
                                        targetState = gpuThrottlingStatus,
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
            }
        }
    }

    DialogUnstyled(
        state = openAMNF,
        title = {
            Text(
                text = "Available frequencies",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (gpuState.availableFreq.isNotEmpty()) {
                LazyColumn {
                    items(gpuState.availableFreq.sortedBy { it.toInt() }) { freq ->
                        DialogTextButton(
                            text = "$freq MHz",
                            onClick = {
                                viewModel.updateFreq("min", freq, "gpu")
                                openAMNF.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available frequencies found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openAMNF.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )

    DialogUnstyled(
        state = openAMXF,
        title = {
            Text(
                text = "Available frequencies",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (gpuState.availableFreq.isNotEmpty()) {
                LazyColumn {
                    items(gpuState.availableFreq.sortedBy { it.toInt() }) { freq ->
                        DialogTextButton(
                            text = "$freq MHz",
                            onClick = {
                                viewModel.updateFreq("max", freq, "gpu")
                                openAMXF.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available frequencies found.")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openAMXF.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )

    DialogUnstyled(
        state = openAGG,
        title = {
            Text(
                text = "Available governor",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            if (gpuState.availableGov.isNotEmpty()) {
                LazyColumn {
                    items(gpuState.availableGov) { gov ->
                        DialogTextButton(
                            text = gov,
                            onClick = {
                                viewModel.updateGov(gov, "gpu")
                                openAGG.visible = false
                            },
                        )
                    }
                }
            } else {
                Text("No available governor found.")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { openAGG.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )

    DialogUnstyled(
        state = openABD,
        title = {
            Text(
                text = "Adreno boost",
                style = MaterialTheme.typography.titleLarge,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Column {
                DialogTextButton(
                    text = "Off",
                    onClick = {
                        viewModel.updateAdrenoBoost("0")
                        openABD.visible = false
                    },
                )
                DialogTextButton(
                    text = "Low",
                    onClick = {
                        viewModel.updateAdrenoBoost("1")
                        openABD.visible = false
                    },
                )
                DialogTextButton(
                    text = "Medium",
                    onClick = {
                        viewModel.updateAdrenoBoost("2")
                        openABD.visible = false
                    },
                )
                DialogTextButton(
                    text = "High",
                    onClick = {
                        viewModel.updateAdrenoBoost("3")
                        openABD.visible = false
                    },
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openABD.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Close")
            }
        },
    )
}
