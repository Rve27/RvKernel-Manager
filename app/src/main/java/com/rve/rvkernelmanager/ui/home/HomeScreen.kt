/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.CustomListItem
import com.rve.rvkernelmanager.ui.components.PinnedTopAppBar
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationBar

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
        topBar = { PinnedTopAppBar(scrollBehavior = scrollBehavior) },
        bottomBar = { BottomNavigationBar(navController) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        val clipboardManager = LocalClipboardManager.current

        var isFullKernelVersion by rememberSaveable { mutableStateOf(false) }

        val deviceInfo by viewModel.deviceInfo.collectAsState()

        val deviceInfoList = listOf(
            DeviceInfoItem(
                title = "Device",
                summary = "${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})",
                icon = painterResource(R.drawable.ic_smartphone),
                onClick = { /* do nothing */ },
                onLongClick = {
                    clipboardManager.setText(
                        AnnotatedString("${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})"),
                    )
                },
            ),
            DeviceInfoItem(
                title = "Android",
                summary = "${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})",
                icon = painterResource(R.drawable.ic_android),
                onClick = { /* do nothing */ },
                onLongClick = { clipboardManager.setText(AnnotatedString("${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})")) },
            ),
            DeviceInfoItem(
                title = "CPU",
                summary = deviceInfo.cpu,
                icon = painterResource(R.drawable.ic_cpu),
                onClick = { /* do nothing */ },
                onLongClick = { clipboardManager.setText(AnnotatedString(deviceInfo.cpu)) },
            ),
            DeviceInfoItem(
                title = "GPU",
                summary = deviceInfo.gpuModel,
                icon = painterResource(R.drawable.ic_video_card),
                onClick = { /* do nothing */ },
                onLongClick = { clipboardManager.setText(AnnotatedString(deviceInfo.gpuModel)) },
            ),
            DeviceInfoItem(
                title = "RAM",
                summary = "${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)",
                icon = painterResource(R.drawable.ic_ram),
                onClick = { /* do nothing */ },
                onLongClick = { clipboardManager.setText(AnnotatedString("${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)")) },
            ),
        )

        val wireGuardInfo = DeviceInfoItem(
            title = "WireGuard",
            summary = deviceInfo.wireGuard,
            icon = painterResource(R.drawable.ic_shield),
            onClick = { /* do nothing */ },
            onLongClick = { clipboardManager.setText(AnnotatedString(deviceInfo.wireGuard)) },
        )

        val kernelInfo = DeviceInfoItem(
            title = "Kernel",
            summary = if (isFullKernelVersion) deviceInfo.fullKernelVersion else deviceInfo.kernelVersion,
            icon = painterResource(R.drawable.ic_linux),
            onClick = { isFullKernelVersion = !isFullKernelVersion },
            onLongClick = {
                clipboardManager.setText(
                    AnnotatedString(if (isFullKernelVersion) deviceInfo.fullKernelVersion else deviceInfo.kernelVersion),
                )
            },
            animateContentSize = true,
        )

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
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
                AnimatedVisibility(
                    visible = deviceInfo.hasWireGuard,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 },
                ) {
                    DeviceInfoItemCard(item = wireGuardInfo)
                }
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

@Composable
fun DeviceInfoTitle() {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
        ),
        border = BorderStroke(
            width = 1.0.dp,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        CustomListItem(
            title = "Device Information",
            titleColor = MaterialTheme.colorScheme.onSecondaryContainer,
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
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
        ),
        border = BorderStroke(
            width = 1.0.dp,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ),
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .combinedClickable(
                onClick = item.onClick,
                onLongClick = item.onLongClick,
            ),
    ) {
        CustomListItem(
            icon = item.icon,
            iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            title = item.title,
            titleColor = MaterialTheme.colorScheme.onSecondaryContainer,
            summary = item.summary,
            animateContentSize = item.animateContentSize,
        )
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
    val kofiLink = "https://ko-fi.com/rve27"

    TooltipBox(
        positionProvider = positionProvider,
        tooltip = { PlainTooltip(caretShape = TooltipDefaults.caretShape()) { Text(kofiLink) } },
        state = tooltipState,
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
            ),
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, kofiLink.toUri()))
            },
            border = BorderStroke(
                width = 1.0.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
            ),
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
