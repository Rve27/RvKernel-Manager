/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.rve.rvkernelmanager.ui.theme

import android.app.Activity

import androidx.core.view.WindowCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*

import com.rve.rvkernelmanager.preference.SettingsPreference

@Composable
fun RvKernelManagerTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val settingsPreference = SettingsPreference.getInstance(context)
    val themeMode by settingsPreference.themeMode.collectAsState()

    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }
    
    val colorScheme = when {
        isDarkTheme -> {
	    dynamicDarkColorScheme(context)
	}
	!isDarkTheme -> {
	    dynamicLightColorScheme(context)
	}
        else -> expressiveLightColorScheme()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
	motionScheme = MotionScheme.expressive(),
        content = content
    )
}
