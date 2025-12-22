package com.example.bics.ui.dispatch.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bics.ui.dispatch.QuantityInput
import com.example.bics.ui.dispatch.viewmodel.DispatchViewModelProvider
import com.example.bics.ui.dispatch.viewmodel.SelectDispatchProductsViewModel
import com.example.bics.ui.product.screen.ProductCard
import com.example.bics.ui.user.ThickButton
import com.example.bics.ui.user.UserTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDispatchProductsScreen(navController: NavController) {
    val viewModel: SelectDispatchProductsViewModel = viewModel(factory = DispatchViewModelProvider.Factory)
    val searchQuery by viewModel.searchQuery.uiState.collectAsState()
    val products by viewModel.products.collectAsState()
    val selected by viewModel.selected.collectAsState()
    val currentItem by viewModel.currentItem.collectAsState()

    val filteredProducts = if (searchQuery.fieldInput.isBlank()) {
        products
    } else {
        products.filter {
            it.name.contains(searchQuery.fieldInput, ignoreCase = true) || it.id.contains(searchQuery.fieldInput, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            UserTopBar(
                title = "Select Products",
                showBackButton = true,
                onBackButtonPressed = navController::navigateUp
            )
        },
        bottomBar = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    selected[currentItem]?.let {
                        Text("Quantity")
                        QuantityInput(
                            dispatchItem = it,
                            onQuantityDecrease = { viewModel.onQuantityDecrease() },
                            onQuantityIncrease = { viewModel.onQuantityIncrease() },
                            onQuantityChange = {a, b -> viewModel.onQuantityChange(b) },
                            buttonSize = 50.dp
                        )
                    }
                }
                ThickButton(
                    text = "Select"
                ) {
                    viewModel.onSave()
                    navController.navigateUp()
                }
            }
        }
    ) { innerPadding ->
        Surface(Modifier.padding(innerPadding)) {
            Column {
                OutlinedTextField(
                    value = searchQuery.fieldInput,
                    onValueChange = viewModel.searchQuery::onValueChanged,
                    label = { Text("Search Name or ID") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductCard(product, currentlySelected = product.id == currentItem, selected = (selected[product.id]?.quantity?.let { it > 0 } ?: false), onClick = { viewModel.onItemSelect(product) })
                    }
                }
            }
        }

    }
}