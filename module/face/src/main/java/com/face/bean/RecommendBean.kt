package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class RecommendBean(
    @Json(name = "recommend")
    var recommend: RecommendX,

    @Json(name = "list")
    var listAiFace: List<AiFaceBean>? = null

) : Parcelable {
    companion object {

        val differCallback = object : DiffUtil.ItemCallback<RecommendBean>() {
            override fun areItemsTheSame(oldItem: RecommendBean, newItem: RecommendBean): Boolean {
                return oldItem.recommend == newItem.recommend
            }

            override fun areContentsTheSame(
                oldItem: RecommendBean,
                newItem: RecommendBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


    @JsonClass(generateAdapter = true)
    @Parcelize
    data class RecommendX(
        @Json(name = "type")
        var type: String="Topic",
        @Json(name = "name")
        var name: String="",
        @Json(name = "open_type")
        var open_type: Int=0,
        @Json(name = "open_value")
        var open_value: String="0",
        @Json(name = "show_type")
        var show_type: Int=0
    ) : Parcelable
}