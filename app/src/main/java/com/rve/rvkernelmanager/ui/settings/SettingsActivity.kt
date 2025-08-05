/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rve.rvkernelmanager.ui.theme.RvKernelManagerTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val lifecycleOwner = LocalLifecycleOwner.current

            RvKernelManagerTheme {
                SettingsScreen(lifecycleOwner = lifecycleOwner)
            }
        }
    }
}
