package com.face.bean

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class UseTypeBean(
    @Json(name = "task_id")
    val task_id: String = "",
    @Json(name = "type")
    var type: String = "",
    @Json(name = "download_url")
    var download_url: String = "",
) : Parcelable

