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
data class CollectBean(
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
    var itemType: Int = ITEM_TYPE_LIST

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var subList: MutableList<CollectBean>? = null


    companion object {

        const val ITEM_TYPE_TEXT = 1
        const val ITEM_TYPE_LIST = 2
        const val ITEM_TYPE_HLIST = 3

        val differCallback = object : DiffUtil.ItemCallback<CollectBean>() {
            override fun areItemsTheSame(
                oldItem: CollectBean,
                newItem: CollectBean
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CollectBean,
                newItem: CollectBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}


