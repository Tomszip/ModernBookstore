package com.example.libreriamoderna.data.remote

import com.example.libreriamoderna.data.model.SearchResponse
import com.example.libreriamoderna.data.model.WorkDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit definition of the Open Library endpoints used by the app.
 * Base URL: https://openlibrary.org/
 */
interface OpenLibraryApi {

    /**
     * Searches books by title or author.
     * Example: search.json?q=tolkien&fields=...&limit=30
     */
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("fields") fields: String = SEARCH_FIELDS,
        @Query("limit") limit: Int = DEFAULT_LIMIT
    ): SearchResponse

    /**
     * Gets the extended info (description, subjects) of a single work.
     * Example: works/OL27482W.json
     */
    @GET("works/{workId}.json")
    suspend fun getWorkDetail(
        @Path("workId") workId: String
    ): WorkDetail

    companion object {
        // Only request the fields we actually show, to keep responses small
        const val SEARCH_FIELDS = "key,title,author_name,first_publish_year,cover_i"
        const val DETAIL_FIELDS = "key,number_of_pages_median,publisher"
        const val DEFAULT_LIMIT = 30
    }
}