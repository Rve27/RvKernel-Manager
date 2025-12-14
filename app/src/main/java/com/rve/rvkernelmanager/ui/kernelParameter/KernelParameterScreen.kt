/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.kernelParameter

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.composables.core.rememberDialogState
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.components.DialogTextButton
import com.rve.rvkernelmanager.ui.components.DialogUnstyled
import com.rve.rvkernelmanager.ui.components.SimpleTopAppBar
import com.rve.rvkernelmanager.ui.navigation.BottomNavigationBar
import com.rve.rvkernelmanager.utils.KernelUtils

@Composable
fun KernelParameterScreen(viewModel: KernelParameterViewModel = viewModel(), navController: NavController) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val kernelParameters by viewModel.kernelParameters.collectAsState()
    val uclamp by viewModel.uclamp.collectAsState()
    val memory by viewModel.memory.collectAsState()
    val bore by viewModel.boreScheduler.collectAsState()

    val pullToRefreshState = remember {
        object : PullToRefreshState {
            private val anim = Animatable(0f, Float.VectorConverter)

            override val distanceFraction
                get() = anim.value

            override val isAnimating: Boolean
                get() = anim.isRunning

            override suspend fun animateToThreshold() {
                anim.animateTo(1f, spring(dampingRatio = Spring.DampingRatioHighBouncy))
            }

            override suspend fun animateToHidden() {
                anim.animateTo(0f)
            }

            override suspend fun snapTo(targetValue: Float) {
                anim.snapTo(targetValue)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.loadKernelParameter()
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            PullToRefreshBox(
                isRefreshing = viewModel.isRefreshing,
                onRefresh = { viewModel.refresh() },
                state = pullToRefreshState,
                indicator = {
                    PullToRefreshDefaults.LoadingIndicator(
                        state = pullToRefreshState,
                        isRefreshing = viewModel.isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                },
            ) {
                LazyColumn(
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        KernelProfileCard()
                    }
                    if (kernelParameters.hasSchedAutogroup || kernelParameters.hasPrintk || kernelParameters.hasTcpCongestionAlgorithm) {
                        item {
                            KernelParameterCard(viewModel)
                        }
                    }
                    if (uclamp.hasUclampMax || uclamp.hasUclampMin || uclamp.hasUclampMinRt) {
                        item {
                            UclampCard(viewModel)
                        }
                    }
                    if (memory.hasZramSize || memory.hasZramCompAlgorithm) {
                        item {
                            MemoryCard(viewModel)
                        }
                    }
                    if (bore.hasBore) {
                        item {
                            BoreSchedulerCard(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KernelProfileCard(viewModel: KernelParameterViewModel = viewModel()) {
    val context = LocalContext.current

    val kernelProfile by viewModel.kernelProfile.collectAsState()
    val kernelProfileLink = "https://github.com/Rve27/RvKernel-Manager/tree/main/kernel-profile-template"

    val options = listOf("Powersave", "Balance", "Performance")

    val icons = listOf(
        painterResource(R.drawable.ic_battery_android_frame_plus),
        painterResource(R.drawable.ic_balance),
        painterResource(R.drawable.ic_speed),
    )

    var selectedIndex by remember { mutableIntStateOf(kernelProfile.currentProfile) }

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tune),
                    contentDescription = null,
                )
                Text(
                    text = "Kernel Profiles",
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                ) {
                    options.forEachIndexed { index, label ->
                        ToggleButton(
                            enabled =
                            kernelProfile.hasProfilePowersave && kernelProfile.hasProfileBalance && kernelProfile.hasProfilePerformance,
                            checked = selectedIndex == index,
                            onCheckedChange = {
                                selectedIndex = index
                                viewModel.updateProfile(index)
                            },
                            shapes =
                            when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes(
                                    shape = RoundedCornerShape(
                                        topStart = 28.dp,
                                        bottomStart = 28.dp,
                                        topEnd = 8.dp,
                                        bottomEnd = 8.dp,
                                    ),
                                    checkedShape = RoundedCornerShape(28.dp),
                                )

                                options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes(
                                    shape = RoundedCornerShape(
                                        topStart = 8.dp,
                                        bottomStart = 8.dp,
                                        topEnd = 28.dp,
                                        bottomEnd = 28.dp,
                                    ),
                                    checkedShape = RoundedCornerShape(28.dp),
                                )

                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes(
                                    checkedShape = RoundedCornerShape(28.dp),
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .semantics { role = Role.RadioButton },
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                            ),
                            contentPadding = PaddingValues(8.dp),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = icons[index],
                                    contentDescription = null,
                                )
                                Text(label)
                            }
                        }
                    }
                }
                if (!kernelProfile.hasProfilePowersave && !kernelProfile.hasProfileBalance && !kernelProfile.hasProfilePerformance) {
                    Button(
                        modifier = Modifier.fillMaxSize(),
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, kernelProfileLink.toUri()))
                        },
                        shapes = ButtonDefaults.shapes(),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        Text(
                            text = "Get kernel profiles templates",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KernelParameterCard(viewModel: KernelParameterViewModel) {
    val kernelParameters by viewModel.kernelParameters.collectAsState()
    var dmesgRestrict = remember(kernelParameters.dmesgRestrict) { kernelParameters.dmesgRestrict == 1 }
    var printk by remember { mutableStateOf(kernelParameters.printk) }
    var schedLibName by remember { mutableStateOf(kernelParameters.schedLibName) }
    var tcpCongestionAlgorithm by remember { mutableStateOf(kernelParameters.tcpCongestionAlgorithm) }
    var schedAutogroup = remember(kernelParameters.schedAutogroup) { kernelParameters.schedAutogroup == 1 }

    // PD = Printk Dialog
    val openPD = rememberDialogState(initiallyVisible = false)
    // TCD = TCP Congestion Dialog
    val openTCD = rememberDialogState(initiallyVisible = false)
    // SLND = Sched Lib Name Dialog
    val openSLND = rememberDialogState(initiallyVisible = false)

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_linux),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    contentDescription = null,
                )
                Text(
                    text = "Kernel Parameter",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }

            AnimatedVisibility(kernelParameters.hasDmesgRestrict) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = { viewModel.setSwitchValue(KernelUtils.DMESG_RESTRICT, !dmesgRestrict) },
                        border = BorderStroke(
                            width = 2.0.dp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_comments_disabled),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    contentDescription = "Restrict dmesg output",
                                )
                                Text(
                                    text = "Restrict dmesg output",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f),
                                )
                                Switch(
                                    checked = dmesgRestrict,
                                    onCheckedChange = { dmesgRestrict = it },
                                    thumbContent = {
                                        Crossfade(
                                            targetState = dmesgRestrict,
                                            animationSpec = tween(durationMillis = 500),
                                        ) { isChecked ->
                                            if (isChecked) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_check),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                        Surface(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.primary,
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Restrict Dmesg",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary)
                                Text(
                                    text = "This toggle indicates whether unprivileged users are prevented" +
                                            " from using dmesg to view messages from the kernel's log buffer." +
                                            " When dmesg_restrict is set to inactive (0) there are no restrictions.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(kernelParameters.hasSchedAutogroup) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    onClick = { viewModel.setSwitchValue(KernelUtils.SCHED_AUTO_GROUP, !schedAutogroup) },
                    border = BorderStroke(
                        width = 2.0.dp,
                        color = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_account_tree),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = null,
                        )
                        Text(
                            text = "Sched auto group",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = schedAutogroup,
                            onCheckedChange = { schedAutogroup = it },
                            thumbContent = {
                                Crossfade(
                                    targetState = schedAutogroup,
                                    animationSpec = tween(durationMillis = 500),
                                ) { isChecked ->
                                    if (isChecked) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_check),
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
            }

            AnimatedVisibility(kernelParameters.hasPrintk) {
                Button(
                    onClick = { openPD.visible = true },
                    shapes = ButtonDefaults.shapes(
                        shape = RoundedCornerShape(28.dp),
                    ),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_speaker_notes),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null,
                        )
                        Column {
                            Text(
                                text = "printk",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                text = printk,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(kernelParameters.hasSchedLibName) {
                Button(
                    onClick = { openSLND.visible = true },
                    shapes = ButtonDefaults.shapes(
                        shape = RoundedCornerShape(28.dp),
                    ),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_speed),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null,
                        )
                        Column {
                            Text(
                                text = "Sched lib name",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                text = "Add apps packages into sched_lib_name list to report max frequency to unity task",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(kernelParameters.hasTcpCongestionAlgorithm) {
                Button(
                    onClick = { openTCD.visible = true },
                    shapes = ButtonDefaults.shapes(
                        shape = RoundedCornerShape(28.dp),
                    ),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_sync_alt),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = null,
                        )
                        Column {
                            Text(
                                text = "TCP congestion algorithm",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                text = tcpCongestionAlgorithm,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }
        }
    }

    DialogUnstyled(
        state = openPD,
        text = {
            OutlinedTextField(
                value = printk,
                onValueChange = { printk = it },
                label = { Text("printk") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.PRINTK, printk)
                        openPD.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.PRINTK, printk)
                    openPD.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openPD.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )

    DialogUnstyled(
        state = openSLND,
        text = {
            OutlinedTextField(
                value = schedLibName,
                onValueChange = { schedLibName = it },
                label = { Text("Sched lib name") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.SCHED_LIB_NAME, schedLibName)
                        openSLND.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.SCHED_LIB_NAME, schedLibName)
                    openSLND.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openSLND.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )

    DialogUnstyled(
        state = openTCD,
        title = {
            Text(
                text = "TCP congestion algorithm",
                style = MaterialTheme.typography.titleMedium,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Column {
                kernelParameters.availableTcpCongestionAlgorithm.forEach { algorithm ->
                    DialogTextButton(
                        text = algorithm,
                        onClick = {
                            viewModel.setValue(KernelUtils.TCP_CONGESTION_ALGORITHM, algorithm)
                            tcpCongestionAlgorithm = algorithm
                            openTCD.visible = false
                        },
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openTCD.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
}

@Composable
fun UclampCard(viewModel: KernelParameterViewModel) {
    val uclamp by viewModel.uclamp.collectAsState()
    var uclampMax by remember { mutableStateOf(uclamp.uclampMax) }
    var uclampMin by remember { mutableStateOf(uclamp.uclampMin) }
    var uclampMinRt by remember { mutableStateOf(uclamp.uclampMinRt) }

    // UMX = Uclamp Max
    val openUMX = rememberDialogState(initiallyVisible = false)
    // UMN = Uclamp Min
    val openUMN = rememberDialogState(initiallyVisible = false)
    // UMRT = Uclamp Min RT
    val openUMRT = rememberDialogState(initiallyVisible = false)

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tune),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    contentDescription = null,
                )
                Text(
                    text = "Uclamp",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
            if (uclamp.hasUclampMax || uclamp.hasUclampMin) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AnimatedVisibility(
                        visible = uclamp.hasUclampMax,
                        modifier = if (uclamp.hasUclampMin) Modifier.weight(1f) else Modifier,
                    ) {
                        Button(
                            onClick = { openUMX.visible = true },
                            shapes = ButtonDefaults.shapes(
                                shape = RoundedCornerShape(28.dp),
                            ),
                            contentPadding = PaddingValues(16.dp),
                        ) {
                            Column(Modifier.fillMaxSize()) {
                                Text(
                                    text = "Uclamp max",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = uclamp.uclampMax,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = uclamp.hasUclampMin,
                        modifier = if (uclamp.hasUclampMax) Modifier.weight(1f) else Modifier,
                    ) {
                        Button(
                            onClick = { openUMN.visible = true },
                            shapes = ButtonDefaults.shapes(
                                shape = RoundedCornerShape(28.dp),
                            ),
                            contentPadding = PaddingValues(16.dp),
                        ) {
                            Column(Modifier.fillMaxSize()) {
                                Text(
                                    text = "Uclamp min",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = uclamp.uclampMin,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(uclamp.hasUclampMinRt) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    shape = MaterialTheme.shapes.extraLarge,
                    onClick = { openUMRT.visible = true },
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                    ) {
                        Text(
                            text = "Uclamp min RT default",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Text(
                            text = uclamp.uclampMinRt,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }

    DialogUnstyled(
        state = openUMX,
        text = {
            OutlinedTextField(
                value = uclampMax,
                onValueChange = { uclampMax = it },
                label = { Text("Uclamp max") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.SCHED_UTIL_CLAMP_MAX, uclampMax)
                        openUMX.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.SCHED_UTIL_CLAMP_MAX, uclampMax)
                    openUMX.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openUMX.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )

    DialogUnstyled(
        state = openUMN,
        text = {
            OutlinedTextField(
                value = uclampMin,
                onValueChange = { uclampMin = it },
                label = { Text("Uclamp min") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.SCHED_UTIL_CLAMP_MIN, uclampMin)
                        openUMX.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.SCHED_UTIL_CLAMP_MIN, uclampMin)
                    openUMN.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openUMN.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )

    DialogUnstyled(
        state = openUMRT,
        text = {
            OutlinedTextField(
                value = uclampMinRt,
                onValueChange = { uclampMinRt = it },
                label = { Text("Uclamp min RT default") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.SCHED_UTIL_CLAMP_MIN_RT_DEFAULT, uclampMinRt)
                        openUMRT.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.SCHED_UTIL_CLAMP_MIN_RT_DEFAULT, uclampMinRt)
                    openUMRT.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text(text = "Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openUMRT.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
}

@Composable
fun MemoryCard(viewModel: KernelParameterViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val memory by viewModel.memory.collectAsState()
    val zramSizeOptions = listOf("1 GB", "2 GB", "3 GB", "4 GB", "5 GB", "6 GB")
    var swappiness by remember { mutableStateOf(memory.swappiness) }
    var dirtyRatio by remember { mutableStateOf(memory.dirtyRatio) }

    // ZD = ZRAM Dialog
    val openZD = rememberDialogState(initiallyVisible = false)
    // ZCD = ZRAM Compression Dialog
    val openZCD = rememberDialogState(initiallyVisible = false)
    // SD = Swappiness Dialog
    val openSD = rememberDialogState(initiallyVisible = false)
    // DR = Dirty Ratio
    val openDR = rememberDialogState(initiallyVisible = false)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_memory_alt),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    contentDescription = null,
                )
                Text(
                    text = "Memory",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }

            OutlinedCard(
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                ),
                border = BorderStroke(
                    width = 2.0.dp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_exclamation),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentDescription = null,
                    )
                    Text(
                        text = "It may take a few minutes to change the ZRAM parameters",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }

            if (memory.hasZramSize || memory.hasSwappiness) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AnimatedVisibility(
                        visible = memory.hasZramSize,
                        modifier = if (memory.hasSwappiness) Modifier.weight(1f) else Modifier,
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = { openZD.visible = true },
                        ) {
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    text = "ZRAM size",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = memory.zramSize,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = memory.hasSwappiness,
                        modifier = if (memory.hasZramSize) Modifier.weight(1f) else Modifier,
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = { openSD.visible = true },
                        ) {
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    text = "Swappiness",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "${memory.swappiness}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(memory.availableZramCompAlgorithms.isNotEmpty()) {
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    onClick = { openZCD.visible = true },
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                    ) {
                        Text(
                            text = "ZRAM compression algorithm",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Text(
                            text = memory.zramCompAlgorithm,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }

            AnimatedVisibility(expanded) {
                AnimatedVisibility(memory.hasDirtyRatio) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                        shape = MaterialTheme.shapes.extraLarge,
                        onClick = { openDR.visible = true },
                    ) {
                        Column(
                            Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                        ) {
                            Text(
                                text = "dirty ratio",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Text(
                                text = memory.dirtyRatio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            TooltipBox(
                positionProvider =
                TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above,
                ),
                tooltip = { PlainTooltip(caretShape = TooltipDefaults.caretShape()) { Text("More VM parameters") } },
                state = rememberTooltipState(),
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = "More VM parameters",
                    )
                }
            }
        }
    }

    DialogUnstyled(
        state = openZD,
        title = {
            Text(
                text = "ZRAM size",
                style = MaterialTheme.typography.titleMedium,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Column {
                zramSizeOptions.forEach { size ->
                    DialogTextButton(
                        text = size,
                        onClick = {
                            val sizeInGb = size.substringBefore(" GB").toInt()
                            viewModel.updateZramSize(sizeInGb)
                            openZD.visible = false
                        },
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openZD.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )

    DialogUnstyled(
        state = openZCD,
        title = {
            Text(
                text = "ZRAM compression algorithm",
                style = MaterialTheme.typography.titleMedium,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Column {
                memory.availableZramCompAlgorithms.forEach { algorithm ->
                    DialogTextButton(
                        text = algorithm,
                        onClick = {
                            viewModel.updateZramCompAlgorithm(algorithm)
                            openZCD.visible = false
                        },
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openZCD.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )

    DialogUnstyled(
        state = openSD,
        text = {
            OutlinedTextField(
                value = swappiness,
                onValueChange = { swappiness = it },
                label = { Text("Swappiness") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.SWAPPINESS, swappiness)
                        openSD.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.SWAPPINESS, swappiness)
                    openSD.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openSD.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )

    DialogUnstyled(
        state = openDR,
        text = {
            OutlinedTextField(
                value = dirtyRatio,
                onValueChange = { dirtyRatio = it },
                label = { Text("dirty ratio") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.DIRTY_RATIO, dirtyRatio)
                        openDR.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.DIRTY_RATIO, dirtyRatio)
                    openDR.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openDR.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
}

@Composable
fun BoreSchedulerCard(viewModel: KernelParameterViewModel) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val boreScheduler by viewModel.boreScheduler.collectAsState()
    var bore by remember { mutableStateOf(boreScheduler.bore == "1") }
    var burstSmoothnessLong by remember { mutableStateOf(boreScheduler.burstSmoothnessLong) }
    var burstSmoothnessShort by remember { mutableStateOf(boreScheduler.burstSmoothnessShort) }
    var burstForkAtavistic by remember { mutableStateOf(boreScheduler.burstForkAtavistic) }
    var burstPenaltyOffset by remember { mutableStateOf(boreScheduler.burstPenaltyOffset) }
    var burstPenaltyScale by remember { mutableStateOf(boreScheduler.burstPenaltyScale) }
    var burstCacheLifetime by remember { mutableStateOf(boreScheduler.burstCacheLifetime) }

    // BSL = Burst Smoothness Long
    val openBSL = rememberDialogState(initiallyVisible = false)
    // BSS = Burst Smoothness Short
    val openBSS = rememberDialogState(initiallyVisible = false)
    // BFA = Burst Fork Atavistic
    val openBFA = rememberDialogState(initiallyVisible = false)
    // BPO = Burst Penalty Offset
    val openBPO = rememberDialogState(initiallyVisible = false)
    // BPS = Burst Penalty Scale
    val openBPS = rememberDialogState(initiallyVisible = false)
    // BCL = Burst Cache Lifetime
    val openBCL = rememberDialogState(initiallyVisible = false)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_account_tree),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    contentDescription = null,
                )
                Text(
                    text = "BORE Scheduler",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(
                    width = 2.0.dp,
                    color = MaterialTheme.colorScheme.primary,
                ),
                onClick = { viewModel.updateBoreStatus(!bore) },
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Enabled",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = bore,
                            onCheckedChange = { isChecked ->
                                bore = isChecked
                                viewModel.updateBoreStatus(isChecked)
                            },
                            thumbContent = {
                                Crossfade(
                                    targetState = bore,
                                    animationSpec = tween(durationMillis = 500),
                                ) { isChecked ->
                                    if (isChecked) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_check),
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            text = "BORE Scheduler",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary)
                        Text(
                            text = "BORE (Burst-Oriented Response Enhancer) is an enhanced versions" +
                                    " of the EEVDF (Earliest Eligible Virtual Deadline First) Linux schedulers." +
                                    " Developed with the aim of maintaining these schedulers' high performance" +
                                    " while delivering resilient responsiveness to user" +
                                    " input under as versatile load scenario as possible.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
            AnimatedVisibility(expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AnimatedVisibility(boreScheduler.hasBurstSmoothnessLong) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = { openBSL.visible = true },
                        ) {
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    text = "Burst smoothness long",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = burstSmoothnessLong,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    AnimatedVisibility(boreScheduler.hasBurstSmoothnessShort) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = { openBSS.visible = true },
                        ) {
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    text = "Burst smoothness short",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = burstSmoothnessShort,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    AnimatedVisibility(boreScheduler.hasBurstForkAtavistic) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = { openBFA.visible = true },
                        ) {
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    text = "Burst fork atavistic",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = burstForkAtavistic,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    AnimatedVisibility(boreScheduler.hasBurstPenaltyOffset) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = { openBPO.visible = true },
                        ) {
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    text = "Burst penalty offset",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = burstPenaltyOffset,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    AnimatedVisibility(boreScheduler.hasBurstPenaltyScale) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = { openBPS.visible = true },
                        ) {
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    text = "Burst penalty scale",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = burstPenaltyScale,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                    AnimatedVisibility(boreScheduler.hasBurstCacheLifetime) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = { openBCL.visible = true },
                        ) {
                            Column(
                                Modifier
                                    .padding(16.dp)
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    text = "Burst cache lifetime",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = burstCacheLifetime,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            TooltipBox(
                positionProvider =
                TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above,
                ),
                tooltip = { PlainTooltip(caretShape = TooltipDefaults.caretShape()) { Text("More Bore parameters") } },
                state = rememberTooltipState(),
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = "More BORE parameters",
                    )
                }
            }
        }
    }

    DialogUnstyled(
        state = openBSL,
        text = {
            OutlinedTextField(
                value = burstSmoothnessLong,
                onValueChange = { burstSmoothnessLong = it },
                label = { Text("Burst smoothness long") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.BURST_SMOOTHNESS_LONG, burstSmoothnessLong)
                        openBSL.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.BURST_SMOOTHNESS_LONG, burstSmoothnessLong)
                    openBSL.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openBSL.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
    DialogUnstyled(
        state = openBSS,
        text = {
            OutlinedTextField(
                value = burstSmoothnessShort,
                onValueChange = { burstSmoothnessShort = it },
                label = { Text("Burst smoothness short") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.BURST_SMOOTHNESS_SHORT, burstSmoothnessShort)
                        openBSS.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.BURST_SMOOTHNESS_SHORT, burstSmoothnessShort)
                    openBSS.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openBSS.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
    DialogUnstyled(
        state = openBFA,
        text = {
            OutlinedTextField(
                value = burstForkAtavistic,
                onValueChange = { burstForkAtavistic = it },
                label = { Text("Burst fork atavistic") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.BURST_FORK_ATAVISTIC, burstForkAtavistic)
                        openBFA.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.BURST_FORK_ATAVISTIC, burstForkAtavistic)
                    openBFA.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openBFA.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
    DialogUnstyled(
        state = openBPO,
        text = {
            OutlinedTextField(
                value = burstPenaltyOffset,
                onValueChange = { burstPenaltyOffset = it },
                label = { Text("Burst penalty offset") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.BURST_PENALTY_OFFSET, burstPenaltyOffset)
                        openBPO.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.BURST_PENALTY_OFFSET, burstPenaltyOffset)
                    openBPO.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openBPO.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
    DialogUnstyled(
        state = openBPS,
        text = {
            OutlinedTextField(
                value = burstPenaltyScale,
                onValueChange = { burstPenaltyScale = it },
                label = { Text("Burst penalty scale") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.BURST_PENALTY_SCALE, burstPenaltyScale)
                        openBPS.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.BURST_PENALTY_SCALE, burstPenaltyScale)
                    openBPS.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openBPS.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
    DialogUnstyled(
        state = openBCL,
        text = {
            OutlinedTextField(
                value = burstCacheLifetime,
                onValueChange = { burstCacheLifetime = it },
                label = { Text("Burst cache lifetime") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.setValue(KernelUtils.BURST_CACHE_LIFETIME, burstCacheLifetime)
                        openBCL.visible = false
                    },
                ),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.setValue(KernelUtils.BURST_CACHE_LIFETIME, burstCacheLifetime)
                    openBCL.visible = false
                },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { openBCL.visible = false },
                shapes = ButtonDefaults.shapes(),
            ) {
                Text("Cancel")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun KernelParameterScreenPreview() {
    val navController = rememberNavController()
    KernelParameterScreen(navController = navController)
}
