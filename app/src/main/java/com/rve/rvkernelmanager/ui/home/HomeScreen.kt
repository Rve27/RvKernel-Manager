/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
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
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = rememberLazyListState(),
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                DeviceInfoCard(viewModel)
            }
            item {
                DonateCard()
            }
            item {
                Spacer(Modifier)
            }
        }
    }
}

@Composable
fun DeviceInfoCard(viewModel: HomeViewModel) {
    val clipboardManager = LocalClipboardManager.current

    val deviceInfo by viewModel.deviceInfo.collectAsState()

    var isExtendCpuInfo by rememberSaveable { mutableStateOf(false) }
    var isFullKernelVersion by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        CustomListItem(
            title = "Device Information",
            titleLarge = true,
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        CustomListItem(
            title = "Device",
            summary = "${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})",
            icon = Icons.Filled.Smartphone,
            onLongClick = {
                clipboardManager.setText(
                    AnnotatedString("${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})"),
                )
            },
        )

        CustomListItem(
            title = "RAM",
            summary = "${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)",
            icon = painterResource(R.drawable.ic_ram),
            onLongClick = { clipboardManager.setText(AnnotatedString("${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)")) },
        )

        CustomListItem(
            title = "CPU",
            summary = if (isExtendCpuInfo) deviceInfo.extendCpu else deviceInfo.cpu,
            icon = painterResource(R.drawable.ic_cpu),
            onClick = { isExtendCpuInfo = !isExtendCpuInfo },
            onLongClick = { clipboardManager.setText(AnnotatedString(if (isExtendCpuInfo) deviceInfo.extendCpu else deviceInfo.cpu)) },
            animateContentSize = true,
        )

        CustomListItem(
            title = "GPU",
            summary = deviceInfo.gpuModel,
            icon = painterResource(R.drawable.ic_video_card),
            onLongClick = { clipboardManager.setText(AnnotatedString(deviceInfo.gpuModel)) },
        )

        CustomListItem(
            title = "Android version",
            summary = "${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})",
            icon = Icons.Filled.Android,
            onLongClick = { clipboardManager.setText(AnnotatedString("${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})")) },
        )

        AnimatedVisibility(visible = deviceInfo.hasWireGuard) {
            CustomListItem(
                title = "WireGuard VPN version",
                summary = deviceInfo.wireGuard,
                icon = Icons.Filled.Shield,
                onLongClick = { clipboardManager.setText(AnnotatedString(deviceInfo.wireGuard)) },
            )
        }

        CustomListItem(
            title = "Kernel version",
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
    }
}

@Composable
fun DonateCard() {
    val context = LocalContext.current

    val summary =
        "I wouldn’t be here without you. Every bit of support helps me keep creating, and I appreciate it more than words can say!"
    val kofiLink = "https://ko-fi.com/rve27"

    TooltipBox(
        positionProvider =
        TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above,
        ),
        tooltip = { PlainTooltip(caretShape = TooltipDefaults.caretShape()) { Text(kofiLink) } },
        state = rememberTooltipState(),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, kofiLink.toUri())) },
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
