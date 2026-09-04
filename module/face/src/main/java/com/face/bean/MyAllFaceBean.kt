package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class MyAllFaceBean(
    @Json(name = "name")
    var name: String? = "",
    @Json(name = "listFace")
    var listFace: List<MyFaceImgBean>? = mutableListOf()
) : Parcelable {

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<MyAllFaceBean>() {
            override fun areItemsTheSame(oldItem: MyAllFaceBean, newItem: MyAllFaceBean): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: MyAllFaceBean,
                newItem: MyAllFaceBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

