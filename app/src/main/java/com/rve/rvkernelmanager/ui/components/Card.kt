@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rve.rvkernelmanager.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_expand_more_rounded_filled
import com.rve.rvkernelmanager.R

object Card {
    @Composable
    fun ExpandableCard(
        icon: Any?,
        text: String,
        onClick: () -> Unit,
        expanded: Boolean,
        content: @Composable () -> Unit
    ) {
        val rotateArrow by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(),
        )

        Card(
            onClick = onClick,
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceBright,
                contentColor = MaterialTheme.colorScheme.onSurface,
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
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    painter = painterResource(materialsymbols_ic_expand_more_rounded_filled),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = if (expanded) "Expanded" else "Collapsed",
                    modifier = Modifier.rotate(rotateArrow),
                )
            }
            content()
        }
    }

    @Composable
    fun ItemCard(
        shape: Shape = CardDefaults.shape,
        icon: Any? = null,
        title: String,
        titleLarge: Boolean = false,
        body: String? = null,
        onClick: (() -> Unit)? = null,
    ) {
        val colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        val content: @Composable ColumnScope.() -> Unit = {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (icon != null) {
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
                }
                Column(
                    modifier = Modifier.animateContentSize(
                        animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        style = if (titleLarge) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium
                    )
                    if (body != null) {
                        Text(
                            text = body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (onClick != null) {
            Card(
                onClick = onClick,
                shape = shape,
                colors = colors,
                content = content
            )
        } else {
            Card(
                shape = shape,
                colors = colors,
                content = content
            )
        }
    }

    @Composable
    fun SwitchOutlinedCard(
        shape: Shape = CardDefaults.shape,
        colors: CardColors = CardDefaults.outlinedCardColors(),
        border: BorderStroke = CardDefaults.outlinedCardBorder(),
        icon: Any?,
        text: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        OutlinedCard(
            shape = shape,
            colors = colors,
            border = border,
            onClick = { onCheckedChange(!checked) },
            interactionSource = interactionSource
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
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    interactionSource = interactionSource,
                    thumbContent = {
                        Crossfade(
                            targetState = checked,
                            animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
                        ) { isChecked ->
                            Icon(
                                imageVector = if (isChecked) Icons.Rounded.Check else Icons.Rounded.Close,
                                contentDescription = stringResource(if (isChecked) R.string.checked else R.string.unchecked),
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    }
                )
            }
        }
    }
}
