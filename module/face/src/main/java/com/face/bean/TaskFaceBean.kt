package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class TaskFaceBean(
    @Json(name = "flag")
    val flag: Int = 0,
    @Json(name = "generate")
    val generate: Int = 0,
    @Json(name = "source")
    val source: List<Source> = listOf(),
    @Json(name = "target")
    val target: String = "",
    @Json(name = "watermarker")
    val watermarker: String = ""
) : Parcelable