/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.home

import android.content.ClipData
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.CustomListItem
import com.rve.rvkernelmanager.ui.components.SimpleTopAppBar
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationBar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.loadDeviceInfo(context)
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
        val clipboard = LocalClipboard.current
        val coroutineScope = rememberCoroutineScope()

        var isFullKernelVersion by rememberSaveable { mutableStateOf(false) }

        val deviceInfo by viewModel.deviceInfo.collectAsState()

        val deviceInfoList = listOf(
            DeviceInfoItem(
                title = "Device",
                summary = "${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})",
                icon = painterResource(R.drawable.ic_smartphone),
                onClick = { /* do nothing */ },
                onLongClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    "Device",
                                    "${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})",
                                ),
                            ),
                        )
                    }
                },
            ),
            DeviceInfoItem(
                title = "Android",
                summary = "${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})",
                icon = painterResource(R.drawable.ic_android),
                onClick = { /* do nothing */ },
                onLongClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    "Android",
                                    "${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})",
                                ),
                            ),
                        )
                    }
                },
            ),
            DeviceInfoItem(
                title = "RAM",
                summary = "${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)",
                icon = painterResource(R.drawable.ic_ram),
                onClick = { /* do nothing */ },
                onLongClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    "RAM",
                                    "${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)",
                                ),
                            ),
                        )
                    }
                },
            ),
        )

        val wireGuardInfo = DeviceInfoItem(
            title = "WireGuard",
            summary = deviceInfo.wireGuard,
            icon = painterResource(R.drawable.ic_shield),
            onClick = { /* do nothing */ },
            onLongClick = {
                coroutineScope.launch {
                    clipboard.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText(
                                "WireGuard",
                                deviceInfo.wireGuard,
                            ),
                        ),
                    )
                }
            },
        )

        val cpuInfo = DeviceInfoItem(
            title = "CPU",
            summary = deviceInfo.cpu,
            icon = painterResource(R.drawable.ic_cpu),
            onClick = { /* do nothing */ },
            onLongClick = {
                coroutineScope.launch {
                    clipboard.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText(
                                "CPU",
                                deviceInfo.cpu,
                            ),
                        ),
                    )
                }
            },
        )

        val gpuInfo = DeviceInfoItem(
            title = "GPU",
            summary = deviceInfo.gpuModel,
            icon = painterResource(R.drawable.ic_video_card),
            onClick = { /* do nothing */ },
            onLongClick = {
                coroutineScope.launch {
                    clipboard.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText(
                                "GPU",
                                deviceInfo.gpuModel,
                            ),
                        ),
                    )
                }
            },
        )

        val kernelInfo = DeviceInfoItem(
            title = "Kernel",
            summary = if (isFullKernelVersion) deviceInfo.fullKernelVersion else deviceInfo.kernelVersion,
            icon = painterResource(R.drawable.ic_linux),
            onClick = { isFullKernelVersion = !isFullKernelVersion },
            onLongClick = {
                coroutineScope.launch {
                    clipboard.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText(
                                "Kernel",
                                if (isFullKernelVersion) deviceInfo.fullKernelVersion else deviceInfo.kernelVersion,
                            ),
                        ),
                    )
                }
            },
            animateContentSize = true,
        )

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    DeviceInfoTitle()
                }

                items(deviceInfoList) { item ->
                    DeviceInfoItemCard(item = item)
                }

                item {
                    AnimatedVisibility(deviceInfo.hasWireGuard) {
                        DeviceInfoItemCard(item = wireGuardInfo)
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    DeviceInfoItemCard(item = cpuInfo)
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    DeviceInfoItemCard(item = gpuInfo)
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    DeviceInfoItemCard(item = kernelInfo)
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    DonateCard()
                }
            }
        }
    }
}

@Composable
fun DeviceInfoTitle() {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        CustomListItem(
            title = "Device Information",
            titleColor = MaterialTheme.colorScheme.onTertiaryContainer,
            titleLarge = true,
        )
    }
}

data class DeviceInfoItem(
    val title: String,
    val summary: String,
    val icon: Painter,
    val onClick: () -> Unit,
    val onLongClick: () -> Unit,
    val animateContentSize: Boolean = false,
)

@Composable
fun DeviceInfoItemCard(item: DeviceInfoItem) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .combinedClickable(
                onClick = item.onClick,
                onLongClick = item.onLongClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(end = 16.dp),
            )
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.then(
                        if (item.animateContentSize) Modifier.animateContentSize() else Modifier,
                    ),
                )
            }
        }
    }
}

@Composable
fun DonateCard() {
    val context = LocalContext.current

    val tooltipState = rememberTooltipState()
    val positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
        TooltipAnchorPosition.Above,
    )

    val summary =
        "I wouldn’t be here without you. Every bit of support helps me keep creating, and I appreciate it more than words can say!"
    val donateLink = "https://t.me/rve_enterprises/33652/90188"

    TooltipBox(
        positionProvider = positionProvider,
        tooltip = { PlainTooltip(caretShape = TooltipDefaults.caretShape()) { Text(donateLink) } },
        state = tooltipState,
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, donateLink.toUri()))
            },
        ) {
            CustomListItem(
                icon = painterResource(R.drawable.ic_kofi),
                iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                title = "Buy Me a Coffee",
                titleColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            CustomListItem(
                summary = summary,
                summaryColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
