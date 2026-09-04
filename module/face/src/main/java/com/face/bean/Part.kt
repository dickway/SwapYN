package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Part(
    @Json(name = "part_number")
    val partNumber: Int = 0,
    @Json(name = "url")
    val url: String = "",
    @Json(name = "etag")
    val etag: String = ""

) : Parcelable