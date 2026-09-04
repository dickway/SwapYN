package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.JsonParser

@JsonClass(generateAdapter = true)
@Parcelize
data class Source(
    @Json(name = "source")
    val source: String = "",
    @Json(name = "target")
    val target: String = ""
) : Parcelable {

    fun parseSources(json: String): List<Source> {
        val result = mutableListOf<Source>()
        val element = JsonParser.parseString(json).asJsonObject

        val sourceElement = element["source"]

        when {
            sourceElement.isJsonArray -> {
                // 第一种情况：source 是数组
                sourceElement.asJsonArray.forEach {
                    val obj = it.asJsonObject
                    result.add(Source(obj["source"].asString, obj["target"].asString))
                }
            }

            sourceElement.isJsonObject -> {
                // 第二种情况：source 是单个对象
                val obj = sourceElement.asJsonObject
                result.add(Source(obj["source"].asString, obj["target"].asString))
            }
        }

        return result
    }
}