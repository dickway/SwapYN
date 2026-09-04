package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.zzkj.structure.util.ktx.toIntOrZero
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class CheckInBean(
    @Json(name = "day") var day: String = "",
    // 1普通，2VIP
    @Json(name = "from_type") var fromType: Int = 1,
    // 0正常，1补签
    @Json(name = "type") var type: Int? = null,
    // 本月第几天，从1开始，本地参数
    @Json(name = "dayOfMonth") var dayOfMonth: Int = 0
) : Parcelable {
    fun getYear() = try {
        day.substring(startIndex = 0, endIndex = 4).toIntOrZero()
    } catch (_: Throwable) {
        0
    }

    fun getMonth() = try {
        day.substring(startIndex = 3, endIndex = 5).toIntOrZero()
    } catch (_: Throwable) {
        0
    }

    companion object {

        val differCallback = object : DiffUtil.ItemCallback<CheckInBean>() {
            override fun areItemsTheSame(oldItem: CheckInBean, newItem: CheckInBean): Boolean {
                return oldItem.day == newItem.day
            }

            override fun areContentsTheSame(oldItem: CheckInBean, newItem: CheckInBean): Boolean {
                return oldItem == newItem
            }
        }

        fun merge(days: List<CheckInBean>?, logs: List<CheckInBean>?): List<CheckInBean> {
            if (days.isNullOrEmpty()) return mutableListOf()
            val newDays = days.map { bean ->
                // copy一下，不然不刷新
                bean.copy(type = logs?.find { bean.day == it.day && bean.fromType == it.fromType }?.type)
            }
            return newDays
        }
    }
}

