package com.face.bean

import android.content.Context
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.face.R
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getStringX
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class Explore2Bean(
    @Json(name = "date")
    val date: String,
    @Json(name = "list")
    var list: List<AiFaceBean>? = null
) : Parcelable {

    fun getTime(context:Context) = date.let {
        val today= TimeUtil.getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd") ?: ""
        val yesterday= TimeUtil.getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd") ?: ""
        if (it==today) {
            context.resources.getString(R.string.today)
        } else if (yesterday==it) {
            context.resources.getString(R.string.yesterday)
        } else {
            date
        }
    }

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<Explore2Bean>() {
            override fun areItemsTheSame(oldItem: Explore2Bean, newItem: Explore2Bean): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: Explore2Bean, newItem: Explore2Bean): Boolean {
                return oldItem == newItem
            }
        }
    }
}

