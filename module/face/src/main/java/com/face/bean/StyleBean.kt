package com.face.bean

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


/**
 * @author 再战科技
 * @date 2023/10/11
 * @description
 */
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class StyleBean(
    @Json(name = "name_en")
    val nameEN: String = "",
    @Json(name = "name_es")
    val nameES: String = "",
    @Json(name = "name_cn")
    val nameCN: String = "",
    @Json(name = "name_de")
    val nameDE: String = "",
    @Json(name = "name_fr")
    val nameFR: String = "",
    @Json(name = "name_sv")
    val nameSV: String = "",
    @Json(name = "name_ar")
    val nameAr: String = "",
    @Json(name = "name_ja")
    val nameJa: String = "",
    @Json(name = "name_ko")
    val nameKo: String = "",
    @Json(name = "name_fa")
    val nameFa: String = "",
    @Json(name = "name_iw")
    val nameIw: String = "",
    @Json(name = "name_ku")
    val nameKu: String = "",
    @Json(name = "name_tr")
    val nameTr: String = "",
    @Json(name = "name_zh_tw")
    val nameZhTW: String = "",
    @Json(name = "name_pt_br")
    val namePtBR: String = "",
    @Json(name = "name_pt_pt")
    val namePt: String = "",
    @Json(name = "name_it")
    val nameIt: String = "",
    @Json(name = "key")
    val key: String = "",
    @Json(name = "img_url")
    val url: String = "",
    @Json(name = "video_url")
    var video: String = ""
) : Parcelable {


    companion object {


        val differCallback = object : DiffUtil.ItemCallback<StyleBean>() {
            override fun areItemsTheSame(
                oldItem: StyleBean,
                newItem: StyleBean
            ): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(
                oldItem: StyleBean,
                newItem: StyleBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
