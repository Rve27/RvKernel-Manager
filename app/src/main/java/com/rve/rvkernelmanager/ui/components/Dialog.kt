/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rve.rvkernelmanager.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.DialogState
import com.composables.core.Scrim

@Composable
fun DialogUnstyled(
    state: DialogState,
    title: String? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
) {
    Dialog(state = state) {
        Scrim(scrimColor = Color.Black.copy(0.6f), enter = fadeIn(), exit = fadeOut())
        DialogPanel(
            backgroundColor = AlertDialogDefaults.containerColor,
            contentColor = AlertDialogDefaults.textContentColor,
            shape = AlertDialogDefaults.shape,
            modifier = Modifier
                .displayCutoutPadding()
                .systemBarsPadding()
                .widthIn(min = 280.dp, max = 560.dp)
                .padding(24.dp),
            enter = fadeIn(spring(stiffness = Spring.StiffnessHigh)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            ),
            exit = scaleOut(targetScale = 0.6f) + fadeOut(tween(durationMillis = 150)),
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                ) {
                    title?.let {
                        Box(
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .align(Alignment.Start),
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = AlertDialogDefaults.titleContentColor,
                            )
                        }
                    }
                    text?.let {
                        Box(
                            modifier = Modifier
                                .weight(weight = 1f, fill = false)
                                .padding(bottom = 24.dp)
                                .align(Alignment.Start),
                        ) {
                            text()
                        }
                    }
                    Row(Modifier.align(Alignment.End)) {
                        dismissButton?.let {
                            dismissButton()
                        }
                        confirmButton?.let {
                            confirmButton()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogTextButton(icon: Any? = null, text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shapes = ButtonDefaults.shapes(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                when (icon) {
                    is ImageVector -> Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    is Painter -> Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }
            Text(text)
        }
    }
}
