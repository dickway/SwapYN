package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class RatingBean(
    @Json(name = "day_rating")
    val dayRating: Int = 0,
    @Json(name = "rewards")
    val rewards: List<Reward> = listOf(),
    @Json(name = "week_rating")
    val weekRating: Int = 0
) : Parcelable