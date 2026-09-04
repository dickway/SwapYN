package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * @author 再战科技
 * @date 2023/10/15
 * @description
 */
@JsonClass(generateAdapter = true)
@Parcelize
data class TopSearchBean(
    @Json(name = "keyTop")
    val keyTop: MutableList<SearchHotItemBean> = mutableListOf(),
) : Parcelable