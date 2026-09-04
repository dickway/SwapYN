package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


/**
 * @author 再战科技
 * @date 2023/10/12
 * @description
 */
@JsonClass(generateAdapter = true)
@Parcelize
data class UploadFileBean(
    @Json(name = "src")
    val src: String = ""
) : Parcelable
