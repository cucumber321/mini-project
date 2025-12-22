package com.example.bics.ui.dispatch.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.bics.data.dispatch.Dispatch
import com.example.bics.data.dispatch.DispatchRepository
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.repository.profile.ProfileRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class EditDispatchViewModel(private val dispatchRepository: DispatchRepository, authRepository: AuthRepository, private val profileRepository: ProfileRepository): DispatchFormViewModel(dispatchRepository, authRepository) {
    lateinit var dispatch: Dispatch

    init {
        viewModelScope.launch {
            val dispatchId = dispatchRepository.state[dispatchRepository.state.lastIndex - 1]["dispatch_id"] ?: ""
            dispatch = dispatchRepository.getDispatch(dispatchId)
            taken = dispatch.items.associate { it.product.id to it.quantity }
            dispatchRepository.state.last()["taken"] = taken

            productList.onValueChanged(dispatch.items)
            orderedBy.onValueChanged(dispatch.orderedBy)
        }
    }

    override suspend fun submitAction() {
        dispatchRepository.updateDispatch(
            dispatch.copy(
                orderedBy = orderedBy.value(),
                items = productList.value(),
                lastModifiedBy = profileRepository.getUserStream().value.uid,
                lastModifiedDate = Timestamp.now()
            )
        )
    }
}