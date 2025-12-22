package com.example.bics.ui.product.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.bics.ui.product.viewmodel.ProductViewModel
import com.example.bics.ui.user.UserTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    onCancel: () -> Unit,
    viewModel: ProductViewModel,
    title: String
) {

    val name by viewModel.name.uiState.collectAsState()
    val price by viewModel.price.uiState.collectAsState()
    val quantity by viewModel.quantity.uiState.collectAsState()
    val description by viewModel.description.uiState.collectAsState()
    val imageUrl by viewModel.imageUrl.uiState.collectAsState()
    val imageUri by viewModel.imageUri.uiState.collectAsState()
    val enabled by viewModel.enabled.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> viewModel.imageUri.onValueChanged(uri) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            UserTopBar(
                title = title,
                showBackButton = true,
                onBackButtonPressed = onCancel,
                enabled = enabled
            )
        }
    ) { padding ->
        Surface(Modifier
            .padding(padding)
            .fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Column(Modifier
                    .verticalScroll(scrollState)
                    .weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp))
                            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp))
                            .clickable(enabled = enabled) { launcher.launch("image/*") }
                            .clip(RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            imageUri.fieldInput != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(imageUri.fieldInput),
                                    contentDescription = "",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            imageUrl.fieldInput.isNotEmpty() -> {
                                Image(
                                    painter = rememberAsyncImagePainter(imageUrl.fieldInput),
                                    contentDescription = "",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            else -> {
                                Image(
                                    painter = painterResource(android.R.drawable.ic_menu_gallery),
                                    contentDescription = "",
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value = name.fieldInput,
                        onValueChange = viewModel.name::onValueChanged,
                        label = { Text("Name") },
                        singleLine = true,
                        enabled = enabled,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    OutlinedTextField(
                        value = price.fieldInput,
                        onValueChange = viewModel.price::onValueChanged,
                        label = { Text("Price") },
                        enabled = enabled,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                    )
                    OutlinedTextField(
                        value = quantity.fieldInput,
                        onValueChange = viewModel.quantity::onValueChanged,
                        label = { Text("Quantity") },
                        enabled = enabled,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
                    )
                    OutlinedTextField(
                        value = description.fieldInput,
                        onValueChange = viewModel.description::onValueChanged,
                        label = { Text("Description") },
                        enabled = enabled,
                        modifier = Modifier
                            .fillMaxWidth(),
                        minLines = 4,
                    )
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    enabled = enabled,
                    onClick = {
                        viewModel.onSubmit(onCancel) {
                            Toast.makeText(
                                context,
                                it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Product")
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    enabled = enabled,
                    onClick = { onCancel() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Cancel") }
            }
        }
    }
}




