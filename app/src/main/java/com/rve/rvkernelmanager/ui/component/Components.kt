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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.animateContentSize

@Composable
fun SwitchItem(
    title: String,
    summary: String? = null,
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
	    .padding(16.dp)
    ) {
	Column(
	    modifier = Modifier.weight(1f)
	) {
	    Text(
		text = title,
		style = MaterialTheme.typography.titleMedium
	    )
	    if (summary != null) {
		Text(
		    text = summary,
		    style = MaterialTheme.typography.bodyMedium,
		    modifier = Modifier.alpha(0.7f)
	        )
	    }
	}
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

@Composable
fun CustomItem(
    title: String? = null,
    body: String? = null,
    onClick: (() -> Unit)? = null,
    animateContent: Boolean = false,
    titleLarge: Boolean = false,
    useAlpha: Boolean = true,
    icon: Any? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            when (icon) {
                is ImageVector -> Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 20.dp)
                )
                is Painter -> Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        }
        Column {
            if (title != null) {
                Text(
                    text = title,
                    style = if (titleLarge) {
                        MaterialTheme.typography.titleLarge
                    } else {
                        MaterialTheme.typography.titleMedium
                    },
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            if (body != null) {
		var modifier = Modifier
		    .clickable(enabled = onClick != null) { onClick?.invoke() }

		if (animateContent) {
		    modifier = modifier.animateContentSize()
		}

		modifier = modifier.padding(top = 4.dp)

		if (useAlpha) {
		    modifier = modifier.alpha(0.7f)
		}

                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = modifier
                )
            }
        }
    }
}
