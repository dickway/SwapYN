package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class RateInfoBean(
    @Json(name = "daily1")
    val daily1: Int = 0,
    @Json(name = "daily2")
    val daily2: Int = 0,
    @Json(name = "daily3")
    val daily3: Int = 0,
    @Json(name = "weekly1")
    val weekly1: Int = 0,
    @Json(name = "weekly2")
    val weekly2: Int = 0,
    @Json(name = "weekly3")
    val weekly3: Int = 0,

    @Json(name = "points_daily1")
    val pointsDaily1: Int = 0,
    @Json(name = "points_daily2")
    val pointsDaily2: Int = 0,
    @Json(name = "points_daily3")
    val pointsDaily3: Int = 0,
    @Json(name = "points_weekly1")
    val pointsWeekly1: Int = 0,
    @Json(name = "points_weekly2")
    val pointsWeekly2: Int = 0,
    @Json(name = "points_weekly3")
    val pointsWeekly3: Int = 0,
    @Json(name = "rate_daily_num1")
    val rateDailyNum1: Int = 0,
    @Json(name = "rate_daily_num2")
    val rateDailyNum2: Int = 0,
    @Json(name = "rate_daily_num3")
    val rateDailyNum3: Int = 0,
    @Json(name = "rate_weekly_num1")
    val rateWeeklyNum1: Int = 0,
    @Json(name = "rate_weekly_num2")
    val rateWeeklyNum2: Int = 0,
    @Json(name = "rate_weekly_num3")
    val rateWeeklyNum3: Int = 0
) : Parcelable