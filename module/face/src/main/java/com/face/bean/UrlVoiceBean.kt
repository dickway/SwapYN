package com.face.bean

import android.os.Parcelable

import kotlinx.parcelize.Parcelize

import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
@Parcelize
data class UrlVoiceBean(
    @Json(name = "ee")
    val ee: String = "",
    @Json(name = "id")
    val id: String = "",
    @Json(name = "md5")
    val md5: String = "",
    @Json(name = "result")
    val result: List<ResultUrl> = listOf(),
    @Json(name = "source")
    val source: String = "",
    @Json(name = "state")
    val state: Int = 0
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class ResultUrl(
    @Json(name = "duration")
    val duration: Int = 0,
    @Json(name = "lang")
    val lang: String = "",
    @Json(name = "url")
    val url: String = ""
) : Parcelable



@JsonClass(generateAdapter = true)
@Parcelize
data class UrlCutBean(
    @Json(name = "ee")
    val ee: String = "",
    @Json(name = "id")
    val id: String = "",
    @Json(name = "md5")
    val md5: String = "",
    @Json(name = "result")
    val result: ResulCuttUrl,
    @Json(name = "source")
    val source: String = "",
    @Json(name = "state")
    val state: Int = 0
) : Parcelable


@JsonClass(generateAdapter = true)
@Parcelize
data class ResulCuttUrl(
    @Json(name = "data")
    val data: String =  "",
    @Json(name = "data_water")
    val dataWater: String = ""
) : Parcelable


