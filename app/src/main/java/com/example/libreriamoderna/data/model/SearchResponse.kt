package com.example.libreriamoderna.data.model

import com.google.gson.annotations.SerializedName

/** Root object returned by /search.json */
data class SearchResponse(
    @SerializedName("numFound") val numFound: Int = 0,
    @SerializedName("docs") val docs: List<Book> = emptyList()
)