package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * @author 再战科技
 * @date 2023/10/10
 * @description
 */
@JsonClass(generateAdapter = true)
@Parcelize
data class SearchHotItemBean(
    @Json(name = "c")
    val id: Int = 0,
    @Json(name = "title")
    val title: String = ""
) : Parcelable {
    companion object {

        val differCallback = object : DiffUtil.ItemCallback<SearchHotItemBean>() {
            override fun areItemsTheSame(
                oldItem: SearchHotItemBean,
                newItem: SearchHotItemBean
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: SearchHotItemBean,
                newItem: SearchHotItemBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}