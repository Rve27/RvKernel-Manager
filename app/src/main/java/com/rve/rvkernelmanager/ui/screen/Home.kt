package com.rve.rvkernelmanager.ui.screen

import android.content.*
import android.net.Uri

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.navigation.*
import com.rve.rvkernelmanager.ui.viewmodel.HomeViewModel
import com.rve.rvkernelmanager.ui.component.CustomListItem

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
	topBar = { PinnedTopAppBar(scrollBehavior = scrollBehavior) },
	bottomBar = { BottomNavigationBar(navController) },
	modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp),
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
    val clipboardManager = LocalClipboardManager.current

    val deviceInfo by viewModel.deviceInfo.collectAsState()

    var isExtendCpuInfo by rememberSaveable { mutableStateOf(false) }
    var isFullKernelVersion by rememberSaveable { mutableStateOf(false) }

    Card {
        CustomListItem(
            title = "Device Information",
	    titleLarge = true
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        CustomListItem(
            title = "Device",
            summary = "${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})",
	    icon = painterResource(R.drawable.ic_smartphone),
	    onLongClick = { clipboardManager.setText(AnnotatedString("${deviceInfo.manufacturer} ${deviceInfo.deviceName} (${deviceInfo.deviceCodename})")) }
        )

        CustomListItem(
            title = "RAM",
            summary = "${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)",
	    icon = painterResource(R.drawable.ic_ram),
	    onLongClick = { clipboardManager.setText(AnnotatedString("${deviceInfo.ramInfo} + ${deviceInfo.zram} (ZRAM)")) }
        )

        CustomListItem(
            title = "CPU",
            summary = if (isExtendCpuInfo) deviceInfo.extendCpu else deviceInfo.cpu,
            icon = painterResource(R.drawable.ic_cpu),
            onClick = { isExtendCpuInfo = !isExtendCpuInfo },
	    onLongClick = { clipboardManager.setText(AnnotatedString(if (isExtendCpuInfo) deviceInfo.extendCpu else deviceInfo.cpu)) },
	    animateContentSize = true
        )

        CustomListItem(
            title = "GPU",
            summary = deviceInfo.gpuModel,
            icon = painterResource(R.drawable.ic_video_card),
	    onLongClick = { clipboardManager.setText(AnnotatedString(deviceInfo.gpuModel)) }
        )

        CustomListItem(
            title = "Android version",
            summary = "${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})",
            icon = painterResource(R.drawable.ic_android),
	    onLongClick = { clipboardManager.setText(AnnotatedString("${deviceInfo.androidVersion} (${deviceInfo.sdkVersion})")) }
        )

        CustomListItem(
            title = "Kernel version",
            summary = if (isFullKernelVersion) deviceInfo.fullKernelVersion else deviceInfo.kernelVersion,
            icon = painterResource(R.drawable.ic_linux),
            onClick = { isFullKernelVersion = !isFullKernelVersion },
	    onLongClick = { clipboardManager.setText(AnnotatedString(if (isFullKernelVersion) deviceInfo.fullKernelVersion else deviceInfo.kernelVersion)) },
	    animateContentSize = true
        )
    }
}

@Composable
fun DonateCard() {
    val context = LocalContext.current

    Card(
	colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com/rve27"))) }
    ) {
	CustomListItem(
	    icon = painterResource(R.drawable.ic_kofi),
	    title = "Buy Me a Coffee"
	)

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        CustomListItem(
             summary = "I wouldn’t be here without you. Every bit of support helps me keep creating, and I appreciate it more than words can say!",
        )
    }
}

@Composable
fun CopyrightCard() {
    Card {
        CustomListItem(
            title = "License",
	    titleLarge = true,
            icon = painterResource(R.drawable.ic_license)
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        CustomListItem(
            summary = """
                Copyright (C) 2025 Rve

                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

                This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

                You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
            """.trimIndent()
        )
    }
}
