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
data class MyFaceImgBean(
    @Json(name = "create_time")
    val createTime: Long = 0,
    @Json(name = "id")
    val id: String = "",
    @Json(name = "pic")
    val pic: String = "",
    @Json(name = "sort")
    var sort: Int = 0,
    @Json(name = "type")
    val type: String = "",
    @Json(name = "user_id")
    val userId: String = "",
    @Json(name = "isSelect")
    var isSelect: Boolean = false,
) : Parcelable {


    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var itemType: Int = ITEM_TYPE_IMG

    companion object {

        const val ITEM_TYPE_ADD = 1
        const val ITEM_TYPE_IMG = 2

        val differCallback = object : DiffUtil.ItemCallback<MyFaceImgBean>() {
            override fun areItemsTheSame(oldItem: MyFaceImgBean, newItem: MyFaceImgBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MyFaceImgBean,
                newItem: MyFaceImgBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

