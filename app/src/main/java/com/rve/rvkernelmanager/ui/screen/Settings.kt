package com.rve.rvkernelmanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.rve.rvkernelmanager.ui.navigation.SettingsTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
	topBar = { SettingsTopAppBar(scrollBehavior = scrollBehavior) }
    ) {
	LazyColumn(
	    modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
	    state = rememberLazyListState()
	) {
	    item {
		Text("Settings")
	    }
	}
    }
}
