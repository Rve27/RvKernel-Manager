/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

fun Modifier.shimmerEffect(widthOfShadowBrush: Int = 500, angleOfAxisY: Float = 270f, durationMillis: Int = 1000): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "Shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer animation",
    ).value

    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val shimmerColors = listOf(
        onSurfaceVariant.copy(alpha = 0.3f),
        onSurfaceVariant.copy(alpha = 0.5f),
        onSurfaceVariant.copy(alpha = 1.0f),
        onSurfaceVariant.copy(alpha = 0.5f),
        onSurfaceVariant.copy(alpha = 0.3f),
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation - widthOfShadowBrush, y = 0.0f),
            end = Offset(x = translateAnimation, y = angleOfAxisY),
        ),
    )
}
