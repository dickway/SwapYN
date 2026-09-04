package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class FeedbackMsgWrapBean(
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "feed_id")
    val feedId: Int = 0,
    @Json(name = "state")
    var state: Int = 0,
    /**
     * 0 自己
     * 1 后台
     * 3 反馈时的内容
     */
    @Json(name = "type")
    val type: Int = 0,
    @Json(name = "content")
    val content: FeedbackMsgBean = FeedbackMsgBean(),
    @Json(name = "create_time")
    val createTime: Long,
) : Parcelable {
    companion object {

        val differCallback = object : DiffUtil.ItemCallback<FeedbackMsgWrapBean>() {
            override fun areItemsTheSame(
                oldItem: FeedbackMsgWrapBean,
                newItem: FeedbackMsgWrapBean
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FeedbackMsgWrapBean,
                newItem: FeedbackMsgWrapBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}