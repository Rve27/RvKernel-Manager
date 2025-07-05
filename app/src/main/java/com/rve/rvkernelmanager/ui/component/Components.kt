package com.rve.rvkernelmanager.ui.component

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.animateContentSize

@Composable
fun SwitchListItem(
    title: String,
    titleSmall: Boolean = false,
    titleLarge: Boolean = false,
    summary: String? = null,
    bodySmall: Boolean = false,
    bodyLarge: Boolean = false,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                role = Role.Switch,
                indication = LocalIndication.current,
                onValueChange = onCheckedChange
            )
	    .padding(16.dp),
	verticalAlignment = Alignment.CenterVertically
    ) {
	Column(
	    modifier = Modifier.weight(1f)
	) {
	    Text(
		text = title,
		style = when {
		    titleSmall -> MaterialTheme.typography.titleSmall
		    titleLarge -> MaterialTheme.typography.titleLarge
		    else -> MaterialTheme.typography.titleMedium
		},
		color = MaterialTheme.colorScheme.onSurface
	    )
	    if (summary != null) {
		Text(
		    text = summary,
		    style = when {
			bodySmall -> MaterialTheme.typography.bodySmall
			bodyLarge -> MaterialTheme.typography.bodyLarge
		        else -> MaterialTheme.typography.bodyMedium
		    },
		    color = MaterialTheme.colorScheme.onSurfaceVariant
	        )
	    }
	}
	Column(
	    modifier = Modifier.padding(start = 16.dp)
	) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                interactionSource = interactionSource,
	        thumbContent = {
	            if (checked) {
		        Icon(
	                    imageVector = Icons.Filled.Check,
	                    contentDescription = null,
	                    modifier = Modifier.size(SwitchDefaults.IconSize),
	                )
		    }
	        }
            )
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
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                role = Role.Button,
                indication = LocalIndication.current,
                onClick = onClick
            )
	    .padding(16.dp),
	verticalAlignment = Alignment.CenterVertically
    ) {
	Column(
	    modifier = Modifier.weight(1f)
	) {
	    Text(
		text = title,
		style = when {
		    titleSmall -> MaterialTheme.typography.titleSmall
		    titleLarge -> MaterialTheme.typography.titleLarge
		    else -> MaterialTheme.typography.titleMedium
		},
		color = MaterialTheme.colorScheme.onSurface
	    )
	    if (summary != null) {
		Text(
		    text = summary,
		    style = when {
			bodySmall -> MaterialTheme.typography.bodySmall
			bodyLarge -> MaterialTheme.typography.bodyLarge
		        else -> MaterialTheme.typography.bodyMedium
		    },
		    color = MaterialTheme.colorScheme.onSurfaceVariant
	        )
	    }
	}
	Column(
	    modifier = Modifier.padding(start = 16.dp)
	) {
            Button(
		onClick = onClick,
                interactionSource = interactionSource,
	    ) {
		Text(
		    text = when {
			isFreq -> if (value.isEmpty()) "N/A" else "$value MHz"
			else -> if (value.trim().isEmpty()) "N/A" else value
		    }
		)
	    }
	}
    }
}

@Composable
fun CustomListItem(
    icon: Any? = null,
    title: String? = null,
    titleSmall: Boolean = false,
    titleLarge: Boolean = false,
    summary: String? = null,
    bodySmall: Boolean = false,
    bodyLarge: Boolean = false,
    onClick: (() -> Unit)? = null,
    animateContentSize: Boolean = false
) {
    Row(
	modifier = Modifier
	    .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
	    .padding(16.dp)
	    .fillMaxWidth(),
	verticalAlignment = Alignment.CenterVertically
    ) {
	if (icon != null) {
            when (icon) {
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = null,
		    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 16.dp)
                )
                is Painter -> Icon(
                    painter = icon,
                    contentDescription = null,
		    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 16.dp)
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
		    color = MaterialTheme.colorScheme.onSurface
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
		    color = MaterialTheme.colorScheme.onSurfaceVariant,
		    modifier = if (animateContentSize) Modifier.animateContentSize() else Modifier
		)
	    }
	}
    }
}

@Composable
fun MonitorListItem(
    title: String,
    summary: String,
) {
    Row(
	verticalAlignment = Alignment.CenterVertically
    ) {
	Text(
	    text = title,
	    style = MaterialTheme.typography.bodyMedium,
	    color = MaterialTheme.colorScheme.onSurface,
	    modifier = Modifier.weight(1f)
	)
	Text(
	    text = summary,
	    style = MaterialTheme.typography.bodyMedium,
	    color = MaterialTheme.colorScheme.onSurfaceVariant
	)
    }
}
