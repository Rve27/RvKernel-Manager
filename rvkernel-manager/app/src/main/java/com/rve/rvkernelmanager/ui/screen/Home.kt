package com.rve.rvkernelmanager.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.utils.*
import com.rve.rvkernelmanager.utils.Utils
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
	    DeviceInfoCard()
	    DonateCard()
	    CopyrightCard()
	    Spacer(Modifier)
        }
    }
}

@Composable
fun DeviceInfoCard() {
    val context = LocalContext.current
    var deviceCodename by remember { mutableStateOf("") }
    var ramInfo by remember { mutableStateOf("") }
    var getCPUOnly by remember { mutableStateOf("") }
    var androidVersion by remember { mutableStateOf("") }
    var rvosVersion by remember { mutableStateOf<String?>(null) }
    var somethingVersion by remember { mutableStateOf<String?>(null) }
    var defaultKernelVersion by remember { mutableStateOf("") }
    var getCPU by remember { mutableStateOf("") }
    var isCPUInfo by remember { mutableStateOf(false) }
    var gpuModel by remember { mutableStateOf("") }
    var kernelVersion by remember { mutableStateOf("") }
    var isFullKernelVersion by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        deviceCodename = Utils.getDeviceCodename()
        ramInfo = Utils.getTotalRam(context)
        getCPUOnly = Utils.getCPU()
        androidVersion = Utils.getAndroidVersion()
        rvosVersion = Utils.getRvOSVersion()
        somethingVersion = Utils.getSomethingOSVersion()
        defaultKernelVersion = Utils.getKernelVersion()
        gpuModel = Utils.getGPUModel()
    }

    LaunchedEffect(isCPUInfo) {
        getCPU = if (isCPUInfo) {
            Utils.getCPUInfo()
        } else {
            Utils.getCPU()
        }
    }

    LaunchedEffect(isFullKernelVersion) {
        kernelVersion = if (isFullKernelVersion) {
            setPermissions(644, Utils.FULL_KERNEL_VERSION_PATH)
            readFile(Utils.FULL_KERNEL_VERSION_PATH)
        } else {
            Utils.getKernelVersion()
        }
    }

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
                    text = getCPU,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable { isCPUInfo = !isCPUInfo }
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
                        .clickable { isFullKernelVersion = !isFullKernelVersion }
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
