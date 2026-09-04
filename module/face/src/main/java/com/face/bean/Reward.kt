package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Reward(
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "info")
    val info: String = "",
    @Json(name = "isReward")
    val isReward: Int = 0,
    @Json(name = "num")
    val num: Int = 0,
    @Json(name = "type")
    val type: String = ""
) : Parcelable