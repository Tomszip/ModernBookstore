package com.example.libreriamoderna.util

import androidx.annotation.StringRes

/**
 * Represents every possible state of a screen that loads remote data.
 * The View observes this state and renders accordingly.
 */
sealed class UiState<out T> {

    /** Request in progress: the View shows a ProgressBar. */
    data object Loading : UiState<Nothing>()

    /** Request finished OK: the View shows the data. */
    data class Success<out T>(val data: T) : UiState<T>()

    /** Request failed: the View shows the error message. */
    data class Error(@StringRes val messageRes: Int) : UiState<Nothing>()
}