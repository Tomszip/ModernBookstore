package com.example.libreriamoderna.data.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

/** Response of /works/{id}.json with the extended info of a book. */
data class WorkDetail(
    @SerializedName("key") val key: String? = null,
    @SerializedName("title") val title: String? = null,
    // The API returns description either as a plain string or as
    // an object {"type": "/type/text", "value": "..."}, so we keep it raw.
    @SerializedName("description") val description: JsonElement? = null,
    @SerializedName("subjects") val subjects: List<String>? = null,
    @SerializedName("covers") val covers: List<Int>? = null
) {

    /** Normalizes both description formats into a plain String. */
    val descriptionText: String?
        get() = when {
            description == null || description.isJsonNull -> null
            description.isJsonPrimitive -> description.asString
            description.isJsonObject -> description.asJsonObject.get("value")?.asString
            else -> null
        }
}