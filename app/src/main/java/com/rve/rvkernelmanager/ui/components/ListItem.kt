/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rve.rvkernelmanager.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun CustomListItem(
    icon: Any? = null,
    iconColor: Color? = null,
    title: String? = null,
    titleSmall: Boolean = false,
    titleLarge: Boolean = false,
    titleColor: Color? = null,
    summary: String? = null,
    summaryColor: Color? = null,
    bodySmall: Boolean = false,
    bodyLarge: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    animateContentSize: Boolean = false,
) {
    Row(
        modifier = Modifier
            .then(
                if (onClick != null || onLongClick != null) {
                    Modifier.combinedClickable(
                        onClick = onClick ?: {},
                        onLongClick = onLongClick,
                    )
                } else {
                    Modifier
                },
            )
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            when (icon) {
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 16.dp),
                )
                is Painter -> Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 16.dp),
                )
            }
        }
        Column {
            if (title != null) {
                Text(
                    text = title,
                    style = when {
                        titleSmall -> MaterialTheme.typography.titleSmall
                        titleLarge -> MaterialTheme.typography.titleLarge
                        else -> MaterialTheme.typography.titleMedium
                    },
                    color = titleColor ?: MaterialTheme.colorScheme.onSurface,
                )
            }
            if (summary != null) {
                Text(
                    text = summary,
                    style = when {
                        bodySmall -> MaterialTheme.typography.bodySmall
                        bodyLarge -> MaterialTheme.typography.bodyLarge
                        else -> MaterialTheme.typography.bodyMedium
                    },
                    color = summaryColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = if (animateContentSize) Modifier.animateContentSize() else Modifier,
                )
            }
        }
    }
}

@Composable
fun ButtonListItem(
    isFreq: Boolean = false,
    title: String,
    titleSmall: Boolean = false,
    titleLarge: Boolean = false,
    summary: String? = null,
    bodySmall: Boolean = false,
    bodyLarge: Boolean = false,
    value: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                role = Role.Button,
                indication = LocalIndication.current,
                onClick = onClick,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = when {
                    titleSmall -> MaterialTheme.typography.titleSmall
                    titleLarge -> MaterialTheme.typography.titleLarge
                    else -> MaterialTheme.typography.titleMedium
                },
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (summary != null) {
                Text(
                    text = summary,
                    style = when {
                        bodySmall -> MaterialTheme.typography.bodySmall
                        bodyLarge -> MaterialTheme.typography.bodyLarge
                        else -> MaterialTheme.typography.bodyMedium
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Button(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                shapes = ButtonDefaults.shapes(),
            ) {
                Text(
                    text = when {
                        isFreq -> if (value.isEmpty()) "N/A" else "$value MHz"
                        else -> if (value.trim().isEmpty()) "N/A" else value
                    },
                )
            }
        }
    }
}
