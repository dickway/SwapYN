package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class FaceRectBean(
    @Json(name = "minneighbors")
    val minneighbors: Int = 10,
    @Json(name = "minwh")
    val minwh: Double = 30.0,
    @Json(name = "rectsize")
    val rectsize: Double = 1.2,// 扩展比例
    @Json(name = "scalefactor")
    val scalefactor: Double = 1.2,
    @Json(name = "mode")
    val mode: String = "haarcascade_frontalface_default"
) : Parcelable

