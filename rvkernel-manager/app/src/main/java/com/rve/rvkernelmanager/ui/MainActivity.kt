package com.rve.rvkernelmanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rve.rvkernelmanager.ui.theme.RvKernelManagerTheme
import com.rve.rvkernelmanager.utils.RootUtils.isDeviceRooted
import com.rve.rvkernelmanager.utils.NoRootDialog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (!isDeviceRooted()) {
            setContent {
                RvKernelManagerTheme {
                    NoRootDialog { finish() }
                }
            }
        } else {
            setContent {
                RvKernelManagerTheme {
                    RvKernelManager()
                }
            }
        }
    }
}
