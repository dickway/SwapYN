package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class ExploreTextBean(
    @Json(name = "id")
    val id: Int = 0,

    @Json(name = "name")
    var name: String = "",

    @Json(name = "isSelected")
    var isSelected: Boolean = false,
) : Parcelable {


    companion object {
        val differCallback = object : DiffUtil.ItemCallback<ExploreTextBean>() {
            override fun areItemsTheSame(oldItem: ExploreTextBean, newItem: ExploreTextBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ExploreTextBean, newItem: ExploreTextBean): Boolean {
                return oldItem == newItem
            }
        }
    }
}

