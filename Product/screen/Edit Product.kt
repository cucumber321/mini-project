package com.example.product.Product.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EditProduct(){
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    Row{

        //Upload image function

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth().size(width=300.dp,height=20.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Product Description") },
            modifier = Modifier.fillMaxWidth().size(width=300.dp,height=40.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Product Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().size(width=300.dp,height=40.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Product Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().size(width=300.dp,height=40.dp),
            singleLine = true
        )

        Button(
            onClick = { /* Edit product function*/ }
        ) {
            Text("Edit")
        }
        Button(
            onClick = { /* Return to previous screen */ }
        ) {
            Text("Cancel")
        }
    }

}