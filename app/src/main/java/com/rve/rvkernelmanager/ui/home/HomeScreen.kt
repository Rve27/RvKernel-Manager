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

package com.rve.rvkernelmanager.ui.home

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.composables.icons.materialsymbols.roundedfilled.R
import com.rve.rvkernelmanager.R.drawable.ic_linux
import com.rve.rvkernelmanager.ui.components.ListItemCard
import com.rve.rvkernelmanager.ui.components.Section
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

        val deviceInfo by viewModel.deviceInfo.collectAsStateWithLifecycle()

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
            ) {
                item {
                    Section("Device Information") {
                        ListItemCard(
                            icon = painterResource(R.drawable.materialsymbols_ic_mobile_info_rounded_filled),
                            title = "Device",
                            body = "${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})",
                            onClick = { /* Nothing */ },
                            onLongClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "Device codename",
                                                "${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})",
                                            ),
                                        ),
                                    )
                                }
                            },
                        )
                        ListItemCard(
                            icon = painterResource(R.drawable.materialsymbols_ic_android_rounded_filled),
                            title = "Android",
                            body = "${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})",
                            onClick = { /* Nothing */ },
                            onLongClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            ClipData.newPlainText(
                                                "Android version",
                                                "${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})",
                                            ),
                                        ),
                                    )
                                }
                            },
                        )
                        ListItemCard(
                            icon = painterResource(R.drawable.materialsymbols_ic_memory_rounded_filled),
                            title = "RAM",
                            body = "${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)",
                            onClick = { /* Nothing */ },
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
                        )
                        ListItemCard(
                            icon = painterResource(R.drawable.materialsymbols_ic_memory_rounded_filled),
                            title = "CPU",
                            body = deviceInfo.cpu,
                            onClick = { /* Nothing */ },
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
                        ListItemCard(
                            icon = painterResource(R.drawable.materialsymbols_ic_memory_rounded_filled),
                            title = "GPU",
                            body = deviceInfo.gpuModel,
                            onClick = { /* Nothing */ },
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
                        AnimatedVisibility(
                            visible = deviceInfo.hasWireGuard,
                            enter = fadeIn(
                                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                            ) + slideInVertically(
                                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                            ),
                            exit = fadeOut(
                                animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
                            ) + slideOutVertically(
                                animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                            ),
                        ) {
                            ListItemCard(
                                icon = painterResource(R.drawable.materialsymbols_ic_shield_rounded_filled),
                                title = "WireGuard",
                                body = deviceInfo.wireGuard,
                                onClick = { /* Nothing */ },
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
                        }
                        ListItemCard(
                            icon = painterResource(ic_linux),
                            title = "Kernel",
                            body = if (isFullKernelVersion) deviceInfo.fullKernelVersion else deviceInfo.kernelVersion,
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
                        )
                    }
                }
            }
        }
    }
}
