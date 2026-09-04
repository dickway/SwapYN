package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class UsePointBean(
    @Json(name = "points_download")
    val pointsDownload: Int = 3,
) : Parcelable

