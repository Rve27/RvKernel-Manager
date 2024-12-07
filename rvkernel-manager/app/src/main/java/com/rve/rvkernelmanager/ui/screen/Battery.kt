package com.rve.rvkernelmanager.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChargingCard()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val context = LocalContext.current
    val githubUrl = stringResource(id = R.string.repo_url)
    val telegramUrl = stringResource(id = R.string.telegram_url)

    TopAppBar(
        title = {
	    Text(
                text = stringResource(R.string.tab_battery),
                color = MaterialTheme.colorScheme.onPrimaryContainer
	    )
        },
        actions = {
            IconButton(
		onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                    context.startActivity(intent)
	        },
		modifier = Modifier
		    .size(35.dp)
		    .padding(end = 12.dp)
	    ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_github),
                    contentDescription = "Github",
		    tint = MaterialTheme.colorScheme.onPrimaryContainer,
		    modifier = Modifier.size(35.dp)
                )
            }

	    IconButton(
		onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl))
                    context.startActivity(intent)
                },
		modifier = Modifier
                    .size(35.dp)
                    .padding(end = 12.dp)
	    ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_telegram),
                    contentDescription = "Telegram Group",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(35.dp)
                )
            }
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun ChargingCard() {
    ElevatedCard(
	shape = CardDefaults.shape,
	colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.charging_title),
                    style = MaterialTheme.typography.titleMedium,
		    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
	    }
        }
    }
}
