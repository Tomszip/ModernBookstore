package com.example.libreriamoderna.data.model

/**
 * UI model for the detail screen. It is built by the repository
 * by combining the work endpoint and the search endpoint.
 */
data class BookDetail(
    val title: String,
    val coverUrl: String?,
    val numberOfPages: Int?,
    val publishers: List<String>,
    val subjects: List<String>,
    val description: String?,
    val openLibraryUrl: String
)