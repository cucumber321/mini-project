package com.example.bics.ui.dispatch.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.R
import com.example.bics.data.AppScreen
import com.example.bics.data.dispatch.Dispatch
import com.example.bics.data.user.ErrorCode
import com.example.bics.ui.dispatch.viewmodel.DispatchListViewModel
import com.example.bics.ui.dispatch.viewmodel.DispatchViewModelProvider
import com.example.bics.ui.schedule.formatDate
import com.example.bics.ui.theme.Typography
import com.example.bics.ui.user.UserTopBar
import java.lang.String.format
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatchListScreen(navController: NavController) {
    val dispatchListViewModel: DispatchListViewModel = viewModel(factory = DispatchViewModelProvider.Factory)
    val search by dispatchListViewModel.search.uiState.collectAsState()
    val dispatchList by dispatchListViewModel.dispatchList.collectAsState()
    val isLoading by dispatchListViewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        dispatchListViewModel.refresh()
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index to
            listState.layoutInfo.totalItemsCount
        }
            .collect { (lastVisible, total) ->
                if (lastVisible == null || lastVisible >= total - 2) {
                    dispatchListViewModel.loadMore()
                }
            }
    }


    Scaffold(
        topBar = {
            UserTopBar(
                title = "Dispatches",
                showBackButton = true,
                onBackButtonPressed = navController::navigateUp,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(AppScreen.AddDispatch.name) { launchSingleTop = true }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add New Dispatch"
                )
            }

        }
    ) { innerPadding ->
        Surface(Modifier.padding(innerPadding)) {
            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = dispatchListViewModel::refresh
            ){
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = search.fieldInput,
                        onValueChange = dispatchListViewModel.search::onValueChanged,
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { navController.navigate(AppScreen.FilterDispatch.name) { launchSingleTop = true } },
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.filter),
                                    contentDescription = "Filter",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        placeholder = { Text("Search Dispatch ID Or Ordered By") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        isError = search.errorCode != ErrorCode.None,
                        supportingText = {
                            Text(
                                stringResource(search.errorCode.errorMessage),
                                color = MaterialTheme.colorScheme.error,
                                style = Typography.bodySmall
                            )
                        }
                    )

                    LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                        items(
                            items = dispatchList
                                .filter {
                                    it.orderedBy.contains(search.fieldInput, true) ||
                                        it.id.contains(search.fieldInput, true)
                                        },
                            key = { it.id }) {
                            DispatchListItemCard(it) {
                                navController.navigate("${AppScreen.DispatchDetails.name}/${it.id}") {
                                    launchSingleTop = true
                                }
                            }
                        }
                    }

                }
            }
        }

    }
}

@Composable
fun DispatchListItemCard(dispatch: Dispatch, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Company name
            Column(Modifier.weight(1f)) {
                Text(
                    text = dispatch.orderedBy,
                    style = Typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = dispatch.id,
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = dispatch.dateCreated.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().formatDate(),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = format(stringResource(R.string.balance_format), dispatch.items.sumOf { it.quantity * it.product.price }),
                style = Typography.bodyLarge
            )
        }
    }
}

