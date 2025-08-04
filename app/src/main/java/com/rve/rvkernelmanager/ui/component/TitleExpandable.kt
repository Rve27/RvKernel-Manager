/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rve.rvkernelmanager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun TitleExpandable(
    leadingIcon: Any? = null,
    text: String,
    titleSmall: Boolean = false,
    titleLarge: Boolean = false,
    trailingIcon: Any? = null,
    onClick: () -> Unit
) {
    Row(
	modifier = Modifier
	    .clickable(onClick = onClick)
	    .padding(16.dp)
	    .fillMaxWidth(),
	verticalAlignment = Alignment.CenterVertically
    ) {
	if (leadingIcon != null) {
	    when (leadingIcon) {
		is ImageVector -> Icon(
		    imageVector = leadingIcon,
		    contentDescription = null,
		    tint = MaterialTheme.colorScheme.onSurfaceVariant,
		    modifier = Modifier.padding(end = 16.dp)
		)
		is Painter -> Icon(
		    painter = leadingIcon,
		    contentDescription = null,
		    tint = MaterialTheme.colorScheme.onSurfaceVariant,
		    modifier = Modifier.padding(end = 16.dp)
		)
	    }
	}
	Text(
	    text = text,
	    style = when {
		titleSmall -> MaterialTheme.typography.titleSmall
		titleLarge -> MaterialTheme.typography.titleLarge
		else -> MaterialTheme.typography.titleMedium
	    },
	    color = MaterialTheme.colorScheme.onSurface,
	    modifier = Modifier.weight(1f)
	)
	if (trailingIcon != null) {
	    when (trailingIcon) {
		is ImageVector -> Icon(
		    imageVector = trailingIcon,
		    contentDescription = null,
		    tint = MaterialTheme.colorScheme.onSurfaceVariant
		)
		is Painter -> Icon(
		    painter = trailingIcon,
		    contentDescription = null,
		    tint = MaterialTheme.colorScheme.onSurfaceVariant
		)
	    }
	}
    }
}
