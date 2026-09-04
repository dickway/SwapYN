package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class TypeTextBean(
    val id: Int = 0,

    val isHot: String = "new",

    var name: String = "Latest",

) : Parcelable {


    companion object {
        val differCallback = object : DiffUtil.ItemCallback<TypeTextBean>() {
            override fun areItemsTheSame(oldItem: TypeTextBean, newItem: TypeTextBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TypeTextBean, newItem: TypeTextBean): Boolean {
                return oldItem == newItem
            }
        }
    }
}

