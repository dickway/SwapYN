package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class AdsCountBean(
    val id: Int = 0,
    var count: Int = 0,
    var time: String = "",
) : Parcelable {
    companion object {
//        fun List<AdsCountBean>.toMap(): Map<Int, String?> {
//            val temp = mutableMapOf<Int, String>()
//            forEach {
//                temp[it.id] = it.time
//            }
//            return temp
//        }
    }
}

