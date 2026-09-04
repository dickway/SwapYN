package com.face.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class VideoLong2Bean(
    @Json(name = "video_type")
    var video_type: String = "180",
    @Json(name = "sub")
    var sub: String = "1",
) : Parcelable
