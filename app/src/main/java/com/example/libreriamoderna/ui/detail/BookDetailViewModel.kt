package com.example.libreriamoderna.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libreriamoderna.data.model.BookDetail
import com.example.libreriamoderna.data.repository.BookRepository
import com.example.libreriamoderna.util.UiState
import com.example.libreriamoderna.util.toErrorMessage
import kotlinx.coroutines.launch

/** ViewModel of the detail screen. Loads and holds the detail of one book. */
class BookDetailViewModel(
    private val repository: BookRepository = BookRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<BookDetail>>()
    val uiState: LiveData<UiState<BookDetail>> = _uiState

    fun loadBookDetail(workId: String) {
        // After a rotation the data is already here: don't call the API again
        if (_uiState.value is UiState.Success || _uiState.value is UiState.Loading) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getBookDetail(workId)
                .onSuccess { detail -> _uiState.value = UiState.Success(detail) }
                .onFailure { error -> _uiState.value = UiState.Error(error.toErrorMessage()) }
        }
    }
}