package com.example.bics.ui.dispatch.viewmodel

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bics.bicsApplication
import com.example.bics.ui.report.ReportViewModel

object DispatchViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            FilterDispatchViewModel(this.bicsApplication().container.dispatchRepository)
        }

        initializer {
            val container = this.bicsApplication().container
            container.dispatchRepository.state.add(this.createSavedStateHandle())
            DispatchListViewModel(container.dispatchRepository)
        }

        initializer {
            val container = this.bicsApplication().container
            container.dispatchRepository.state.add(this.createSavedStateHandle())
            AddDispatchViewModel(container.dispatchRepository, container.authRepository)
        }

        initializer {
            val container = this.bicsApplication().container
            container.dispatchRepository.state.add(this.createSavedStateHandle())
            EditDispatchViewModel(container.dispatchRepository, container.authRepository, container.profileRepository)
        }

        initializer {
            val container = this.bicsApplication().container
            SelectDispatchProductsViewModel(container.dispatchRepository, container.productRepository)
        }

        initializer {
            val container = this.bicsApplication().container
            DispatchDetailsViewModel(container.dispatchRepository, container.profileRepository, container.authRepository)
        }

        initializer {
            val container = this.bicsApplication().container
            ReportViewModel(container.dispatchRepository, container.productRepository)
        }
    }
}