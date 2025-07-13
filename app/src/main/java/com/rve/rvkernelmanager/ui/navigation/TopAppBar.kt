package com.rve.rvkernelmanager.ui.navigation

import android.net.Uri
import android.app.Activity
import android.content.Intent

import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*

import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.activity.SettingsActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinnedTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    TopAppBar(
	title = { Text("RvKernel Manager", maxLines = 1, overflow = TextOverflow.Ellipsis) },
	actions = {
	    IconButton(onClick = { expanded = true }) {
		Icon(
		    imageVector = Icons.Filled.MoreVert,
		    contentDescription = "More",
		)
	    }
	    DropdownMenu(
		expanded = expanded,
		onDismissRequest = {
		    expanded = false
		}
	    ) {
		DropdownMenuItem(
		    text = {
			Text("Source code")
		    },
		    onClick = {
			context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Rve27/RvKernel-Manager")))
			expanded = false
		    },
		    leadingIcon = {
			Icon(
			    painter = painterResource(R.drawable.ic_github),
			    contentDescription = null
			)
		    }
		)
		DropdownMenuItem(
		    text = {
			Text("Telegram group")
		    },
		    onClick = {
			context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/rve_enterprises")))
			expanded = false
		    },
		    leadingIcon = {
			Icon(
			    painter = painterResource(R.drawable.ic_telegram),
			    contentDescription = null
			)
		    }
		)
		DropdownMenuItem(
		    text = {
			Text("Settings")
		    },
		    onClick = {
			context.startActivity(Intent(context, SettingsActivity::class.java))
			expanded = false
		    },
		    leadingIcon = {
			Icon(
			    Icons.Filled.Settings,
			    contentDescription = null
			)
		    }
		)
	    }
	},
	scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    val context = LocalContext.current

    TopAppBar(
	title = { Text("Settings", maxLines = 1, overflow = TextOverflow.Ellipsis) },
	navigationIcon = {
            IconButton(onClick = { (context as? Activity)?.finish() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
	scrollBehavior = scrollBehavior
    )
}
