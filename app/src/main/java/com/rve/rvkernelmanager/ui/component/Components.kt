package com.rve.rvkernelmanager.ui.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

@Composable
fun SwitchItem(
    title: String,
    summary: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    ListItem(
        modifier = Modifier
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                role = Role.Switch,
                indication = LocalIndication.current,
                onValueChange = onCheckedChange
            ),
	colors = ListItemDefaults.colors(
	    containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        headlineContent = {
            Row {
                Text(
                    text = title
                )
            }
        },
        trailingContent = {
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
        },
        supportingContent = {
            if (summary != null) {
                Text(
                    text = summary
                )
            }
        }
    )
}
