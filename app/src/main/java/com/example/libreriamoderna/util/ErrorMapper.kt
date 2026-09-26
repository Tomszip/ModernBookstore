package com.example.libreriamoderna.util

import androidx.annotation.StringRes
import com.example.libreriamoderna.R
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Maps a technical exception to a user friendly message resource.
 * Order matters: SocketTimeoutException is also an IOException.
 */
@StringRes
fun Throwable.toErrorMessage(): Int = when (this) {
    is HttpException -> R.string.error_http               // 4xx / 5xx response
    is SocketTimeoutException -> R.string.error_timeout   // server too slow
    is IOException -> R.string.error_no_connection        // no internet / DNS failure
    else -> R.string.error_unknown
}