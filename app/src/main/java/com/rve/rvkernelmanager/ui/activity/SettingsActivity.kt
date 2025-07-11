package com.rve.rvkernelmanager.ui.activity

import android.os.Bundle

import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import com.rve.rvkernelmanager.ui.screen.SettingsScreen
import com.rve.rvkernelmanager.ui.theme.RvKernelManagerTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RvKernelManagerTheme {
		SettingsScreen()
	    }
	}
    }
}
