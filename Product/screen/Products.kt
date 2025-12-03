package com.example.product.Product.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.product.data.Product
import coil.compose.rememberAsyncImagePainter

@Composable
fun Product(){
    val items = listOf(
    "Item 1", "Item 2", "Item 3", "Item 4",
    "Item 5", "Item 6", "Item 7", "Item 8"
    )

    var search by remember { mutableStateOf("") }
    var isSearch by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
//            UserTopBar(
//                title = "Product",
//                showBackButton = true,
//                onBackButtonPressed = navController::navigateUp
//            )
        }
    ){ innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row{
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                {
                    OutlinedTextField(
                        onValueChange = { search = it },
                        value = search,
                        label = { Text("Search") },
                        modifier = Modifier.size(width=300.dp,height=40.dp),
                        singleLine = true
                    )
                    IconButton(
                        onClick = { isSearch = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Creates 2 columns
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items) { item ->
                        if (items != null) {
                            if (isSearch && item.contains(search)) {
                                CardItem()
                            } else {
                                Text(text = "No Product found")
                            }
                        } else {
                            Text(text = "No Product yet")
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun CardItem(product: Product? = null,)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),

        onClick = {/*navigate to product details*/}

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = product?.productName ?: "No Name",
                style = MaterialTheme.typography.titleLarge
            )
            Image(
              painter = rememberAsyncImagePainter(product?.productImage),
              contentDescription = null,
              modifier = Modifier

            )
            Text(
                text = "Price: ${product?.productPrice ?: "No Price"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Quantity: ${product?.productQuantity ?: "No Quantity"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
