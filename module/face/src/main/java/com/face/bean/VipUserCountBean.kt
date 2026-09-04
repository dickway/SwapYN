package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class VipUserCountBean(
    val id: Int = 0,
    var countUse: Int = 0,

    ) : Parcelable {
    companion object {
        fun List<VipUserCountBean>.toMap(): Map<Int, Int?> {
            val temp = mutableMapOf<Int, Int>()
            forEach {
                temp[it.id] = it.countUse
            }
            return temp
        }
    }
}

