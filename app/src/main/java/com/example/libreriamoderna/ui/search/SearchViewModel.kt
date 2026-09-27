package com.example.libreriamoderna.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libreriamoderna.R
import com.example.libreriamoderna.data.model.Book
import com.example.libreriamoderna.data.repository.BookRepository
import com.example.libreriamoderna.util.UiState
import com.example.libreriamoderna.util.toErrorMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * ViewModel of the search screen.
 * Holds the UI state and survives configuration changes (e.g. rotation).
 */
class SearchViewModel(
    private val repository: BookRepository = BookRepository()
) : ViewModel() {

    // Mutable only inside the ViewModel; the View gets a read-only LiveData
    private val _uiState = MutableLiveData<UiState<List<Book>>>()
    val uiState: LiveData<UiState<List<Book>>> = _uiState

    private var searchJob: Job? = null

    fun searchBooks(query: String) {
        val cleanQuery = query.trim()
        if (cleanQuery.isEmpty()) return

        searchJob?.cancel() // a new search replaces the previous one
        searchJob = viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.searchBooks(cleanQuery)
                .onSuccess { books ->
                    _uiState.value = if (books.isEmpty()) {
                        UiState.Error(R.string.search_no_results)
                    } else {
                        UiState.Success(books)
                    }
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.toErrorMessage())
                }
        }
    }
}