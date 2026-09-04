package com.face.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * @author 再战科技
 * @date 2022/4/8
 * @description
 */
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class InitConfigBean(
    // 4 -> b
    @Json(name = "s")
    val s: String = "",
    // 1 黑名单
    @Json(name = "b")
    val b: Int = 0,
    // 资源版本
    @Json(name = "e")
    val e: Int = 0,
    // 资源下载地址
    @Json(name = "mod")
    val mod: String? = null
) : Parcelable {
    fun isBlock() = b == 1

    fun isB() = s == "4" && !mod.isNullOrBlank() && !isBlock()
}