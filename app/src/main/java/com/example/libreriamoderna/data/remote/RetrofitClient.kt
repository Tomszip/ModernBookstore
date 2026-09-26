package com.example.libreriamoderna.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton that builds the Retrofit instance only once
 * and exposes the API implementation to the rest of the app.
 */
object RetrofitClient {

    private const val BASE_URL = "https://openlibrary.org/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Retrofit generates the implementation of the interface at runtime. */
    val api: OpenLibraryApi by lazy {
        retrofit.create(OpenLibraryApi::class.java)
    }
}