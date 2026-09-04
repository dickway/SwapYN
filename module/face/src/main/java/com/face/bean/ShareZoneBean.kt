package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ShareZoneBean(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "create_time")
    val createTime: Long = 0L,
    @Json(name = "user_id")
    val userId: String="",
    @Json(name = "num")
    var num: Int = 1,
    @Json(name = "state")
    var state: Int = 1,
    @Json(name = "type")
    var type: String = "",
) : Parcelable {
    companion object {

        val differCallback = object : DiffUtil.ItemCallback<ShareZoneBean>() {
            override fun areItemsTheSame(oldItem: ShareZoneBean, newItem: ShareZoneBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ShareZoneBean, newItem: ShareZoneBean): Boolean {
                return oldItem == newItem
            }
        }
    }
}