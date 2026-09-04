package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class CompleteBean(
    @Json(name = "etag")
    val etag: String = "",
    @Json(name = "location")
    val location: String = "",
    @Json(name = "url")
    val urlName: String = ""
) : Parcelable