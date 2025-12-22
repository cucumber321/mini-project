package com.example.bics.ui.dispatch.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.bics.data.dispatch.DispatchItem
import com.example.bics.data.dispatch.DispatchRepository
import com.example.bics.data.repository.auth.AuthRepository
import com.example.bics.data.user.ErrorCode
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import com.example.bics.ui.user.viewmodel.FormViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

abstract class DispatchFormViewModel(private val dispatchRepository: DispatchRepository, private val authRepository: AuthRepository): FormViewModel() {
    protected val sharedList = dispatchRepository.state.last().getMutableStateFlow("selected", emptyList<DispatchItem>())
    protected val _productList = MutableStateFlow(FieldUiState(emptyList<DispatchItem>()))
    protected val _orderedBy = MutableStateFlow(FieldUiState(""))
    protected val _showConfirm = MutableStateFlow(false)
    protected var taken = emptyMap<String, Long>()
    val orderedBy = FieldUiStateWrapper(_orderedBy)
    val productList = FieldUiStateWrapper(_productList)
    val showConfirm = _showConfirm.asStateFlow()
    var productToRemove = ""

    override fun onCleared() {
        super.onCleared()
        dispatchRepository.state.removeAt(dispatchRepository.state.lastIndex)
    }

    init {
        sharedList.onEach { list ->
            productList.onValueChanged(list)
            _productList.update { it.copy(errorCode = ErrorCode.None) }
        }.launchIn(viewModelScope)
    }

    fun onQuantityIncrease(id: String) {
        _productList.update { list ->
            list.copy(fieldInput = list.fieldInput.map {
                if (it.product.id == id) it.increment(taken)
                else it
            })
        }
    }
    fun onQuantityDecrease(id: String) {
        _productList.update { list ->
            list.copy(fieldInput = list.fieldInput.map {
                if (it.product.id == id)
                    if (it.quantity > 1) it.copy(quantity = it.quantity - 1)
                    else {
                        productToRemove = it.product.id
                        _showConfirm.update { true }
                        it.copy(quantity = 1)
                    }
                else it
            })
        }
    }
    fun onQuantityChange(id: String, value: String) {
        _productList.update { list ->
            list.copy(fieldInput = list.fieldInput.map {
                if (it.product.id == id) it.onChange(value, taken)
                else it
            })
        }
    }

    fun onRemove() {
        _productList.update { list ->
            list.copy(fieldInput = list.fieldInput
                .filter { it.product.id != productToRemove }
            )
        }
    }

    fun onDismiss() {
        _showConfirm.update { false }
    }

    fun updateSharedList() {
        sharedList.update { productList.value() }
    }

    fun validateProductList(): Boolean {
        return if (productList.value().isEmpty()) {
            _productList.update {
                it.copy(errorCode = ErrorCode.NoDispatchProducts)
            }
            false
        } else if (productList.value().any {it.quantity == 0L}) false
        else true
    }

    override suspend fun onSubmit(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {

        if (!_showConfirm.value && validateAllFields()) {
            _available.update { false }
            if (authRepository.refresh()) {
                submitAction()
                onSuccess()
            }
            _available.update { true }
        }
    }

    override fun validateAllFields(): Boolean {
        return listOf(
            validateBasicField(_orderedBy, ErrorCode.EmptyField),
            validateProductList()
        ).all { it }
    }

    override fun processFieldErrorCode(errorCode: ErrorCode) {

    }

    abstract suspend fun submitAction()
}