package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class PointBean(
    @Json(name = "ads_num")
    val adsNum: Int = 0,
    @Json(name = "check_14")
    val check14: Int = 0,
    @Json(name = "check_3")
    val check3: Int = 0,
    @Json(name = "check_30")
    val check30: Int = 0,
    @Json(name = "check_7")
    val check7: Int = 0,
    @Json(name = "check_in")
    val checkIn: Int = 0,
    @Json(name = "points_ads")
    val pointsAds: Int = 0,
    @Json(name = "vip_30")
    val vip30: Int = 0,
    @Json(name = "vip_365")
    val vip365: Int = 0,
    @Json(name = "vip_7")
    val vip7: Int = 0
) : Parcelable

