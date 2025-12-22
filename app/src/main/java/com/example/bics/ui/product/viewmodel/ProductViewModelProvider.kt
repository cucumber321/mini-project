package com.example.bics.ui.product.viewmodel

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bics.bicsApplication

object ProductViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val container = this.bicsApplication().container
            ProductViewModel(container.productRepository, container.authRepository)
        }
        initializer {
            ProductListViewModel(this.bicsApplication().container.productRepository)
        }
    }
}