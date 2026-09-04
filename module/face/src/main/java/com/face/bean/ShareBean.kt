package com.face.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


/**
 * @author 再战科技
 * @date 2023/10/11
 * @description
 */
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class ShareBean(
    @Json(name = "add_day")
    val addDay: Int = 0,
    @Json(name = "days")
    val days: Int = 0,
    @Json(name = "num")
    val num: String = "0",
    @Json(name = "url")
    val url: String = ""
) : Parcelable
