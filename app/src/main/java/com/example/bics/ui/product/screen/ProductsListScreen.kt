package com.example.bics.ui.product.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.bics.R
import com.example.bics.ui.product.viewmodel.ProductListViewModel
import com.example.bics.ui.product.viewmodel.ProductViewModelProvider
import com.example.bics.ui.theme.Typography
import com.example.bics.ui.user.UserTopBar
import com.example.product.data.Product
import java.lang.String.format

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onAddClick: () -> Unit,
    onItemClick: (String) -> Unit,
    onBackButtonClick: () -> Unit
) {
    val viewModel: ProductListViewModel = viewModel(factory = ProductViewModelProvider.Factory)
    val searchQuery by viewModel.searchQuery.uiState.collectAsState()
    val products by viewModel.products.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

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
                showBackButton = true,
                onBackButtonPressed = onBackButtonClick,
                title = "Products"
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddClick() }, containerColor = Color(0xFF82A9FF)) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        Surface(Modifier.padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                OutlinedTextField(
                    value = searchQuery.fieldInput,
                    onValueChange = viewModel.searchQuery::onValueChanged,
                    label = { Text("Search Name or ID") },
                    singleLine = true,
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
                        ProductCard(product, onClick = { onItemClick(product.id) })
                    }

                }
            }
        }
    }
}


@Composable
fun ProductCard(product: Product, onClick: () -> Unit, selected: Boolean = false, currentlySelected: Boolean = false) {
    Card(
        onClick = onClick,
        border = when {
            currentlySelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            selected -> BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
            else -> null
        },
        modifier = Modifier
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(product.name, style = Typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(product.id, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = rememberAsyncImagePainter(product.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Quantity: ${product.quantity}", maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${format(stringResource(R.string.balance_format), product.price)}", maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

