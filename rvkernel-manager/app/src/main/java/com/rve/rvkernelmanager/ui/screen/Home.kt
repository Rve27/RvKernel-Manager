package com.rve.rvkernelmanager.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.rve.rvkernelmanager.utils.getDeviceCodename
import com.rve.rvkernelmanager.utils.getTotalRam
import com.rve.rvkernelmanager.utils.getSOC
import com.rve.rvkernelmanager.utils.getAndroidVersion
import com.rve.rvkernelmanager.utils.getRvOSVersion
import com.rve.rvkernelmanager.utils.getKernelVersion
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
            KernelSupportedCard()
	    DeviceInfoCard()
	    CopyrightCard()
	    Spacer(Modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val context = LocalContext.current
    val githubUrl = stringResource(id = R.string.repo_url)
    val telegramUrl = stringResource(id = R.string.telegram_url)

    TopAppBar(
        title = {
	    Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onPrimaryContainer
	    )
        },
        actions = {
            IconButton(
		onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                    context.startActivity(intent)
	        },
		modifier = Modifier
		    .size(35.dp)
		    .padding(end = 12.dp)
	    ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_github),
                    contentDescription = "Github",
		    tint = MaterialTheme.colorScheme.onPrimaryContainer,
		    modifier = Modifier.size(35.dp)
                )
            }

	    IconButton(
		onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                    context.startActivity(intent)
                },
		modifier = Modifier
                    .size(35.dp)
                    .padding(end = 12.dp)
	    ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_telegram),
                    contentDescription = "Telegram Group",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(35.dp)
                )
            }
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun KernelSupportedCard() {
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
                    text = stringResource(R.string.kernel_supported_title),
                    style = MaterialTheme.typography.titleSmall,
		    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.kernel_supported_summary),
                    style = MaterialTheme.typography.bodyMedium,
		    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
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
    val kernelVersion = remember { getKernelVersion() }

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
                    color = MaterialTheme.colorScheme.onPrimaryContainer
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
