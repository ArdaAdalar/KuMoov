package com.example.inventory.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.inventory.R
import com.example.inventory.ui.navigation.NavigationDestination
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// API Data Classes
@Serializable
data class RouteRequest(val bitis_duragi: String)
@Serializable
data class RouteResponse(val output: List<String>)  // output is now a list of strings


// Navigation object for the Search page
object SearchDestination : NavigationDestination {
    override val route = "search"
    override val titleRes = R.string.search_page_title
}

// Network module to handle API requests
object NetworkModule {
    private val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getRoutesForDestination(destination: String): RouteResponse {
        return client.post("http://10.0.2.2:5010/process_destination") { // Ensure this matches your Flask endpoint
            contentType(ContentType.Application.Json)
            body = RouteRequest(destination)
        }
    }
}

// Main Composable Function for Search Page
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchPage(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    var backendResponse by remember { mutableStateOf<String?>(value = null)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter destination") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                coroutineScope.launch {
                    try {
                        val response = NetworkModule.getRoutesForDestination(searchText)
                        backendResponse =
                            response.output.toString()  // Displaying the output of the main function
                    } catch (e: Exception) {
                        backendResponse = "Failed to fetch data: ${e.message}"
                    }
                }
            })
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val response = NetworkModule.getRoutesForDestination(searchText)
                        backendResponse =
                            response.output.toString()  // Assuming 'output' is a String containing all info
                    } catch (e: Exception) {
                        backendResponse = "Failed to fetch data: ${e.localizedMessage}"
                    }

                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        // Displaying the backend response directly
        Text(text = backendResponse ?: "No data received")
    }
}
