/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.data.Item
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.InventoryTheme
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */

private fun parseTimeString(timeString: String): Date? {
    val format = SimpleDateFormat("HH.mm", Locale.getDefault())
    return format.parse(timeString)
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    navigateToItemUpdate: (Int) -> Unit, // Adjusted parameter type
    navigateToSearchPage: () -> Unit,
    navigateToMapPage: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            InventoryTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            Column {

                FloatingActionButton(
                    onClick = navigateToMapPage, // Handle navigation to the SearchPage
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn, // Replace with appropriate search icon
                        contentDescription = stringResource(R.string.map_page_title)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(
                    onClick = navigateToSearchPage, // Handle navigation to the SearchPage
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                ) {
                    Icon(
                        imageVector = Icons.Default.Search, // Replace with appropriate search icon
                        contentDescription = stringResource(R.string.search_button_title)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp)) // Adjust spacing as needed
                FloatingActionButton(
                    onClick = navigateToItemEntry,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.item_entry_title)
                    )
                }

            }
        },
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            //MapView(modifier = Modifier.fillMaxSize()) // Add map view
            HomeBody(
                itemList = homeUiState.itemList,
                onItemClick = navigateToItemUpdate,
                modifier = Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_small))
                    .fillMaxSize()
            )
        }
    }
}



@Composable
private fun HomeBody(
    itemList: List<Item>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Group items by day of week
    val itemsByDayOfWeek = itemList.groupBy { it.dayOfWeek }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Create a Row for each day of the week
        for (dayOfWeek in listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")) {
            // Display items for the current day of the week, if any
            itemsByDayOfWeek[dayOfWeek]?.let { items ->
                Column {
                    // Display day of week as title
                    Text(
                        text = dayOfWeek,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp)
                    )
                    // Display items for the current day of the week, sorted by start time
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items.sortedBy { parseTimeString(it.startTime) } // Sıralama işlemi
                            .forEach { item ->
                                InventoryItem(
                                    item = item,
                                    onItemClick = onItemClick,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                    }
                }
            }
        }
    }
}
/*
@Composable
private fun InventoryList(
    itemList: List<Item>, onItemClick: (Item) -> Unit, modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = itemList, key = { it.id }) { item ->
            InventoryItem(item = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onItemClick(item) })
        }
    }
}
*/





@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InventoryItem(
    item: Item,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().height(75.dp), // Adjusted width and height modifier
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onItemClick(item.id) }
    )
    {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.courseId,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.day_of_week),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 6.sp,

                    )
                Spacer(Modifier.size(5.dp))
                Text(
                    text = stringResource(R.string.in_stock, item.dayOfWeek),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 6.sp,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.start_time, item.startTime),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 6.sp
                )
                Spacer(Modifier.size(10.dp))
                Text(
                    text = stringResource(R.string.in_stock, item.startTime),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 6.sp
                )


            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.end_time, item.startTime),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 6.sp
                )
                Spacer(Modifier.size(10.dp))
                Text(
                    text = item.endTime,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 6.sp
                )


            }

        }
    }
}



/*
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://api.multiavatar.com/Starcrasher.png")
                    .crossfade(true)
                    .build(),
                error = painterResource(R.drawable.ic_launcher_background),
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "Image from API",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp) // Adjust the height as needed
            )*/

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    InventoryTheme {
        HomeBody(listOf(
            Item(1, "Comp302", "Software Engineering", "Monday", "11.20", "12.30"), Item(2, "Comp132", "Ada", "Monday", "13.00", "14.10"), Item(3, "Econ101", "Ad", "Monday", "8.30", "9.40"), Item(4, "Comp302", "Software Engineering", "Tuesday", "11.20", "12.30")
        ), onItemClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    InventoryTheme {
        HomeBody(listOf(), onItemClick = {})
    }
}
/*
@Preview(showBackground = true)
@Composable
fun InventoryItemPreview() {
    InventoryTheme {
        InventoryItem(
            Item(1, "Game", "Adalar", "5053554953", "game@gmail.com", "1"),
        )
    }
}

 */



