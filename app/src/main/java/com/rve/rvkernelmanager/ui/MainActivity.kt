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
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rve.rvkernelmanager.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.rve.rvkernelmanager.BuildConfig
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.navigation.RvKernelManagerNavHost
import com.rve.rvkernelmanager.ui.theme.RvKernelManagerTheme
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var isRoot by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch(Dispatchers.IO) {
            Shell.getShell { shell ->
                isRoot = shell.isRoot
            }
        }

        if (BuildConfig.DEBUG) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        setContent {
            RvKernelManagerTheme {
                RvKernelManagerApp()
            }
        }
    }

    companion object {
        init {
            @Suppress("DEPRECATION")
            if (Shell.getCachedShell() == null) {
                Shell.setDefaultBuilder(
                    Shell.Builder.create()
                        .setFlags(Shell.FLAG_MOUNT_MASTER or Shell.FLAG_REDIRECT_STDERR)
                        .setTimeout(20),
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun RvKernelManagerApp() {
        var showRootDialog by remember { mutableStateOf(true) }
        var isLoading by remember { mutableStateOf(true) }
        var visible by remember { mutableStateOf(false) }
        val progressAnim = remember { Animatable(0f) }

        if (isRoot) {
            LaunchedEffect(Unit) {
                visible = true
                progressAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = FastOutSlowInEasing,
                    ),
                )
                visible = false
                isLoading = false
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Crossfade(
                    targetState = isLoading,
                    animationSpec = tween(durationMillis = 1000),
                ) { loading ->
                    if (loading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 64.dp),
                            ) {
                                val percentage = (progressAnim.value * 100).toInt()
                                val bias = (progressAnim.value * 2) - 1

                                AnimatedVisibility(
                                    visible = visible,
                                    enter = fadeIn(
                                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                                    ) + slideInVertically(
                                        initialOffsetY = { it },
                                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                                    ),
                                    exit = fadeOut(
                                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                                    ) + slideOutVertically(
                                        targetOffsetY = { it },
                                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
                                    ),
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text(
                                            text = "$percentage%",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.align(
                                                BiasAlignment(horizontalBias = bias, verticalBias = 0f),
                                            ),
                                        )
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                AnimatedVisibility(
                                    visible = visible,
                                    enter = fadeIn(
                                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                                    ) + expandHorizontally(
                                        animationSpec = tween(
                                            durationMillis = 750,
                                            easing = FastOutSlowInEasing,
                                        ),
                                    ),
                                    exit = fadeOut(
                                        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec(),
                                    ) + shrinkHorizontally(
                                        animationSpec = tween(
                                            durationMillis = 750,
                                            easing = FastOutSlowInEasing,
                                        ),
                                    ),
                                ) {
                                    LinearWavyProgressIndicator(
                                        progress = { progressAnim.value },
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
                        }
                    } else {
                        RvKernelManagerNavHost()
                    }
                }
            }
        } else {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    Button(
                        shapes = ButtonDefaults.shapes(),
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                Shell.getCachedShell()?.close()

                                val shell = Shell.getShell()
                                val hasRoot = shell.isRoot

                                if (hasRoot) {
                                    launch {
                                        snackbarHostState.showSnackbar("Root access granted")
                                    }
                                    delay(1000)
                                    isRoot = true
                                } else {
                                    snackbarHostState.showSnackbar("Root access denied")
                                }
                            }
                        },
                    ) {
                        Text(text = stringResource(R.string.text_continue))
                    }
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ContainedLoadingIndicator()
                        Text(
                            text = stringResource(R.string.waiting_root),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                if (showRootDialog) {
                    AlertDialog(
                        onDismissRequest = { showRootDialog = false },
                        title = {
                            Text(
                                text = stringResource(R.string.root_required_title),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                        text = { Text(text = stringResource(R.string.root_required_desc)) },
                        confirmButton = {
                            TextButton(onClick = { showRootDialog = false }) {
                                Text(text = stringResource(R.string.close))
                            }
                        },
                    )
                }
            }
        }
    }
}
