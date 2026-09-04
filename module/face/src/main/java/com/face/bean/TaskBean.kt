package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Ignore
import com.face.R
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getStringX

import kotlinx.parcelize.Parcelize

import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json
import kotlinx.parcelize.IgnoredOnParcel


@JsonClass(generateAdapter = true)
@Parcelize
data class TaskBean(
    @Json(name = "end_time")
    val endTime: Long = 0,
    @Json(name = "isnew")
    val isnew: Int = 0,
    @Json(name = "ee")
    val ee: String = "",
    @Json(name = "id")
    var id: String = "",
    @Json(name = "task_group")
    val taskGroup: String = "",
    @Json(name = "faceUrl")
    val faceUrl: String = "",
    @Json(name = "refund")
    val refund: Int = 0,
    @Json(name = "create_time")
    var createTime: Long = 0,
    @Json(name = "estimated_time")
    var estimatedTime: Long = 0,
    @Json(name = "media")
    val media: AiFaceBean = AiFaceBean(),
    @Json(name = "state")
    val state: Int = 0,//0新建，1生成中，2完成,3失败，4个别失败
    @Json(name = "task_type")
    val taskType: String = "",
    @Json(name = "name")
    var name: String = "",
    @Json(name = "content")
    var content: String = "",
    @Json(name = "result")
    val result: Result = Result(),
    @Json(name = "params")
    val params: Params = Params(),
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var itemType: Int = ITEM_TYPE_LIST

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var subList: MutableList<TaskBean>? = null

    fun getIsTaskShareState():Boolean {
        return params.isShare==1
    }

    fun getTime2() = createTime.let {
        TimeUtil.getFormatDate(it, "MM/dd/yyyy")
    }

    fun getTime() = createTime.let {
        if (TimeUtil.isSameDay(it, System.currentTimeMillis())) {
            getStringX(R.string.today)
        } else if (TimeUtil.isYestoday(it, System.currentTimeMillis())) {
            getStringX(R.string.yesterday)
        } else {
            TimeUtil.getFormatDate(it, "MMMM dd,yyyy")
        }
    }

    companion object {

        const val ITEM_TYPE_TEXT = 1
        const val ITEM_TYPE_LIST = 2
        const val ITEM_TYPE_HLIST = 3

        val differCallback = object : DiffUtil.ItemCallback<TaskBean>() {
            override fun areItemsTheSame(
                oldItem: TaskBean,
                newItem: TaskBean
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TaskBean,
                newItem: TaskBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

@JsonClass(generateAdapter = true)
@Parcelize
data class Result(
    @Json(name = "data")
    var mData: List<String> = listOf(""),
    @Json(name = "data_water")
    var dataWater: List<String> = listOf(""),
    @Json(name = "data_webp")
    var dataWebp: String = "",
    @Json(name = "face_url")
    var faceUrl: List<String> = listOf(""),
    @Json(name = "lisst")
    val listUrl: List<ResultUrl> = mutableListOf(),
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Params(
    @Json(name = "zone")
    val zone: String = "",
    @Json(name = "record_id")
    val recordid: String = "",
    @Json(name = "rating")
    val rating: Float = 0f,
    @Json(name = "isShare")
    val isShare: Int = 0,

) : Parcelable
