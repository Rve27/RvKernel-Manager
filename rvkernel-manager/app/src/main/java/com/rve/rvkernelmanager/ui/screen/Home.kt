package com.rve.rvkernelmanager.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.ui.ViewModel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadDeviceInfo(context)
    }

    Scaffold(
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DeviceInfoCard(viewModel)
            DonateCard()
            CopyrightCard()
            Spacer(Modifier)
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
    val rvosVersion by viewModel.rvosVersion.collectAsState()
    val somethingVersion by viewModel.somethingVersion.collectAsState()
    val kernelVersion by viewModel.kernelVersion.collectAsState()
    val isCPUInfo by viewModel.isExtendCPUInfo.collectAsState()
    val isFullKernelVersion by viewModel.isFullKernelVersion.collectAsState()

    ElevatedCard(
        shape = CardDefaults.shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.device_codename),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = deviceCodename,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.ram_info),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = ramInfo,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.cpu),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = cpu,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable { viewModel.showCPUInfo() }
                        .animateContentSize()
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.gpu),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = gpuModel,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.android_version),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = androidVersion,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))

                rvosVersion?.let { version ->
                    Text(
                        text = stringResource(R.string.rvos_version),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = version,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                }

                somethingVersion?.let { version ->
                    Text(
                        text = stringResource(R.string.something_version),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = version,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    text = stringResource(R.string.kernel_version),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = kernelVersion,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable { viewModel.showFullKernelVersion() }
                        .animateContentSize()
                )
            }
        }
    }
}

@Composable
fun DonateCard() {
    var showDonateDialog by remember { mutableStateOf(false) }
    var showDanaQR by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val paypalUrl = stringResource(id = R.string.paypal_url)

    ElevatedCard(
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
                    text = stringResource(R.string.donate_title),
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.donate_summary),
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
                    text = stringResource(R.string.donate_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
		    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.paypal),
                        style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paypalUrl))
                            context.startActivity(intent)
                        }
                    )
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.dana),
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
                    text = stringResource(R.string.dana),
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
    ElevatedCard(
	shape = CardDefaults.shape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Copyright",
                    style = MaterialTheme.typography.titleSmall
                )
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
