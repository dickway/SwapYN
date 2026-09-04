package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Ignore
import kotlinx.parcelize.IgnoredOnParcel
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

@JsonClass(generateAdapter = true)
@Parcelize
data class AiFaceBean(
    @Json(name = "sub_num")
    var subNum: Int = 0,
    @Json(name = "exploreId")
    val exploreId: Int = 0,
    @Json(name = "name")
    val name: String = "",
//    @ColumnInfo(name = "title")
//    @Json(name = "title")
//    var title: String = "",
    @ColumnInfo(name = "more")
    @Json(name = "more")
    var more: String = "",
    @Json(name = "create_time")
    val createTime: Long = 0,
    @Json(name = "face_num")
    val faceNum: Int = 0,
    @Json(name = "height")
    var height: Int = 1,
    @Json(name = "id")
    val id: String = "",
    @Json(name = "image_url")
    val imageUrl: String = "",//https://img.meeti.ink/reface/COS/images/348d7985-8e70-404b-a681-9d03788d987d/i.jpg
    @Json(name = "md5")
    val md5: String = "",
    @Json(name = "media_type")
    var mediaType: String = "",
    @Json(name = "pro")
    val pro: Int = 0,
    @Json(name = "title")
    val title: String = "",
    @Json(name = "video_url")
    val videoUrl: String = "",
    @Json(name = "webp_url")
    val webpUrl: String = "",
    @Json(name = "weights")
    val weights: Int = 0,
    @Json(name = "width")
    var width: Int = 1,
    @Json(name = "media_id")
    var mediaId: String = "",
    @Json(name = "isSound")
    var isSound: Boolean = false,
    @Json(name = "banner_img")
    var bannerImg: String = "",
    @Json(name = "isShare")
    val isShare: Int = 0,
    @Json(name = "num")
    val num: Int = 0,
    @Json(name = "is_blur")
    var isBlur: Boolean = false,
    @Json(name = "isVip")
    var isVip: Boolean = false,
    @Json(name = "desc")
    val desc: String = "",
    @Json(name = "param")
    val param: String = "",
    @Json(name = "collect_id")
    var collectId: String = "0",
    @Json(name = "user_id")
    var userId: String = "",
    @Json(name = "score")
    var score: Float = 0.0f,
    @Json(name = "faceList")
    val faceList: List<Face>? = mutableListOf(),

    ) : Parcelable {

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

    fun getGroupId():String {
        val jsonObject = JSONObject(param)
        return jsonObject.optString("group_id")
    }

    fun getShareNum():String {
        return num.toString()
    }

    fun getIsShareState():Boolean {
        return isShare==1
    }

    fun notZeroWidth() = width.takeIf { it > 0 } ?: 1
    fun notZeroHeight() = height.takeIf { it > 0 } ?: 1

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var subList: MutableList<AiFaceBean>? = null

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var typeList1: List<VipBannerBean>? = null

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var typeList2: List<String>? = null

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var toolTypeList: List<ToolTypeBean>? = null

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var toolTypeList2: List<ToolTypeBean>? = null

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var itemType: Int = ITEM_TYPE_FACE

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var recommendBean: RecommendBean.RecommendX? = null

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var explreBean: AiFaceBean? = null

    companion object {

        const val ITEM_TYPE_BANNER = 1
        const val ITEM_TYPE_PLAY = 2
        const val ITEM_TYPE_LIKE = 3
        const val ITEM_TYPE_HOT = 4
        const val ITEM_TYPE_TYPE = 5
        const val ITEM_TYPE_FACE = 6
        const val ITEM_TYPE_B = 7
        const val ITEM_TYPE_TOOL = 8

        var differCallback = object : DiffUtil.ItemCallback<AiFaceBean>() {
            override fun areItemsTheSame(
                oldItem: AiFaceBean,
                newItem: AiFaceBean
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: AiFaceBean,
                newItem: AiFaceBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}



//@JsonClass(generateAdapter = true)
//@Parcelize
//data class Param(
//    @Json(name = "group_id")
//    val groupId: String = ""
//) : Parcelable