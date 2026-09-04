package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.zzkj.structure.util.ktx.toIntOrZero
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class PointInfoBean(
    @Json(name = "day")
    val day: Int = 0,
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "info")
    val info: List<InfoX> = listOf(),
    @Json(name = "isGet")
    val isGet: Int = 0
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class InfoX(
    @Json(name = "num")
    val num: Int = 0,
    @Json(name = "type")
    val type: Int = 0
) : Parcelable