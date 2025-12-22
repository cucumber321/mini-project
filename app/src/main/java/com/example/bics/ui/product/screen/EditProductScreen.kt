package com.example.bics.ui.product.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bics.ui.product.viewmodel.ProductViewModel
import com.example.bics.ui.product.viewmodel.ProductViewModelProvider

// edit product screen
@Composable
fun EditProductScreen(
    productId: String,
    onCancel: () -> Unit
) {
    val viewModel: ProductViewModel = viewModel(factory = ProductViewModelProvider.Factory)
    val isLoading by viewModel.isLoading.collectAsState()
    val success by viewModel.success.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitial(productId)
    }

    // Show loading indicator while fetching data
    if (isLoading) {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    // Show ProductScreen only when data is loaded
    else if (success) {
        ProductScreen(
            // keep the same ID
            onCancel = onCancel,
            viewModel = viewModel,
            title = "Edit Product"
        )
    } else {
        // Handle case where product couldn't be loaded
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Product not found")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onCancel) {
                    Text("Go Back")
                }
            }
        }
    }
}