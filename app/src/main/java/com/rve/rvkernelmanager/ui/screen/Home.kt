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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.navigation.*
import com.rve.rvkernelmanager.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner,
    navController: NavController
) {
    val context = LocalContext.current
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
        topBar = {
	    PinnedTopAppBar(scrollBehavior = scrollBehavior)
	},
	bottomBar = {
            BottomNavigationBar(navController)
        },
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
		    Spacer(Modifier.height(16.dp))
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
    val device by viewModel.device.collectAsState()
    val ram by viewModel.ram.collectAsState()
    val cpu by viewModel.cpu.collectAsState()
    val gpu by viewModel.gpu.collectAsState()
    val android by viewModel.android.collectAsState()
    val kernel by viewModel.kernel.collectAsState()

    Card(
	modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
	    headlineContent = {
                Text(
                    text = "Device Information",
                    style = MaterialTheme.typography.titleLarge
                )
	    },
	    colors = ListItemDefaults.colors(
		CardDefaults.cardColors().containerColor
	    )
	)
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

	ListItem(
	    headlineContent = {
		Text(
		    text = "Device",
		    style = MaterialTheme.typography.titleMedium
		)
	    },
	    supportingContent = {
		Text(
		    text = device,
		    style = MaterialTheme.typography.bodyMedium
		)
	    },
	    leadingContent = {
	        Icon(
		    painterResource(R.drawable.ic_smartphone),
		    contentDescription = null
		)
	    },
	    colors = ListItemDefaults.colors(
		CardDefaults.cardColors().containerColor
	    )
	)

	ListItem(
	    headlineContent = {
		Text(
		    text = "RAM",
		    style = MaterialTheme.typography.titleMedium
		)
	    },
	    supportingContent = {
		Text(
		    text = ram,
		    style = MaterialTheme.typography.bodyMedium
		)
	    },
	    leadingContent = {
	        Icon(
		    painter = painterResource(R.drawable.ic_ram),
		    contentDescription = null
		)
	    },
	    colors = ListItemDefaults.colors(
		CardDefaults.cardColors().containerColor
	    )
	)

	ListItem(
	    headlineContent = {
		Text(
		    text = "CPU",
                    style = MaterialTheme.typography.titleMedium
                )
	    },
	    supportingContent = {
                Text(
                    text = cpu,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
	    leadingContent = {
                Icon(
                    painter = painterResource(R.drawable.ic_cpu),
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                CardDefaults.cardColors().containerColor
            )
        )


	ListItem(
	    headlineContent = {
		Text(
		    text = "GPU",
		    style = MaterialTheme.typography.titleMedium
		)
	    },
	    supportingContent = {
		Text(
		    text = gpu,
		    style = MaterialTheme.typography.bodyMedium
		)
	    },
	    leadingContent = {
		Icon(
		    painter = painterResource(R.drawable.ic_video_card),
		    contentDescription = null
		)
	    },
	    colors = ListItemDefaults.colors(
                CardDefaults.cardColors().containerColor
            )
	)

	ListItem(
            headlineContent = {
                Text(
                    text = "Android version",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Text(
                    text = android,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingContent = {
                Icon(
                    painter = painterResource(R.drawable.ic_android),
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                CardDefaults.cardColors().containerColor
            )
        )

	ListItem(
            headlineContent = {
                Text(
                    text = "Kernel version",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Text(
                    text = kernel,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingContent = {
                Icon(
                    painter = painterResource(R.drawable.ic_linux),
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                CardDefaults.cardColors().containerColor
            )
        )
    }
}

@Composable
fun DonateCard() {
    var showDonateDialog by remember { mutableStateOf(false) }
    var showDanaQR by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
	modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
		.clickable(
		    onClick = { showDonateDialog = true }
		)
		.padding(16.dp),
	    verticalAlignment = Alignment.CenterVertically
	) {
	    Icon(
                painter = painterResource(R.drawable.ic_donate),
                contentDescription = null,
		tint = MaterialTheme.colorScheme.onSurfaceVariant,
		modifier = Modifier.padding(end = 16.dp)
            )
	    Column {
		Text(
		    text = "Donate",
                    style = MaterialTheme.typography.titleMedium,
		    color = MaterialTheme.colorScheme.onSurface
		)
		Text(
		    text = "Click this donate card if you want to donate or buy me a coffee.",
		    style = MaterialTheme.typography.bodyMedium,
		    color = MaterialTheme.colorScheme.onSurfaceVariant
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
    Card(
	modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
	    Icon(
		painter = painterResource(R.drawable.ic_license),
		contentDescription = null,
		tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 16.dp)
	    )
	    Text(
		text = "License",
		style = MaterialTheme.typography.titleMedium,
		color = MaterialTheme.colorScheme.onSurface
	    )
	}
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        Column(
	    modifier = Modifier.padding(16.dp)
	) {
	    Text(
                text = """
                    Copyright (C) 2025 Rve

                    This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

                    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

                    You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
                """.trimIndent(),
		style = MaterialTheme.typography.bodyMedium,
		color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
