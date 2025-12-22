package com.example.bics.ui.dispatch.viewmodel

import com.example.bics.data.dispatch.DispatchRepository
import com.example.bics.data.repository.auth.AuthRepository

class AddDispatchViewModel(private val dispatchRepository: DispatchRepository, authRepository: AuthRepository): DispatchFormViewModel(dispatchRepository, authRepository) {
    override suspend fun submitAction() {
        dispatchRepository.createDispatch(orderedBy.value(), productList.value())
    }
}