/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

// Dear programmer:
// When I wrote this code, only god and
// I knew how it worked.
// Now, only god knows it!
//
// Therefore, if you are trying to optimize
// this routine and it fails (most surely),
// please increase this counter as a
// warning for the next person:
//
// total hours wasted here = 254
//
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.rve.rvkernelmanager.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_restart_alt_rounded_filled
import com.composables.icons.materialsymbols.roundedfilled.R.drawable.materialsymbols_ic_settings_rounded_filled
import com.rve.rvkernelmanager.R
import com.rve.rvkernelmanager.ui.settings.SettingsActivity
import com.rve.rvkernelmanager.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SimpleTopAppBar() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isExpanded by remember { mutableStateOf(false) }

    val rebootMenu = listOf(
        Pair(stringResource(R.string.reboot_system), ""),
        Pair(stringResource(R.string.reboot_recovery), "recovery"),
        Pair(stringResource(R.string.reboot_bootloader), "bootloader"),
        Pair(stringResource(R.string.reboot_edl), "edl"),
    )

    TopAppBar(
        title = { Text(stringResource(R.string.app_name), maxLines = 1, overflow = TextOverflow.Ellipsis) },
        actions = {
            Row {
                TooltipBox(
                    positionProvider =
                        TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Below,
                        ),
                    tooltip = { PlainTooltip(caretShape = TooltipDefaults.caretShape()) { Text(stringResource(R.string.settings)) } },
                    state = rememberTooltipState(),
                ) {
                    IconButton(
                        onClick = {
                            context.startActivity(Intent(context, SettingsActivity::class.java))
                        },
                    ) {
                        Icon(
                            painter = painterResource(materialsymbols_ic_settings_rounded_filled),
                            contentDescription = stringResource(R.string.menu),
                        )
                    }
                }
                TooltipBox(
                    positionProvider =
                        TooltipDefaults.rememberTooltipPositionProvider(
                            TooltipAnchorPosition.Below,
                        ),
                    tooltip = { PlainTooltip(caretShape = TooltipDefaults.caretShape()) { Text(stringResource(R.string.reboot_menu)) } },
                    state = rememberTooltipState(),
                ) {
                    IconButton(
                        onClick = { isExpanded = true },
                    ) {
                        Icon(
                            painter = painterResource(materialsymbols_ic_restart_alt_rounded_filled),
                            contentDescription = stringResource(R.string.menu),
                        )
                    }
                }
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false },
                    shape = MaterialTheme.shapes.large,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    rebootMenu.forEach { r ->
                        DropdownMenuItem(
                            text = { Text(r.first) },
                            onClick = {
                                isExpanded = false
                                scope.launch(Dispatchers.IO) {
                                    Utils.reboot(r.second)
                                }
                            },
                        )
                    }
                }
            }
        },
    )
}

@Composable
fun TopAppBarWithBackButton(text: String, onBack: () -> Unit, scrollBehavior: TopAppBarScrollBehavior) {
    LargeFlexibleTopAppBar(
        title = {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Below,
                    ),
                tooltip = { PlainTooltip(caretShape = TooltipDefaults.caretShape()) { Text(stringResource(R.string.back)) } },
                state = rememberTooltipState(),
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
