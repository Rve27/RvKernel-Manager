package com.rve.rvkernelmanager.utils

import com.topjohnwu.superuser.Shell
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.rve.rvkernelmanager.R

object RootUtils {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NoRootDialog(onConfirm: () -> Unit) {
        val openDialog = remember { mutableStateOf(true) }
        
        if (openDialog.value) {
            BasicAlertDialog(
                onDismissRequest = {}
            ) {
                Surface(
                    modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = AlertDialogDefaults.TonalElevation,
                ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "RvKernel Manager requires root access!",
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        TextButton(
                            onClick = {
                                openDialog.value = false
                                onConfirm()
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = "Exit")
                        }
                    }
                }
            }
        }
    }
}
