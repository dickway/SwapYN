package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class VipBannerBean(
    @Json(name = "src")
    val src: Int = 0,
    @Json(name = "logo")
    val logo: Int = 0,
    @Json(name = "name")
    val name: String? = "",
    @Json(name = "hint")
    val hint: String? = "",
) : Parcelable {
    companion object {
        val differCallback = object : DiffUtil.ItemCallback<VipBannerBean>() {
            override fun areItemsTheSame(oldItem: VipBannerBean, newItem: VipBannerBean): Boolean {
                return oldItem.src == newItem.src
            }

            override fun areContentsTheSame(
                oldItem: VipBannerBean,
                newItem: VipBannerBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

