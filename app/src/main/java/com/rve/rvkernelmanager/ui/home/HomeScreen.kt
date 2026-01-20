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
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups3
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_android_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_memory_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_mobile_info_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_shield_rounded_filled
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.ListItemCard
import com.rve.rvkernelmanager.ui.components.SimpleTopAppBar
import com.rve.rvkernelmanager.ui.components.section
import com.rve.rvkernelmanager.ui.contributor.ContributorActivity
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

        val deviceInfoItems = listOf(
            HomeItem(
                icon = painterResource(materialsymbols_ic_mobile_info_rounded_filled),
                title = stringResource(R.string.device),
                body = "${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})",
                onClick = { /* Nothing */ },
                onLongClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    context.getString(R.string.device_codename),
                                    "${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})",
                                ),
                            ),
                        )
                    }
                },
            ),
            HomeItem(
                icon = painterResource(materialsymbols_ic_android_rounded_filled),
                title = stringResource(R.string.android),
                body = "${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})",
                onClick = { /* Nothing */ },
                onLongClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    context.getString(R.string.android_version),
                                    "${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})",
                                ),
                            ),
                        )
                    }
                },
            ),
            HomeItem(
                icon = painterResource(materialsymbols_ic_memory_rounded_filled),
                title = stringResource(R.string.ram),
                body = "${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)",
                onClick = { /* Nothing */ },
                onLongClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    context.getString(R.string.ram),
                                    "${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)",
                                ),
                            ),
                        )
                    }
                }
            ),
            HomeItem(
                icon = painterResource(materialsymbols_ic_memory_rounded_filled),
                title = stringResource(R.string.cpu),
                body = deviceInfo.cpu,
                onClick = { /* Nothing */ },
                onLongClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    context.getString(R.string.cpu),
                                    deviceInfo.cpu,
                                ),
                            ),
                        )
                    }
                }
            ),
            HomeItem(
                icon = painterResource(materialsymbols_ic_memory_rounded_filled),
                title = stringResource(R.string.gpu),
                body = deviceInfo.gpuModel,
                onClick = { /* Nothing */ },
                onLongClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    context.getString(R.string.gpu),
                                    deviceInfo.gpuModel,
                                ),
                            ),
                        )
                    }
                }
            ),
            HomeItem(
                icon = painterResource(materialsymbols_ic_shield_rounded_filled),
                title = stringResource(R.string.wireguard),
                body = deviceInfo.wireGuard,
                onClick = { /* Nothing */ },
                onLongClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    context.getString(R.string.wireguard),
                                    deviceInfo.wireGuard,
                                ),
                            ),
                        )
                    }
                }
            ),
            HomeItem(
                icon = painterResource(R.drawable.ic_linux),
                title = stringResource(R.string.kernel),
                body = if (isFullKernelVersion) deviceInfo.fullKernelVersion else deviceInfo.kernelVersion,
                onClick = { isFullKernelVersion = !isFullKernelVersion },
                onLongClick = {
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    context.getString(R.string.kernel),
                                    if (isFullKernelVersion) deviceInfo.fullKernelVersion else deviceInfo.kernelVersion,
                                ),
                            ),
                        )
                    }
                }
            ),
        )

        val aboutAppItems = listOf(
            HomeItem(
                icon = Icons.Rounded.Groups3,
                title = stringResource(R.string.contributors),
                body = stringResource(R.string.contributors_desc),
                onClick = { context.startActivity(Intent(context, ContributorActivity::class.java)) },
                onLongClick = { /* Nothing */ },
            )
        )

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                section(R.string.device_info_section) {
                    items(deviceInfoItems) { item ->
                        ListItemCard(
                            icon = item.icon,
                            title = item.title,
                            body = item.body,
                            onClick = item.onClick,
                            onLongClick = item.onLongClick,
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                section(R.string.about_app_section) {
                    items(aboutAppItems) { item ->
                        ListItemCard(
                            icon = item.icon,
                            title = item.title,
                            body = item.body,
                            onClick = item.onClick,
                            onLongClick = item.onLongClick,
                        )
                    }
                }
            }
        }
    }
}

private data class HomeItem(
    val icon: Any?,
    val title: String,
    val body: String,
    val onClick: () -> Unit,
    val onLongClick: () -> Unit
)
