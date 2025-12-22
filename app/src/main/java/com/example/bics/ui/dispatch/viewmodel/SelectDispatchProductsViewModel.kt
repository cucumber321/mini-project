package com.example.bics.ui.dispatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.dispatch.DispatchItem
import com.example.bics.data.dispatch.DispatchRepository
import com.example.bics.data.product.ProductRepository
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import com.example.product.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectDispatchProductsViewModel(dispatchRepository: DispatchRepository, private val productRepository: ProductRepository): ViewModel() {

    private val _products = MutableStateFlow(emptyList<Product>())
    private val _selected = dispatchRepository.state.last().getMutableStateFlow("selected", emptyList<DispatchItem>())
    private val taken = dispatchRepository.state.last()["taken"] ?: emptyMap<String, Long>()
    private val _temp = MutableStateFlow(_selected.value.associateBy { it.product.id })
    private val _currentItem = MutableStateFlow("")

    val products = _products.asStateFlow()
    val selected = _temp.asStateFlow()
    val currentItem = _currentItem.asStateFlow()
    val searchQuery = FieldUiStateWrapper(MutableStateFlow(FieldUiState("")))

    init {
        viewModelScope.launch {
            _products.update { productRepository.getAllProducts() }
        }
    }

    fun onItemSelect(product: Product) {
        _currentItem.update { product.id }
        if (!_temp.value.contains(product.id)) _temp.update {
            it + mapOf(product.id to DispatchItem(product, 0))
        }
    }

    fun onQuantityIncrease() {
        _temp.update { it + mapOf(_currentItem.value to it[_currentItem.value]!!.increment(taken)) }
    }
    fun onQuantityDecrease() {
        _temp.update { it + mapOf(_currentItem.value to it[_currentItem.value]!!.decrement()) }
    }
    fun onQuantityChange(value: String) {
        _temp.update { it + mapOf(_currentItem.value to it[_currentItem.value]!!.onChange(value, taken)) }
    }

    fun onSave() {
        _selected.update { _temp.value.values.filter { it.quantity > 0 } }
    }
}
