package com.example.bics.ui.product.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.bics.R
import com.example.bics.ui.user.UserTopBar
import com.example.product.data.Product
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.internal.format

//View product screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewProductScreen(
    productId: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    var product by remember { mutableStateOf<Product?>(null) }
    var description by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(productId) {
        firestore.collection("Products").document(productId).get()
            .addOnSuccessListener { doc ->
                product = Product(
                    id = doc.getString("id") ?: "",
                    name = doc.getString("name") ?: "",
                    price = doc.getDouble("price") ?: 0.0,
                    quantity = doc.getLong("quantity") ?: 0,
                    imageUrl = doc.getString("imageUrl") ?: ""
                )
                description = doc.getString("description") ?: ""
            }
    }

    if (product == null) {
        Surface {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
        return
    }

    Scaffold(
        topBar = {
            UserTopBar(
                showBackButton = true,
                onBackButtonPressed = onBack,
                title = "Product Details",
                showActions = true,
                onActionsPressed = { showDeleteDialog = true }
            )
        }
    ) { padding ->
        Surface(Modifier.padding(padding)) {
            Column(Modifier
                .padding(20.dp)
                .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(product!!.imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                    )

                    Spacer(Modifier.height(20.dp))

                    Text("ID: ${product!!.id}", fontSize = 18.sp)
                    Text("Name: ${product!!.name}", fontSize = 18.sp)
                    Text(
                        "Price: ${
                            format(
                                stringResource(R.string.balance_format),
                                product!!.price
                            )
                        }",
                        fontSize = 18.sp
                    )
                    Text("Quantity: ${product!!.quantity}", fontSize = 18.sp)
                    Text("Description: $description", fontSize = 18.sp)
                }
                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Edit") }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },

                        title = { Text("Confirm Delete") },

                        text = { Text("Are you sure you want to delete this product? This action cannot be undone.") },

                        confirmButton = {
                            TextButton(
                                onClick = {
                                    firestore.collection("Products")
                                        .document(productId)
                                        .delete()

                                    showDeleteDialog = false
                                    Toast.makeText(
                                        context,
                                        "Product has been successfully deleted",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onDelete()
                                }
                            ) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        },

                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) { Text("Back") }

            }
        }
    }
}