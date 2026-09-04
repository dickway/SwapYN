package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil

@JsonClass(generateAdapter = true)
@Parcelize
data class MovesBean(
    @Json(name = "img_url")
    val imgUrl: String = "",
    @Json(name = "stype")
    val stype: String = "",
    @Json(name = "video_url")
    val videoUrl: String = ""
) : Parcelable{
    companion object {


        var differCallback = object : DiffUtil.ItemCallback<MovesBean>() {
            override fun areItemsTheSame(
                oldItem: MovesBean,
                newItem: MovesBean
            ): Boolean {
                return oldItem.imgUrl == newItem.imgUrl
            }

            override fun areContentsTheSame(
                oldItem: MovesBean,
                newItem: MovesBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}