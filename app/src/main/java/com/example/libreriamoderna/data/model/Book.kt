package com.example.libreriamoderna.data.model

import com.google.gson.annotations.SerializedName

/**
    A single book (a "work") returned by the Open Library Search API.
    All fields are nullable because the API may omit any of them.
 */
data class Book(
    @SerializedName("key") val key: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("author_name") val authorNames: List<String>? = null,
    @SerializedName("first_publish_year") val firstPublishYear: Int? = null,
    @SerializedName("cover_i") val coverId: Int? = null,
    @SerializedName("number_of_pages_median") val numberOfPages: Int? = null,
    @SerializedName("publisher") val publishers: List<String>? = null
) {

    /** Work id without the "/works/" prefix, e.g. "OL27482W". */
    val workId: String?
        get() = key?.removePrefix("/works/")

    /** Authors joined in a single readable string. */
    val authorsText: String?
        get() = authorNames?.joinToString(", ")

    /** Medium size cover URL, or null if the book has no cover. */
    val coverUrl: String?
        get() = coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
}