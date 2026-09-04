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
data class SwapBean(
    @Json(name = "id")
    val id: Int = 0,

    @Json(name = "name")
    var name: String = "",

    @Json(name = "isSelected")
    var isSelected: Boolean = false,
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var isSelectBean: CollectBean? = null

    companion object {


        val differCallback = object : DiffUtil.ItemCallback<SwapBean>() {
            override fun areItemsTheSame(
                oldItem: SwapBean,
                newItem: SwapBean
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SwapBean,
                newItem: SwapBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
