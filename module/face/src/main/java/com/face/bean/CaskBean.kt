package com.face.bean
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
@Parcelize
data class CaskBean(
    @Json(name = "card_no")
    val cardNo: String = "",
    @Json(name = "card_paswd")
    val cardPaswd: String = "",
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "point")
    val point: Int = 0,
    @Json(name = "use_time")
    val useTime: Long = 0,
    @Json(name = "user_id")
    val userId: String = ""
) : Parcelable{
    companion object {

        var differCallback = object : DiffUtil.ItemCallback<CaskBean>() {
            override fun areItemsTheSame(
                oldItem: CaskBean,
                newItem: CaskBean
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CaskBean,
                newItem: CaskBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}



