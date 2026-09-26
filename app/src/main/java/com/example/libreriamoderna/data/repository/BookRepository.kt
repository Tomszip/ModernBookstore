package com.example.libreriamoderna.data.repository

import com.example.libreriamoderna.data.model.Book
import com.example.libreriamoderna.data.model.BookDetail
import com.example.libreriamoderna.data.remote.OpenLibraryApi
import com.example.libreriamoderna.data.remote.RetrofitClient
import kotlin.coroutines.cancellation.CancellationException

/**
 * Single source of truth for book data.
 * ViewModels talk to this class and never to Retrofit directly.
 *
 * @param api injected through the constructor (manual Dependency Injection),
 * which makes it easy to replace with a fake implementation in tests.
 */
class BookRepository(
    private val api: OpenLibraryApi = RetrofitClient.api
) {

    /** Searches books by title or author. */
    suspend fun searchBooks(query: String): Result<List<Book>> = safeApiCall {
        api.searchBooks(query).docs
            .filter { it.workId != null } // without an id we can't open the detail
    }

    /**
     * Builds the full detail of a book by combining two requests:
     * 1. works/{id}.json -> description, subjects and covers
     * 2. search.json?q=key:/works/{id} -> number of pages and publishers
     */
    suspend fun getBookDetail(workId: String): Result<BookDetail> = safeApiCall {
        val work = api.getWorkDetail(workId)
        val extraInfo = api.searchBooks(
            query = "key:/works/$workId",
            fields = OpenLibraryApi.DETAIL_FIELDS,
            limit = 1
        ).docs.firstOrNull()

        BookDetail(
            title = work.title.orEmpty(),
            coverUrl = work.covers
                ?.firstOrNull { it > 0 } // the API sometimes returns -1 as a cover id
                ?.let { "$COVERS_BASE_URL$it-L.jpg" },
            numberOfPages = extraInfo?.numberOfPages,
            publishers = extraInfo?.publishers.orEmpty().take(MAX_PUBLISHERS),
            subjects = work.subjects.orEmpty().take(MAX_SUBJECTS),
            description = work.descriptionText,
            openLibraryUrl = "$OPEN_LIBRARY_WORK_URL$workId"
        )
    }

    /**
     * Executes a network call and wraps the outcome in a Result,
     * so exceptions never reach the ViewModel uncaught.
     */
    private suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> =
        try {
            Result.success(call())
        } catch (e: CancellationException) {
            throw e // coroutine was cancelled (user left the screen): don't treat as error
        } catch (e: Exception) {
            Result.failure(e)
        }

    private companion object {
        const val COVERS_BASE_URL = "https://covers.openlibrary.org/b/id/"
        const val OPEN_LIBRARY_WORK_URL = "https://openlibrary.org/works/"
        const val MAX_PUBLISHERS = 10
        const val MAX_SUBJECTS = 15
    }
}