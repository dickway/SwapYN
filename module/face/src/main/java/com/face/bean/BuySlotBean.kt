package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class BuySlotBean(
    @Json(name = "num")
    val num: Int = 0,
    @Json(name = "point")
    val point: Int = 0,
    @Json(name = "old_point")
    val oldPoint: Int = 0,
    @Json(name = "image_num")
    val imageNum: Int = 0,
    @Json(name = "video_180_num")
    val video180Num: Int = 0,
    @Json(name = "video_20_num")
    val video20Num: Int = 0,
    @Json(name = "video_300_num")
    val video300Num: Int = 0
) : Parcelable