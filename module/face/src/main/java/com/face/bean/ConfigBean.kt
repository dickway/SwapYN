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
data class ConfigBean(
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "key")
    val key: String = "",
    @Json(name = "type")
    val type: String = "",
    @Json(name = "value")
    val value: String = ""
) : Parcelable {
    companion object {

        fun List<ConfigBean>.toMap(): Map<String, String?> {
            val temp = mutableMapOf<String, String>()
            forEach {
                temp[it.key] = it.value
            }
            return temp
        }
    }
}

