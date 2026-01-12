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

package com.rve.rvkernelmanager.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Section(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        content()
    }
}

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
fun ListItemCard(
    icon: Any?,
    title: String,
    body: String,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,

) {
    Card(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .combinedClickable(
                onClick = onClick ?: {},
                onLongClick = onLongClick,
            )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                when (icon) {
                    is ImageVector -> Icon(
                        imageVector = icon,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = null
                    )

                    is Painter -> Icon(
                        painter = icon,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = null
                    )
                }
            }
            Column(
                modifier = Modifier.animateContentSize(
                    animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                ),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
