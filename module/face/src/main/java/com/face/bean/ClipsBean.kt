package com.face.bean

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class ClipsBean(
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "mBitmap")
    val mBitmap: Bitmap?,
    @Json(name = "clipSize")
    var clipSize: Float = 0f,
) : Parcelable {


    companion object {
        val differCallback = object : DiffUtil.ItemCallback<ClipsBean>() {
            override fun areItemsTheSame(oldItem: ClipsBean, newItem: ClipsBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ClipsBean, newItem: ClipsBean): Boolean {
                return oldItem == newItem
            }
        }
    }
}

