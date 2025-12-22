package com.example.bics.ui.dispatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bics.data.dispatch.Dispatch
import com.example.bics.data.dispatch.DispatchFilterSettings
import com.example.bics.data.dispatch.DispatchRepository
import com.example.bics.data.user.FieldUiState
import com.example.bics.data.user.FieldUiStateWrapper
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

class DispatchListViewModel(private val dispatchRepository: DispatchRepository): ViewModel() {

    val filterSettings = dispatchRepository.state.last().getMutableStateFlow("filter", DispatchFilterSettings())
    val search = FieldUiStateWrapper(MutableStateFlow(FieldUiState("")))
    private val _dispatchList = MutableStateFlow(emptyList<Dispatch>())
    private val _isLoading = MutableStateFlow(false)

    val dispatchList = _dispatchList.asStateFlow()
    val isLoading = _isLoading.asStateFlow()


    override fun onCleared() {
        super.onCleared()
        dispatchRepository.state.removeAt(dispatchRepository.state.lastIndex)
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.update { true }
            _dispatchList.update {
                dispatchRepository.getDispatches(
                    filterSettings.value.startDateSeconds?.let {
                        Timestamp(Instant.ofEpochSecond(it))
                    },
                    filterSettings.value.endDateSeconds?.let {
                        Timestamp(Instant.ofEpochSecond(it))
                    },
                )
            }
            delay(100)
            _isLoading.update { false }
        }
    }

    fun loadMore() {
        if (_isLoading.value || dispatchRepository.endReached) return
        viewModelScope.launch {
            _isLoading.update { true }
            _dispatchList.update { it + dispatchRepository.loadMore() }
            delay(100)
            _isLoading.update { false }
        }
    }

}