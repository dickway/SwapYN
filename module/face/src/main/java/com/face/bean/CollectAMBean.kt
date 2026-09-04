package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Ignore
import com.face.R
import com.face.bean.AiFaceBean.Companion.ITEM_TYPE_FACE
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getStringX
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class CollectAMBean(
    @Json(name = "time_day")
    var timeDay: String? = "",
    @Json(name = "content")
    var contentBean: ContentBean = ContentBean(),
    @Json(name = "create_time")
    var createTime: Long = 0,
    @Json(name = "id")
    var id: String = "",
    @Json(name = "type")
    val type: String = "",
    @Json(name = "user_id")
    val userId: String = "",
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var itemType: Int = ITEM_TYPE_BEAN

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var subList: MutableList<CollectAMBean>? = null

    fun getTime() = createTime.let {
        if (it.toInt() ==0) return timeDay
        if (TimeUtil.isSameDay(it, System.currentTimeMillis())) {
            getStringX(R.string.today)
        } else if (TimeUtil.isYestoday(it, System.currentTimeMillis())) {
            getStringX(R.string.yesterday)
        } else {
            TimeUtil.getFormatDate(it, "MMMM dd,yyyy")
        }
    }

    companion object {
        const val ITEM_TYPE_TIME = 1
        const val ITEM_TYPE_BEAN = 2
        val differCallback = object : DiffUtil.ItemCallback<CollectAMBean>() {
            override fun areItemsTheSame(
                oldItem: CollectAMBean,
                newItem: CollectAMBean
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CollectAMBean,
                newItem: CollectAMBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}


@JsonClass(generateAdapter = true)
@Parcelize
data class ContentBean(
    @Json(name = "video_url")
    val videoUrl: String = "",
    @Json(name = "image_url")
    val imageUrl: String = "",
    @Json(name = "media_type")
    val mediaType: String = "",
    @Json(name = "webp_url")
    val webpUrl: String = "",
    @Json(name = "media_id")
    val mediaId: String = "",
    @Json(name = "user_id")
    val userId: String = "",
    @Json(name = "face_num")
    val faceNum: Int = 0,
    @Json(name = "height")
    val height: Int = 0,
    @Json(name = "width")
    val width: Int = 0,
    @Json(name = "pro")
    val pro: Int = 0,
    @Json(name = "title")
    val title: String = "",
    @Json(name = "index")
    val index: Int = 0,
    @Json(name = "image_water")
    val imageWater: String = "",
    @Json(name = "video_water")
    val videoWater: String = "",
    @Json(name = "task_id")
    val taskId: String = "",
    @Json(name = "task_content")
    val taskContent: String = "",

    ) : Parcelable

