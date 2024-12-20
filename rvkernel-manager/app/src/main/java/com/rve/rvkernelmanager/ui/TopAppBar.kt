package com.rve.rvkernelmanager.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import com.rve.rvkernelmanager.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val context = LocalContext.current
    val githubUrl = stringResource(id = R.string.repo_url)
    val telegramUrl = stringResource(id = R.string.telegram_url)

    TopAppBar(
        title = {
	    Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onBackground
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
		    tint = MaterialTheme.colorScheme.onBackground,
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
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(35.dp)
                )
            }
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior
    )
}
