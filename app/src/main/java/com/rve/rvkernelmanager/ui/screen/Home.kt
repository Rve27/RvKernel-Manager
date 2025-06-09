package com.rve.rvkernelmanager.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.ui.ViewModel.HomeViewModel
import com.rve.rvkernelmanager.utils.Utils.CustomItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

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
        topBar = { TopBar(scrollBehavior = scrollBehavior) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
	    state = rememberLazyListState()
        ) {
	    item {
                    DeviceInfoCard(viewModel)
	    }
	    item {
                    DonateCard()
	    }
	    item {
                    CopyrightCard()
	    }
	    item {
                    Spacer(Modifier)
	    }
        }
    }
}

@Composable
fun DeviceInfoCard(viewModel: HomeViewModel) {
    val deviceCodename by viewModel.deviceCodename.collectAsState()
    val ramInfo by viewModel.ramInfo.collectAsState()
    val cpu by viewModel.cpu.collectAsState()
    val gpuModel by viewModel.gpuModel.collectAsState()
    val androidVersion by viewModel.androidVersion.collectAsState()
    val kernelVersion by viewModel.kernelVersion.collectAsState()
    val isCPUInfo by viewModel.isExtendCPUInfo.collectAsState()
    val isFullKernelVersion by viewModel.isFullKernelVersion.collectAsState()

    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Device Information",
                style = MaterialTheme.typography.titleLarge
            )

            HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))

            CustomItem(
                title = "Codename",
                body = deviceCodename,
                icon = painterResource(R.drawable.ic_smartphone)
            )
            Spacer(Modifier.height(16.dp))

            CustomItem(
                title = "RAM",
                body = ramInfo,
                icon = painterResource(R.drawable.ic_ram)
            )
            Spacer(Modifier.height(16.dp))

            CustomItem(
                title = "CPU",
                body = cpu,
                icon = painterResource(R.drawable.ic_cpu),
                onClick = { viewModel.showCPUInfo() },
                animateContent = true
            )
            Spacer(Modifier.height(16.dp))

            CustomItem(
                title = "GPU",
                body = gpuModel,
                icon = painterResource(R.drawable.ic_video_card)
            )
            Spacer(Modifier.height(16.dp))

            CustomItem(
                title = "Android version",
                body = androidVersion,
                icon = painterResource(R.drawable.ic_android)
            )
            Spacer(Modifier.height(16.dp))

            CustomItem(
                title = "Kernel version",
                body = kernelVersion,
                icon = painterResource(R.drawable.ic_linux),
                onClick = { viewModel.showFullKernelVersion() },
                animateContent = true
            )
        }
    }
}

@Composable
fun DonateCard() {
    var showDonateDialog by remember { mutableStateOf(false) }
    var showDanaQR by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.clickable { showDonateDialog = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Donate",
                    style = MaterialTheme.typography.titleLarge
                )

                HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))

                CustomItem(
                    body = "Click this donate card if you want to donate or buy me a coffee.",
                    icon = painterResource(R.drawable.ic_donate),
                    onClick = { showDonateDialog = true }
                )
            }
        }
    }

    if (showDonateDialog) {
        AlertDialog(
            onDismissRequest = { showDonateDialog = false },
            tonalElevation = 8.dp,
            title = { 
		Text(
		    text = "Donate",
		    style = MaterialTheme.typography.titleLarge
		)
	    },

            text = {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "PayPal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/rve27"))
                            context.startActivity(intent)
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Dana",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            showDanaQR = true
                            showDonateDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDonateDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showDanaQR) {
        AlertDialog(
            onDismissRequest = { showDanaQR = false },
            tonalElevation = 8.dp,
            properties = DialogProperties(dismissOnClickOutside = true),
            title = {
		Text(
		    text = "Dana",
		    style = MaterialTheme.typography.titleLarge
		)
	    },

            text = {
                Image(
                    painter = painterResource(id = R.drawable.dana_qr),
                    contentDescription = "Dana QR Code",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Fit
                )
            },
            confirmButton = {
                TextButton(onClick = { showDanaQR = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun CopyrightCard() {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                CustomItem(
                    title = "License",
                    titleLarge = true,
                    icon = painterResource(R.drawable.ic_license)
                )

                HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))

                Text(
                    text = """
                        Copyright (C) 2025 Rve

                        This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

                        This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

                        You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
