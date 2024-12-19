package com.rve.rvkernelmanager.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.rve.rvkernelmanager.ui.TopBar
import com.rve.rvkernelmanager.utils.getDeviceCodename
import com.rve.rvkernelmanager.utils.getTotalRam
import com.rve.rvkernelmanager.utils.getSOC
import com.rve.rvkernelmanager.utils.getAndroidVersion
import com.rve.rvkernelmanager.utils.getRvOSVersion
import com.rve.rvkernelmanager.utils.getKernelVersion
import com.rve.rvkernelmanager.utils.setPermissions
import com.rve.rvkernelmanager.utils.readFile
import com.rve.rvkernelmanager.utils.FULL_KERNEL_VERSION_PATH
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
	    CopyrightCard()
	    Spacer(Modifier)
        }
    }
}

@Composable
fun DeviceInfoCard() {
    val context = LocalContext.current
    val deviceCodename = remember { getDeviceCodename() }
    val ramInfo = remember { getTotalRam(context) }
    val getSoc = remember { getSOC() }
    val androidVersion = remember { getAndroidVersion() }
    val rvosVersion = remember { getRvOSVersion() }
    val defaultKernelVersion = remember { getKernelVersion() }

    var kernelVersion by remember { mutableStateOf(defaultKernelVersion) }
    var isFullKernelVersion by remember { mutableStateOf(false) }

    ElevatedCard(
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors()
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
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = deviceCodename,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.ram_info),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = ramInfo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.soc),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = getSoc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.android_version),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = androidVersion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(16.dp))

                rvosVersion?.let {
                    Text(
                        text = stringResource(R.string.rvos_version),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = rvosVersion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    text = stringResource(R.string.kernel_version),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = kernelVersion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .clickable {
                            if (isFullKernelVersion) {
                                kernelVersion = defaultKernelVersion
                            } else {
				setPermissions(644, FULL_KERNEL_VERSION_PATH)
                                kernelVersion = readFile(FULL_KERNEL_VERSION_PATH)
                            }
                            isFullKernelVersion = !isFullKernelVersion
                        }
                        .animateContentSize()
                )
            }
        }
    }
}

@Composable
fun CopyrightCard() {
    ElevatedCard(
	shape = CardDefaults.shape,
	colors = CardDefaults.cardColors()
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
                    style = MaterialTheme.typography.titleSmall,
		    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "© 2024 Rve. Licensed under the GNU General Public License v3.0.",
                    style = MaterialTheme.typography.bodyMedium,
		    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Developed by Rve.",
                    style = MaterialTheme.typography.bodyMedium,
		    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
