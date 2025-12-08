package com.example.product.Product.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.rememberAsyncImagePainter
import com.example.product.data.Product

@Composable
fun ProductDetails(product: Product?) {
    Scaffold(
        topBar = {
//            UserTopBar(
//                title = "Product",
//                showBackButton = true,
//                onBackButtonPressed = navController::navigateUp
//            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (product != null) {
                ProductDetailsScreen(product)
            } else {
                Text("Product not found")
            }
            Button(
                onClick = { /* To edit page*/ }
            ) {
                Text("Edit")
            }
            Button(
                onClick = { /* Delete product function */ }
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun ProductDetailsScreen(product: Product) {
    Row {
        Image(
            painter = rememberAsyncImagePainter(product.productImage),
            contentDescription = null,
            modifier = Modifier
        )
        Column {
            Text("Name: ${product.productName}")
            Text("Description: ${product.productDescription}")
        }
        Text("Price: ${product.productPrice}")
        Text("Quantity: ${product.productQuantity}")
    }
}