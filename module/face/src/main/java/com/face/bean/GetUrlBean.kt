package com.face.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


/**
 * @author 再战科技
 * @date 2022/3/28
 * @description
 */
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class GetUrlBean(
    @Json(name = "upload_url")
    val uploadUrl: String = "",
    @Json(name = "url")
    val url: String = "",
    @Json(name = "content_type")
    val contentType: String = ""

) : Parcelable

