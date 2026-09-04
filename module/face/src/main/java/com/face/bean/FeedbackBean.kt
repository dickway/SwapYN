package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class FeedbackBean(
    @Json(name = "id")
    val id: Int = 0,
    /**
     * 1 有新消息
     * 2 完成
     */
    @Json(name = "state")
    val state: Int = 0,
    @Json(name = "type")
    val type: Int = 0,
    @Json(name = "content")
    val content: FeedbackMsgBean = FeedbackMsgBean()
) : Parcelable {
    companion object {

        val differCallback = object : DiffUtil.ItemCallback<FeedbackBean>() {
            override fun areItemsTheSame(oldItem: FeedbackBean, newItem: FeedbackBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FeedbackBean, newItem: FeedbackBean): Boolean {
                return oldItem == newItem
            }
        }
    }
}