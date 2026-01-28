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
package com.rve.rvkernelmanager.ui.contributor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rve.rvkernelmanager.R
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Contributor(val login: String, val id: Int, val avatar_url: String, val contributions: Int, val html_url: String) {
    val avatarUrl: String get() = avatar_url
    val htmlUrl: String get() = html_url
}

class ContributorViewModel(application: Application) : AndroidViewModel(application) {
    private val _contributors = MutableStateFlow<List<Contributor>>(emptyList())
    val contributors: StateFlow<List<Contributor>> = _contributors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        loadContributors()
    }

    fun loadContributors() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val context = getApplication<Application>()

            try {
                val contributorsList = withContext(Dispatchers.IO) {
                    fetchContributors(context)
                }
                _contributors.value = contributorsList
            } catch (e: UnknownHostException) {
                _error.value = context.getString(R.string.no_internet)
            } catch (e: SocketTimeoutException) {
                _error.value = context.getString(R.string.timeout)
            } catch (e: IOException) {
                _error.value = "${context.getString(R.string.network_error)}: ${e.message}"
            } catch (e: Exception) {
                _error.value = "${context.getString(R.string.error_prefix)} ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchContributors(application: Application): List<Contributor> {
        val url = URL("https://api.github.com/repos/Rve27/RvKernel-Manager/contributors")
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.apply {
                requestMethod = "GET"
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "RvKernel-Manager-Android")
                connectTimeout = 15000
                readTimeout = 15000
            }

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                json.decodeFromString<List<Contributor>>(response)
            } else {
                val errorMessage = try {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                } catch (e: Exception) {
                    "HTTP $responseCode"
                }
                throw IOException("${application.getString(R.string.server_error)}: $errorMessage")
            }
        } finally {
            connection.disconnect()
        }
    }
}
