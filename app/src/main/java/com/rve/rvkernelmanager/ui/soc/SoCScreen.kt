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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_data_usage_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_dvr_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_emergency_heat_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_memory_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_mode_cool_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_mode_heat_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_rocket_launch_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_speed_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_view_in_ar_rounded_filled
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.Card.ExpandableCard
import com.rve.rvkernelmanager.ui.components.Card.ItemCard
import com.rve.rvkernelmanager.ui.components.SimpleTopAppBar
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationBar

sealed interface SocCardType {
    data object CpuMonitor : SocCardType
    data object GpuMonitor : SocCardType
    data object CpuLittleCluster : SocCardType
    data object CpuBigCluster : SocCardType
    data object CpuPrimeCluster : SocCardType
    data object CpuBoost : SocCardType
    data object GpuInfo : SocCardType
}

@Composable
fun SoCScreen(viewModel: SoCViewModel = viewModel(), navController: NavController) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val hasCpuInputBoostMs by viewModel.hasCpuInputBoostMs.collectAsStateWithLifecycle()
    val hasCpuSchedBoostOnInput by viewModel.hasCpuSchedBoostOnInput.collectAsStateWithLifecycle()
    val hasBigCluster by viewModel.hasBigCluster.collectAsStateWithLifecycle()
    val hasPrimeCluster by viewModel.hasPrimeCluster.collectAsStateWithLifecycle()

    val socCards = remember(hasBigCluster, hasPrimeCluster, hasCpuInputBoostMs, hasCpuSchedBoostOnInput) {
        buildList {
            add(SocCardType.CpuMonitor)
            add(SocCardType.GpuMonitor)
            add(SocCardType.CpuLittleCluster)

            if (hasBigCluster) {
                add(SocCardType.CpuBigCluster)
            }

            if (hasPrimeCluster) {
                add(SocCardType.CpuPrimeCluster)
            }

            if (hasCpuInputBoostMs || hasCpuSchedBoostOnInput) {
                add(SocCardType.CpuBoost)
            }

            add(SocCardType.GpuInfo)
        }
    }

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
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(items = socCards, key = { it.toString() }) { cardType ->

                    when (cardType) {
                        SocCardType.CpuMonitor -> CPUMonitorCard(viewModel)
                        SocCardType.GpuMonitor -> GPUMonitorCard(viewModel)
                        SocCardType.CpuLittleCluster -> CPULittleClusterCard(viewModel)
                        SocCardType.CpuBigCluster -> BigClusterCard(viewModel)
                        SocCardType.CpuPrimeCluster -> PrimeClusterCard(viewModel)
                        SocCardType.CpuBoost -> CPUBoostCard(viewModel)
                        SocCardType.GpuInfo -> GPUCard(viewModel)
                    }
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
            color = MaterialTheme.colorScheme.surfaceBright,
        ),
        colors = CardDefaults.outlinedCardColors(
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
                title = stringResource(R.string.cpu_monitor),
                titleLarge = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(materialsymbols_ic_data_usage_rounded_filled),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        contentDescription = null,
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.usage),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = if (cpuUsage == "N/A") stringResource(R.string.na) else "$cpuUsage%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            LinearWavyProgressIndicator(
                                progress = { animatedCpuUsageProgress },
                                modifier = Modifier.fillMaxWidth(),
                                trackColor = MaterialTheme.colorScheme.surfaceContainer,
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright
                        ),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Crossfade(
                                targetState = cpuTemp.toIntOrNull() ?: 0,
                                animationSpec = tween(durationMillis = 500),
                            ) { temp ->
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (temp >= 60) {
                                        Icon(
                                            painter = painterResource(materialsymbols_ic_emergency_heat_rounded_filled),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            contentDescription = null,
                                        )
                                    } else if (temp >= 50) {
                                        Icon(
                                            painter = painterResource(materialsymbols_ic_mode_heat_rounded_filled),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            contentDescription = null,
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(materialsymbols_ic_mode_cool_rounded_filled),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            contentDescription = null,
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (cpuTemp == "N/A") stringResource(R.string.na) else "$cpuTemp°C",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }

            if (hasBigCluster) {
                ItemCard(
                    shape = CircleShape,
                    icon = painterResource(materialsymbols_ic_speed_rounded_filled),
                    title = stringResource(R.string.current_frequencies),
                )
            } else {
                ItemCard(
                    shape = MaterialTheme.shapes.extraLarge,
                    icon = painterResource(materialsymbols_ic_speed_rounded_filled),
                    title = stringResource(R.string.current_frequencies),
                    body = if (cpu0State.currentFreq.isEmpty()) stringResource(R.string.unknown) else "${cpu0State.currentFreq} MHz",
                )
            }

            if (hasBigCluster) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(Modifier.weight(1f)) {
                        ItemCard(
                            shape = MaterialTheme.shapes.large,
                            title = stringResource(R.string.little_cluster),
                            body = if (cpu0State.currentFreq.isEmpty()) stringResource(R.string.unknown) else "${cpu0State.currentFreq} MHz",
                        )
                    }
                    Box(Modifier.weight(1f)) {
                        ItemCard(
                            shape = MaterialTheme.shapes.large,
                            title = stringResource(R.string.big_cluster),
                            body = if (bigClusterState.currentFreq.isEmpty()) stringResource(R.string.na) else "${bigClusterState.currentFreq} MHz",
                        )
                    }
                }
                if (hasPrimeCluster) {
                    ItemCard(
                        shape = MaterialTheme.shapes.large,
                        title = stringResource(R.string.prime_cluster),
                        body = if (primeClusterState.currentFreq.isEmpty()) stringResource(R.string.unknown) else "${primeClusterState.currentFreq} MHz",
                    )
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
            color = MaterialTheme.colorScheme.surfaceBright,
        ),
        colors = CardDefaults.outlinedCardColors(
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
                title = stringResource(R.string.gpu_monitor),
                titleLarge = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright
                        )
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
                                        text = stringResource(R.string.usage),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = if (gpuUsage == "N/A") stringResource(R.string.na) else "$gpuUsage%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                    )
                                }
                            }
                            LinearWavyProgressIndicator(
                                progress = { animatedGpuUsageProgress },
                                modifier = Modifier.fillMaxWidth(),
                                trackColor = MaterialTheme.colorScheme.surfaceContainer,
                            )
                        }
                    }
                }
                Box(Modifier.weight(1f)) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright
                        )
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
                                text = if (gpuTemp == "N/A") stringResource(R.string.na) else "$gpuTemp°C",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
            ItemCard(
                shape = CircleShape,
                icon = painterResource(materialsymbols_ic_speed_rounded_filled),
                title = stringResource(R.string.current_frequencies),
                body = if (gpuState.currentFreq.isEmpty()) stringResource(R.string.na) else "${gpuState.currentFreq} MHz",
            )
        }
    }
}

@Composable
fun CPULittleClusterCard(viewModel: SoCViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    // AMXF = Available Max Frequencies
    var openAMXF by remember { mutableStateOf(false) }
    // AMNF = Available Min Frequencies
    var openAMNF by remember { mutableStateOf(false) }
    // ACG = Available CPU Governor
    var openACG by remember { mutableStateOf(false) }

    val cpu0State by viewModel.cpu0State.collectAsStateWithLifecycle()
    val minFreq = cpu0State.minFreq
    val maxFreq = cpu0State.maxFreq
    val hasBigCluster by viewModel.hasBigCluster.collectAsStateWithLifecycle()

    ExpandableCard(
        icon = painterResource(materialsymbols_ic_memory_rounded_filled),
        text = stringResource(if (hasBigCluster) R.string.little_cluster else R.string.cpu),
        expanded = expanded,
        onClick = { expanded = !expanded },
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            ) + expandVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
            ),
            exit = fadeOut(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            ) + shrinkVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
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
                        onClick = { openAMNF = true },
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 8.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp,
                            ),
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
                                    text = stringResource(R.string.min_freq),
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
                        onClick = { openAMXF = true },
                        shapes = ButtonDefaults.shapes(
                            RoundedCornerShape(
                                topStart = 8.dp,
                                topEnd = 28.dp,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp,
                            ),
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
                                    text = stringResource(R.string.max_freq),
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
                    onClick = { openACG = true },
                    shapes = ButtonDefaults.shapes(
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 28.dp,
                            bottomEnd = 28.dp,
                        ),
                    ),
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
                                text = stringResource(R.string.governor),
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

    if (openAMNF) {
        AlertDialog(
            onDismissRequest = { openAMNF = false },
            title = {
                Text(
                    text = stringResource(R.string.available_frequencies),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (cpu0State.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(cpu0State.availableFreq) { freq ->
                            Button(
                                onClick = {
                                    viewModel.updateFreq("min", freq, "little")
                                    openAMNF = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp),
                            ) {
                                Text("$freq MHz")
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_frequencies))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openAMNF = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    if (openAMXF) {
        AlertDialog(
            onDismissRequest = { openAMXF = false },
            title = {
                Text(
                    text = stringResource(R.string.available_frequencies),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (cpu0State.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(cpu0State.availableFreq) { freq ->
                            Button(
                                onClick = {
                                    viewModel.updateFreq("max", freq, "little")
                                    openAMXF = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("$freq MHz")
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_frequencies))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openAMXF = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    if (openACG) {
        AlertDialog(
            onDismissRequest = { openACG = false },
            title = {
                Text(
                    text = stringResource(R.string.available_governor),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (cpu0State.availableGov.isNotEmpty()) {
                    LazyColumn {
                        items(cpu0State.availableGov) { gov ->
                            Button(
                                onClick = {
                                    viewModel.updateGov(gov, "little")
                                    openACG = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text(gov)
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_governors))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openACG = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }
}

@Composable
fun BigClusterCard(viewModel: SoCViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Available Max Frequencies
    var openAMXF by remember { mutableStateOf(false) }
    // Available Min Frequencies
    var openAMNF by remember { mutableStateOf(false) }
    // ACG = Available CPU Governor
    var openACG by remember { mutableStateOf(false) }

    val bigClusterState by viewModel.bigClusterState.collectAsStateWithLifecycle()
    val minFreq = bigClusterState.minFreq
    val maxFreq = bigClusterState.maxFreq

    ExpandableCard(
        icon = painterResource(materialsymbols_ic_memory_rounded_filled),
        text = stringResource(R.string.big_cluster),
        expanded = expanded,
        onClick = { expanded = !expanded }
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            ) + expandVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
            ),
            exit = fadeOut(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            ) + shrinkVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
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
                            ),
                        ),
                        onClick = { openAMNF = true },
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
                                    text = stringResource(R.string.min_freq),
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
                            ),
                        ),
                        onClick = { openAMXF = true },
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
                                    text = stringResource(R.string.max_freq),
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
                        ),
                    ),
                    onClick = { openACG = true },
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
                                text = stringResource(R.string.governor),
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

    if (openAMNF) {
        AlertDialog(
            onDismissRequest = { openAMNF = false },
            title = {
                Text(
                    text = stringResource(R.string.available_frequencies),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (bigClusterState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(bigClusterState.availableFreq) { freq ->
                            Button(
                                onClick = {
                                    viewModel.updateFreq("min", freq, "big")
                                    openAMNF = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("$freq MHz")
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_frequencies))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openAMNF = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    if (openAMXF) {
        AlertDialog(
            onDismissRequest = { openAMXF = false },
            title = {
                Text(
                    text = stringResource(R.string.available_frequencies),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (bigClusterState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(bigClusterState.availableFreq) { freq ->
                            Button(
                                onClick = {
                                    viewModel.updateFreq("max", freq, "big")
                                    openAMXF = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("$freq MHz")
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_frequencies))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openAMXF = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    if (openACG) {
        AlertDialog(
            onDismissRequest = { openACG = false },
            title = {
                Text(
                    text = stringResource(R.string.available_governor),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (bigClusterState.availableGov.isNotEmpty()) {
                    LazyColumn {
                        items(bigClusterState.availableGov) { gov ->
                            Button(
                                onClick = {
                                    viewModel.updateGov(gov, "big")
                                    openACG = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text(gov)
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_governors))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openACG = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }
}

@Composable
fun PrimeClusterCard(viewModel: SoCViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Available Max Frequencies
    var openAMXF by remember { mutableStateOf(false) }
    // Available Min Frequencies
    var openAMNF by remember { mutableStateOf(false) }
    // ACG = Available CPU Governor
    var openACG by remember { mutableStateOf(false) }

    val primeClusterState by viewModel.primeClusterState.collectAsStateWithLifecycle()
    val minFreq = primeClusterState.minFreq
    val maxFreq = primeClusterState.maxFreq

    ExpandableCard(
        icon = painterResource(materialsymbols_ic_memory_rounded_filled),
        text = stringResource(R.string.prime_cluster),
        expanded = expanded,
        onClick = { expanded = !expanded }
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            ) + expandVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
            ),
            exit = fadeOut(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            ) + shrinkVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
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
                            ),
                        ),
                        onClick = { openAMNF = true },
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
                                    text = stringResource(R.string.min_freq),
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
                            ),
                        ),
                        onClick = { openAMXF = true },
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
                                    text = stringResource(R.string.max_freq),
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
                        ),
                    ),
                    onClick = { openACG = true },
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
                                text = stringResource(R.string.governor),
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

    if (openAMNF) {
        AlertDialog(
            onDismissRequest = { openAMNF = false },
            title = {
                Text(
                    text = stringResource(R.string.available_frequencies),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (primeClusterState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(primeClusterState.availableFreq) { freq ->
                            Button(
                                onClick = {
                                    viewModel.updateFreq("min", freq, "prime")
                                    openAMNF = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("$freq MHz")
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_frequencies))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openAMNF = true },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    if (openAMXF) {
        AlertDialog(
            onDismissRequest = { openAMXF = false },
            title = {
                Text(
                    text = stringResource(R.string.available_frequencies),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (primeClusterState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(primeClusterState.availableFreq) { freq ->
                            Button(
                                onClick = {
                                    viewModel.updateFreq("max", freq, "prime")
                                    openAMXF = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("$freq MHz")
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_frequencies))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openAMXF = true },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    if (openACG) {
        AlertDialog(
            onDismissRequest = { openACG = false },
            title = {
                Text(
                    text = stringResource(R.string.available_governor),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (primeClusterState.availableGov.isNotEmpty()) {
                    LazyColumn {
                        items(primeClusterState.availableGov) { gov ->
                            Button(
                                onClick = {
                                    viewModel.updateGov(gov, "prime")
                                    openACG = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text(gov)
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_governors))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openACG = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }
}

@Composable
fun CPUBoostCard(viewModel: SoCViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    // CPU Input Boost Dialog
    var openCIBD by remember { mutableStateOf(false) }

    val hasCpuInputBoostMs by viewModel.hasCpuInputBoostMs.collectAsStateWithLifecycle()
    val cpuInputBoostMs by viewModel.cpuInputBoostMs.collectAsStateWithLifecycle()
    var cpuInputBoostMsValue by remember { mutableStateOf(cpuInputBoostMs) }

    val hasCpuSchedBoostOnInput by viewModel.hasCpuSchedBoostOnInput.collectAsStateWithLifecycle()
    val cpuSchedBoostOnInput by viewModel.cpuSchedBoostOnInput.collectAsStateWithLifecycle()
    val cpuSchedBoostOnInputChecked = cpuSchedBoostOnInput == "1"

    ExpandableCard(
        icon = painterResource(materialsymbols_ic_rocket_launch_rounded_filled),
        text = stringResource(R.string.cpu_boost),
        expanded = expanded,
        onClick = { expanded = !expanded }
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            ) + expandVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
            ),
            exit = fadeOut(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            ) + shrinkVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AnimatedVisibility(
                    visible = hasCpuInputBoostMs,
                    enter = fadeIn(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    ) + expandVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                    ),
                    exit = fadeOut(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    ) + shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
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
                            ),
                        ),
                        onClick = { openCIBD = true },
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
                                    text = stringResource(R.string.input_boost_ms),
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
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    ) + expandVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                    ),
                    exit = fadeOut(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    ) + shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
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
                                text = stringResource(R.string.sched_boost_input),
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

    if (openCIBD) {
        AlertDialog(
            onDismissRequest = { openCIBD = false },
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
                            openCIBD = false
                        },
                    ),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateCpuInputBoostMs(cpuInputBoostMsValue)
                        openCIBD = false
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.change))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openCIBD = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
fun GPUCard(viewModel: SoCViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Available Max Frequencies
    var openAMXF by remember { mutableStateOf(false) }
    // Available Min Frequencies
    var openAMNF by remember { mutableStateOf(false) }
    // AGG = Available GPU Governor
    var openAGG by remember { mutableStateOf(false) }
    // ABD = Adreno Boost Dialog
    var openABD by remember { mutableStateOf(false) }

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

    ExpandableCard(
        icon = painterResource(materialsymbols_ic_view_in_ar_rounded_filled),
        text = stringResource(R.string.gpu),
        expanded = expanded,
        onClick = { expanded = !expanded }
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            ) + expandVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
            ),
            exit = fadeOut(
                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
            ) + shrinkVertically(
                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
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
                            ),
                        ),
                        onClick = { openAMNF = true },
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
                                    text = stringResource(R.string.min_freq),
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
                            ),
                        ),
                        onClick = { openAMXF = true },
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
                                    text = stringResource(R.string.max_freq),
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
                        },
                    ),
                    onClick = { openAGG = true },
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
                                text = stringResource(R.string.governor),
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
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    ) + expandVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                    ),
                    exit = fadeOut(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    ) + shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
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
                            },
                        ),
                        onClick = { openABD = true },
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
                                    text = stringResource(R.string.adreno_boost),
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
                                    }.let {
                                        when(it) {
                                            "Off" -> stringResource(R.string.off)
                                            "Low" -> stringResource(R.string.low)
                                            "Medium" -> stringResource(R.string.medium)
                                            "High" -> stringResource(R.string.high)
                                            else -> it
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
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    ) + expandVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                    ),
                    exit = fadeOut(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    ) + shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
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
                                        text = stringResource(R.string.default_pwrlevel),
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
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    ) + expandVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                    ),
                    exit = fadeOut(
                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                    ) + shrinkVertically(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
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
                                text = stringResource(R.string.gpu_throttling),
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

    if (openAMNF) {
        AlertDialog(
            onDismissRequest = { openAMNF = false },
            title = {
                Text(
                    text = stringResource(R.string.available_frequencies),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (gpuState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(gpuState.availableFreq.sortedBy { it.toInt() }) { freq ->
                            Button(
                                onClick = {
                                    viewModel.updateFreq("min", freq, "gpu")
                                    openAMNF = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("$freq MHz")
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_frequencies))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openAMNF = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    if (openAMXF) {
        AlertDialog(
            onDismissRequest = { openAMXF = false },
            title = {
                Text(
                    text = stringResource(R.string.available_frequencies),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (gpuState.availableFreq.isNotEmpty()) {
                    LazyColumn {
                        items(gpuState.availableFreq.sortedBy { it.toInt() }) { freq ->
                            Button(
                                onClick = {
                                    viewModel.updateFreq("max", freq, "gpu")
                                    openAMXF = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("$freq MHz")
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_frequencies))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openAMXF = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    if (openAGG) {
        AlertDialog(
            onDismissRequest = { openAGG = false },
            title = {
                Text(
                    text = stringResource(R.string.available_governor),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                if (gpuState.availableGov.isNotEmpty()) {
                    LazyColumn {
                        items(gpuState.availableGov) { gov ->
                            Button(
                                onClick = {
                                    viewModel.updateGov(gov, "gpu")
                                    openAGG = false
                                },
                                shapes = ButtonDefaults.shapes(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text(gov)
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.no_governors))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openAGG = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    if (openABD) {
        AlertDialog(
            onDismissRequest = { openABD = false },
            title = {
                Text(
                    text = stringResource(R.string.adreno_boost),
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertDialogDefaults.titleContentColor,
                )
            },
            text = {
                Column {
                    Button(
                        onClick = {
                            viewModel.updateAdrenoBoost("0")
                            openABD = false
                        },
                        shapes = ButtonDefaults.shapes(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(stringResource(R.string.off))
                    }
                    Button(
                        onClick = {
                            viewModel.updateAdrenoBoost("1")
                            openABD = false
                        },
                        shapes = ButtonDefaults.shapes(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(stringResource(R.string.low))
                    }
                    Button(
                        onClick = {
                            viewModel.updateAdrenoBoost("2")
                            openABD = false
                        },
                        shapes = ButtonDefaults.shapes(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(stringResource(R.string.medium))
                    }
                    Button(
                        onClick = {
                            viewModel.updateAdrenoBoost("3")
                            openABD = false
                        },
                        shapes = ButtonDefaults.shapes(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(stringResource(R.string.high))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { openABD = false },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }
}
