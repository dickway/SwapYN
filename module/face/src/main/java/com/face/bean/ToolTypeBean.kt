package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.zzkj.structure.util.SPbaseUtils
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class ToolTypeBean(
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "src2")
    val src2: String = "",
    @Json(name = "is_new")
    val isNew: Boolean = false,
    @Json(name = "is_debug")
    val isDebug: Boolean = false,
    @Json(name = "show_version")
    val showVersion: String = "",
    @Json(name = "putin")
    val putIn: String = "",
    @Json(name = "url_img")
    val urlImg: String = "",
    @Json(name = "url_icon")
    val urlIcon: String = "",
    @Json(name = "name_de")
    val nameDe: String = "",
    @Json(name = "name_en")
    val nameEn: String = "",
    @Json(name = "name_es")
    val nameEs: String = "",
    @Json(name = "name_fr")
    val nameFr: String = "",
    @Json(name = "name_sv")
    val nameSv: String = "",
    @Json(name = "name_zh")
    val nameZh: String = "",
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
    @Json(name = "name_pt_br")
    val namePtBR: String = "",
    @Json(name = "name_pt_pt")
    val namePt: String = "",
    @Json(name = "name_zh_tw")
    val nameZhTW: String = "",
    @Json(name = "name_it")
    val nameIt: String = "",

    ) : Parcelable {

    fun getName(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> nameZh
            "es" -> nameEs
            "de" -> nameDe
            "fr" -> nameFr
            "sv" -> nameSv
            "ar" -> nameAr
            "ja" -> nameJa
            "ko" -> nameKo
            "ku" -> nameKu
            "fa" -> nameFa
            "iw" -> nameIw
            "tr" -> nameTr
            "zh-tw" -> nameZhTW
            "pt-br" -> namePtBR
            "pt-pt" -> namePt
            "it" -> nameIt
            else -> nameEn
        }
    }

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<ToolTypeBean>() {
            override fun areItemsTheSame(oldItem: ToolTypeBean, newItem: ToolTypeBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ToolTypeBean,
                newItem: ToolTypeBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

