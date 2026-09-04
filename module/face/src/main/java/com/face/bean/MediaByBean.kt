package com.face.bean

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode


/**
 * @author 再战科技
 * @date 2023/10/11
 * @description
 */
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class MediaByBean(
    @Json(name = "collect_id")
    var collectId: String? = "",
    @Json(name = "faceList")
    val faceList: List<Face> = mutableListOf(),
    @Json(name = "face_num")
    val faceNum: Int = 0,
    @Json(name = "icon")
    val icon: String = "",
    @Json(name = "height")
    val height: Int = 0,
    @Json(name = "id")
    val id: String? = "",
    @Json(name = "image_url")
    val imageUrl: String = "",
    @Json(name = "media_type")
    val mediaType: String = "",
    @Json(name = "title")
    val title: String = "",
    @Json(name = "video_url")
    val videoUrl: String = "",
    @Json(name = "webp_url")
    val webpUrl: String = "",
    @Json(name = "weights")
    val weights: Int = 0,
    @Json(name = "user_id")
    val userId: String = "",
    @Json(name = "width")
    val width: Int = 1,
    @Json(name = "pro")
    val pro: Int = 0,
    @Json(name = "tag")
    val tag: String = "",
    @Json(name = "desc")
    val desc: String = "",
    @Json(name = "param")
    val param: String = "",
    @Json(name = "isShare")
    val isShare: Int = 0,
    @Json(name = "score")
    var score: Float = 0.0f,
//    @Json(name = "faceCacheList")
//    val faceCacheList: List<Face> = mutableListOf(),
) : Parcelable {
    fun notZeroWidth() = width.takeIf { it > 0 } ?: 1
    fun notZeroHeight() = height.takeIf { it > 0 } ?: 1

    fun getGroupId():String {
        val jsonObject = JSONObject(param)
        return jsonObject.optString("group_id")
    }

    fun getScoreStr():String {
        return  BigDecimal(score.toString())
            .setScale(1, RoundingMode.DOWN)
            .toString()
    }

    fun getScoreFloat():Float {
        return  BigDecimal(score.toString())
            .setScale(1, RoundingMode.DOWN)
            .toFloat()
    }

    fun getIcon1(): String= icon.let {
        icon.split(",").getOrNull(0)?:icon
    }

    fun getIsShareState():Boolean {
        return isShare==1
    }

    companion object {

        val differCallback = object : DiffUtil.ItemCallback<MediaByBean>() {
            override fun areItemsTheSame(oldItem: MediaByBean, newItem: MediaByBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MediaByBean, newItem: MediaByBean): Boolean {
                return oldItem == newItem
            }
        }
    }
}


@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class Face(
    @Json(name = "face_id")
    val faceId: String = "",
    @Json(name = "media_id")
    val mediaId: String = "",
    @Json(name = "url")
    val url: String = "",
    @Json(name = "isSelected")
    var isSelected: Boolean = false
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var isSelectBean: MyFaceImgBean? = null

    companion object {

        val differCallback = object : DiffUtil.ItemCallback<Face>() {
            override fun areItemsTheSame(oldItem: Face, newItem: Face): Boolean {
                return oldItem.faceId == newItem.faceId
            }

            override fun areContentsTheSame(oldItem: Face, newItem: Face): Boolean {
                return oldItem == newItem
            }
        }
    }
}

