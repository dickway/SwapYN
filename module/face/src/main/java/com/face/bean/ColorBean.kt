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
data class ColorBean(
    @Json(name = "color")
    var color: String = "",
    @Json(name = "name")
    val name: String = "",
) : Parcelable {

    companion object {

        val differCallback = object : DiffUtil.ItemCallback<ColorBean>() {
            override fun areItemsTheSame(oldItem: ColorBean, newItem: ColorBean): Boolean {
                return oldItem.color == newItem.color
            }

            override fun areContentsTheSame(
                oldItem: ColorBean,
                newItem: ColorBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

