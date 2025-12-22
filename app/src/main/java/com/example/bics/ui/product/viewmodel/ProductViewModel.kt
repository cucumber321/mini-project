package com.example.bics.ui.product.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.product.ProductRepository
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.format

class ProductViewModel(private val productRepository: ProductRepository, private val authRepository: AuthRepository): ViewModel() {
    private val _productId = MutableStateFlow(FieldUiState(""))
    private val _name = MutableStateFlow(FieldUiState(""))
    private val _price = MutableStateFlow(FieldUiState(""))
    private val _quantity = MutableStateFlow(FieldUiState(""))
    private val _description = MutableStateFlow(FieldUiState(""))
    private val _imageUrl = MutableStateFlow(FieldUiState(""))
    private val _imageUri = MutableStateFlow(FieldUiState<Uri?>(null))
    private val _isLoading = MutableStateFlow(false)
    private val _success = MutableStateFlow(false)
    private val _enabled = MutableStateFlow(true)
    val productId = FieldUiStateWrapper(_productId)
    val name = FieldUiStateWrapper(_name)
    val price = FieldUiStateWrapper(_price)
    val quantity = FieldUiStateWrapper(_quantity)
    val description = FieldUiStateWrapper(_description)
    val imageUrl = FieldUiStateWrapper(_imageUrl)
    val imageUri = FieldUiStateWrapper(_imageUri)
    val isLoading = _isLoading.asStateFlow()
    val success = _success.asStateFlow()
    val enabled = _enabled.asStateFlow()

    fun loadInitial(pID: String) {
        if (productId.value().isNotEmpty()) return
        viewModelScope.launch {
            productId.onValueChanged(pID)
            _isLoading.update { true }
            val product = productRepository.getProduct(pID)
            name.onValueChanged(product.name)
            price.onValueChanged(format("%.02f", product.price))
            quantity.onValueChanged(product.quantity.toString())
            description.onValueChanged(product.description)
            imageUrl.onValueChanged(product.imageUrl)
            _success.update { true }
            _isLoading.update { false }
        }
    }

    fun onSubmit(onBack: () -> Unit, onToast: (String) -> Unit) {
        viewModelScope.launch {
            _enabled.update { false }
            if (imageUri.value() == null && imageUrl.value().isEmpty()) {
                onToast("Select an image")
                _enabled.update { true }
                return@launch
            }
            if (name.value().isBlank() || price.value().isBlank() || quantity.value().isBlank() || description.value().isBlank()) {
                onToast("Fill all fields")
                _enabled.update { true }
                return@launch
            }

            try {
                val p = (price.value().toDouble() * 100).toLong().toDouble() / 100
                val q = quantity.value().toLong()

                if (p < 0 || q < 0) throw NumberFormatException()

                if (!authRepository.refresh()) return@launch

                var productId = productId.value()
                var imageUrl = imageUrl.value()

                if (productId.isEmpty()) {
                    val id = productRepository.generateId {
                        onToast("Failed to generate ID")
                    }
                    if (id.isEmpty()) {
                        _enabled.update { true }
                        return@launch
                    }
                    productId = id
                }

                if (imageUri.value() != null) {
                    val url = productRepository.uploadImage(productId,imageUri.value()!!, onToast)
                    if (url.isEmpty()) {
                        _enabled.update { true }
                        return@launch
                    }
                    imageUrl = url
                }
                productRepository.saveProduct(productId, name.value(), p, q, description.value(), imageUrl, onToast)
                onBack()
            } catch (_: Exception) {
                onToast("Invalid numbers")
                _enabled.update { true }
            }
        }
    }
}