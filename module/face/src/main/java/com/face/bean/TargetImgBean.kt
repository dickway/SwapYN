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
data class TargetImgBean(
    @Json(name = "name")
    var name: String? = "",
    @Json(name = "mBitmap")
    val mBitmap: Bitmap? = null
) : Parcelable {
    companion object {

        val differCallback = object : DiffUtil.ItemCallback<TargetImgBean>() {
            override fun areItemsTheSame(oldItem: TargetImgBean, newItem: TargetImgBean): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: TargetImgBean,
                newItem: TargetImgBean
            ): Boolean {
                return false
            }
        }
    }
}