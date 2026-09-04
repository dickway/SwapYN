package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class SizeBean(
    @Json(name = "color")
    var color: String = "",
    @Json(name = "style")
    var style: String = "",
    @Json(name = "size")
    val size: String = "22 x 32 mm",
    @Json(name = "proportionW")
    val proportionW: Int = 0,
    @Json(name = "proportionH")
    val proportionH: Int = 0,
    @Json(name = "px_size")
    val pxSize: String = "",
    @Json(name = "img")
    val img: Int?
) : Parcelable {


    companion object {

        val differCallback = object : DiffUtil.ItemCallback<SizeBean>() {
            override fun areItemsTheSame(oldItem: SizeBean, newItem: SizeBean): Boolean {
                return oldItem.color == newItem.color
            }

            override fun areContentsTheSame(
                oldItem: SizeBean,
                newItem: SizeBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

