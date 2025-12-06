/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rve.rvkernelmanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.core.rememberDialogState
import com.rve.rvkernelmanager.ui.components.DialogUnstyled
import com.rve.rvkernelmanager.ui.navigation.RvKernelManagerNavHost
import com.rve.rvkernelmanager.ui.theme.RvKernelManagerTheme
import com.topjohnwu.superuser.Shell
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        actionBar?.hide()

        Shell.enableVerboseLogging = true
        if (Shell.getCachedShell() == null) {
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_MOUNT_MASTER) 
                    .setTimeout(20),
            )
        }

        setContent {
            RvKernelManagerTheme {
                RvKernelManagerApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RvKernelManagerApp() {
    var isRoot by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(true) }
    
    val showRootDialog = rememberDialogState(initiallyVisible = true)

    LaunchedEffect(Unit) {
        Shell.getShell { shell ->
            isRoot = shell.isRoot
            isChecking = false
            if (shell.isRoot) {
                showRootDialog.visible = false
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding() 
            .imePadding(), 
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        if (isRoot) {
            RvKernelManagerNavHost()
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (isChecking) {
                    ContainedLoadingIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Checking root access...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                } else {
                    Text(
                        text = "Root access denied.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            if (!isChecking && !isRoot) {
                DialogUnstyled(
                    state = showRootDialog,
                    title = {
                        Text(
                            text = "Root access required",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    text = {
                        Text(
                            text = "RvKernel Manager requires root access to function properly.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { exitProcess(0) },
                        ) {
                            Text(text = "Exit App")
                        }
                    },
                )
            }
        }
    }
}
