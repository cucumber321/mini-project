package com.example.bics.ui.product.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.product.ProductRepository
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import com.example.product.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductListViewModel(private val productRepository: ProductRepository): ViewModel() {

    val searchQuery = FieldUiStateWrapper(MutableStateFlow(FieldUiState("")))

    private val _products = MutableStateFlow(emptyList<Product>())

    val products = _products.asStateFlow()

    fun refresh() {
        viewModelScope.launch{
            _products.update { productRepository.getAllProducts() }
        }
    }
}