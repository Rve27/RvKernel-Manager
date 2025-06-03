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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.ui.ViewModel.HomeViewModel

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

    Card(shape = CardDefaults.shape) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            InfoRow(label = "Device codename", value = deviceCodename)
            InfoRow(label = "RAM", value = ramInfo)
            InfoRow(
                label = "CPU",
                value = cpu,
                onClick = { viewModel.showCPUInfo() },
                animateContent = true
            )
            InfoRow(label = "GPU", value = gpuModel)
            InfoRow(label = "Android version", value = androidVersion)
            InfoRow(
                label = "Kernel version",
                value = kernelVersion,
                onClick = { viewModel.showFullKernelVersion() },
                animateContent = true,
		spacer = false
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null,
    animateContent: Boolean = false,
    spacer: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable(enabled = onClick != null) { onClick?.invoke() }
                .then(if (animateContent) Modifier.animateContentSize() else Modifier)
        )
        if (spacer) {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun DonateCard() {
    var showDonateDialog by remember { mutableStateOf(false) }
    var showDanaQR by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        shape = CardDefaults.shape,
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
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Click this if you want to donate or buy me a coffee.",
                    style = MaterialTheme.typography.bodyMedium
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
    Card(shape = CardDefaults.shape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Copyright", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "© 2024-2025 Rve. Licensed under the GNU General Public License v3.0.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Developed by Rve.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
