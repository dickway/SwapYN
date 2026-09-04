package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class TagConfigBean(
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "key")
    val key: String = "",
    @Json(name = "package_name")
    val packageName: String = "",
    @Json(name = "remark")
    val remark: String = "",
    @Json(name = "type")
    val type: String = "",
    @Json(name = "value")
    val value: String = ""
) : Parcelable {
    companion object {
        fun List<TagConfigBean>.toMap(): Map<String, String?> {
            val temp = mutableMapOf<String, String>()
            forEach {
                temp[it.key] = it.value
            }
            return temp
        }
    }
}
