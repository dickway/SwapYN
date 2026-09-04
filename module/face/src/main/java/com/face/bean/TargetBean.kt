package com.face.bean

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * @author 再战科技
 * @date 2023/10/15
 * @description
 */
@JsonClass(generateAdapter = true)
@Parcelize
data class TargetBean(
    @Json(name = "path")
    var path: String? = "",
    @Json(name = "isSelect")
    var isSelect: Boolean = false,
    @Json(name = "isAdd")
    var isAdd: Boolean = false
) : Parcelable {
    companion object {

        val differCallback = object : DiffUtil.ItemCallback<TargetBean>() {
            override fun areItemsTheSame(oldItem: TargetBean, newItem: TargetBean): Boolean {
                return oldItem.path == newItem.path
            }

            override fun areContentsTheSame(
                oldItem: TargetBean,
                newItem: TargetBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}