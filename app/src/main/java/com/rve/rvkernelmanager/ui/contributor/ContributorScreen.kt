/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.contributor

import android.app.Activity

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext

import com.rve.rvkernelmanager.ui.component.appBar.TopAppBarWithBackButton

@Composable
fun ContributorScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = {
	    TopAppBarWithBackButton(
		text = "Contributors",
		onBack = { (context as? Activity)?.finish() }
	    )
	}
    ) { innerPadding ->
	Box(
	    modifier = Modifier
		.fillMaxSize()
		.padding(innerPadding),
	    contentAlignment = Alignment.Center
	) {
	    Text("Contributors placeholder")
	}
    }
}
