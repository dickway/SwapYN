package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.apkfuns.logutils.LogUtils
import com.inmobi.media.r
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.moshi.MoshiHelper
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

@JsonClass(generateAdapter = true)
@Parcelize
data class PlayTaskBean(
    @Json(name = "content")
    val content: String = "",
    @Json(name = "create_time")
    val createTime: Long = 0,
    @Json(name = "estimated_time")
    val estimatedTime: Long = 0,
    @Json(name = "face_num")
    val faceNum: Int = 0,
    @Json(name = "height")
    val height: Int = 0,
    @Json(name = "icon")
    val icon: String = "",
    @Json(name = "id")
    val id: String = "",
    @Json(name = "image_url")
    val imageUrl: String = "",
    @Json(name = "isdelete")
    val isdelete: Int = 0,
    @Json(name = "isnew")
    val isnew: Int = 0,
    @Json(name = "media_id")
    val mediaId: String = "",
    @Json(name = "media_state")
    val mediaState: Int = 0,
    @Json(name = "media_type")
    val mediaType: String = "",
    @Json(name = "params")
    val params: String = "",
    @Json(name = "result")
    val result: String = "",
    @Json(name = "pro")
    val pro: Int = 0,
    @Json(name = "state")
    val state: Int = 0,
    @Json(name = "tag")
    val tag: String = "",
    @Json(name = "task_type")
    val taskType: String = "",
    @Json(name = "title")
    val title: String = "",
    @Json(name = "video_url")
    val videoUrl: String = "",
    @Json(name = "webp_url")
    val webpUrl: String = "",
    @Json(name = "width")
    val width: Int = 0
) : Parcelable {
    fun notZeroWidth() = width.takeIf { it > 0 } ?: 1
    fun notZeroHeight() = height.takeIf { it > 0 } ?: 1
    fun getProgress(): Int {
        if (System.currentTimeMillis() > estimatedTime) {
            return 99
        } else {
            val totalTime = estimatedTime - createTime
            val millisFinished = System.currentTimeMillis()-createTime
            return ((millisFinished.toFloat() / totalTime) * 100).toInt()
        }
    }

    fun getIcon1(): String= icon.let {
        icon.split(",").getOrNull(0)?:icon
    }

    fun getIcon2(): String= icon.let {
        icon.split(",").getOrNull(1)?:icon
    }


    fun getResultImgUrl(): String = result.let {
        try {
            val resultJson = JSONObject(result)
            resultJson.getString("data_webp")?:""
        } catch (e: Exception) {
            it
        }
    }

    fun getResultVoideUrl() = result.let {
        try {
            val dataBean = MoshiHelper.adapter(ResulCuttUrl::class.java).fromJson(it)
            dataBean?.data?:""
        } catch (e: Exception) {
            it
        }
    }

    fun getResultHead(): String? = content.let {
        try {
            val json = JSONObject(it)
            val sourceArray = json.getJSONArray("source")
            val sourceUrl = sourceArray
                .getJSONObject(0)
                .getString("source")
            sourceUrl
        } catch (e: Exception) {
            it
        }
    }

    fun getGroupId(): String?= params.let {
        val root = JSONObject(it)
        // media 对象
        val media = root.getJSONObject("media")
        // param 是一个 JSON 字符串
        val param = media.getString("param")
        // 再解析一次
        val groupId = JSONObject(param).getString("group_id")
        groupId
    }




    companion object {

        val differCallback = object : DiffUtil.ItemCallback<PlayTaskBean>() {
            override fun areItemsTheSame(
                oldItem: PlayTaskBean,
                newItem: PlayTaskBean
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PlayTaskBean,
                newItem: PlayTaskBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}