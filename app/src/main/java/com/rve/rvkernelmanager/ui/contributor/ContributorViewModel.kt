/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
package com.rve.rvkernelmanager.ui.contributor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Contributor(val login: String, val id: Int, val avatar_url: String, val contributions: Int, val html_url: String) {
    val avatarUrl: String get() = avatar_url
    val htmlUrl: String get() = html_url
}

class ContributorViewModel : ViewModel() {
    private val _contributors = MutableStateFlow<List<Contributor>>(emptyList())
    val contributors: StateFlow<List<Contributor>> = _contributors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    fun loadContributors() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val contributorsList = withContext(Dispatchers.IO) {
                    fetchContributors()
                }
                _contributors.value = contributorsList
            } catch (e: UnknownHostException) {
                _error.value = "No internet connection"
            } catch (e: SocketTimeoutException) {
                _error.value = "Connection timeout"
            } catch (e: IOException) {
                _error.value = "Network error: ${e.message}"
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchContributors(): List<Contributor> {
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
                throw IOException("Server error: $errorMessage")
            }
        } finally {
            connection.disconnect()
        }
    }
}
