package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class ALinkBean(
    @Json(name = "content_type")
    val contentType: String = "",
    @Json(name = "object_key")
    val objectKey: String = "",
    @Json(name = "parts")
    var parts: List<Part> = listOf(),
    @Json(name = "upload_id")
    val uploadId: String = "",
    @Json(name = "url")
    val url: String = ""
) : Parcelable