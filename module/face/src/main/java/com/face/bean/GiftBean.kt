package com.face.bean

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.face.util.SPUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.moshi.MoshiHelper
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class GiftBean(
    @Json(name = "id")
    val id: Int = 0,
    @Json(name = "key")
    val key: String = "",
    @Json(name = "remark")
    val remark: String = "",
    @Json(name = "type")
    val type: String = "",
    @Json(name = "value")
    val value: String = ""
) : Parcelable {

    var infoBean: GiftInfoBean =
        MoshiHelper.adapter(GiftInfoBean::class.java).fromJson(remark) ?: GiftInfoBean()

    fun getName(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> infoBean.guideZh.split("|").getOrNull(0) ?: ""
            "es" -> infoBean.guideEs.split("|").getOrNull(0) ?: ""
            "de" -> infoBean.guideDe.split("|").getOrNull(0) ?: ""
            "fr" -> infoBean.guideFr.split("|").getOrNull(0) ?: ""
            "sv" -> infoBean.guideSv.split("|").getOrNull(0) ?: ""
            "ar" -> infoBean.guideAr.split("|").getOrNull(0) ?: ""
            "ko" -> infoBean.guideKo.split("|").getOrNull(0) ?: ""
            "ja" -> infoBean.guideJa.split("|").getOrNull(0) ?: ""
            "ku" -> infoBean.guideKu.split("|").getOrNull(0) ?: ""
            "fa" -> infoBean.guideFa.split("|").getOrNull(0) ?: ""
            "iw" -> infoBean.guideIw.split("|").getOrNull(0) ?: ""
            "tr" -> infoBean.guideTr.split("|").getOrNull(0) ?: ""
            "zh-tw" -> infoBean.guideZhTW.split("|").getOrNull(0) ?: ""
            "pt-br" -> infoBean.guidePtBR.split("|").getOrNull(0) ?: ""
            "pt-pt" -> infoBean.guidePt.split("|").getOrNull(0) ?: ""
            "it" -> infoBean.guideIt.split("|").getOrNull(0) ?: ""
            else -> infoBean.guideEn.split("|").getOrNull(0) ?: ""
        }
    }

    fun getDescribe(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> infoBean.guideZh.split("|").getOrNull(1) ?: ""
            "es" -> infoBean.guideEs.split("|").getOrNull(1) ?: ""
            "de" -> infoBean.guideDe.split("|").getOrNull(1) ?: ""
            "fr" -> infoBean.guideFr.split("|").getOrNull(1) ?: ""
            "sv" -> infoBean.guideSv.split("|").getOrNull(1) ?: ""
            "ar" -> infoBean.guideAr.split("|").getOrNull(1) ?: ""
            "ko" -> infoBean.guideKo.split("|").getOrNull(1) ?: ""
            "ja" -> infoBean.guideJa.split("|").getOrNull(1) ?: ""
            "ku" -> infoBean.guideKu.split("|").getOrNull(1) ?: ""
            "fa" -> infoBean.guideFa.split("|").getOrNull(1) ?: ""
            "iw" -> infoBean.guideIw.split("|").getOrNull(1) ?: ""
            "tr" -> infoBean.guideTr.split("|").getOrNull(1) ?: ""
            "zh-tw" -> infoBean.guideZhTW.split("|").getOrNull(1) ?: ""
            "pt-br" -> infoBean.guidePtBR.split("|").getOrNull(1) ?: ""
            "pt-pt" -> infoBean.guidePt.split("|").getOrNull(1) ?: ""
            "it" -> infoBean.guideIt.split("|").getOrNull(1) ?: ""
            else -> infoBean.guideEn.split("|").getOrNull(1) ?: ""
        }
    }


    companion object {
        val differCallback = object : DiffUtil.ItemCallback<GiftBean>() {
            override fun areItemsTheSame(oldItem: GiftBean, newItem: GiftBean): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(
                oldItem: GiftBean,
                newItem: GiftBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

@JsonClass(generateAdapter = true)
@Parcelize
data class GiftInfoBean(
    @Json(name = "key")
    val key: String = "",
    @Json(name = "aipoints")
    val aipoints: Int = 0,
    @Json(name = "guide_de")
    val guideDe: String = "",
    @Json(name = "guide_en")
    val guideEn: String = "",
    @Json(name = "guide_es")
    val guideEs: String = "",
    @Json(name = "guide_fr")
    val guideFr: String = "",
    @Json(name = "guide_sv")
    val guideSv: String = "",
    @Json(name = "guide_zh")
    val guideZh: String = "",
    @Json(name = "guide_zh_tw")
    val guideZhTW: String = "",
    @Json(name = "guide_ar")
    val guideAr: String = "",
    @Json(name = "guide_ko")
    val guideKo: String = "",
    @Json(name = "guide_ja")
    val guideJa: String = "",
    @Json(name = "guide_fa")
    val guideFa: String = "",
    @Json(name = "guide_iw")
    val guideIw: String = "",
    @Json(name = "guide_ku")
    val guideKu: String = "",
    @Json(name = "guide_tr")
    val guideTr: String = "",
    @Json(name = "guide_pt_br")
    val guidePtBR: String = "",
    @Json(name = "guide_pt_pt")
    val guidePt: String = "",
    @Json(name = "guide_it")
    val guideIt: String = "",

    @Json(name = "img")
    val img: String = ""
) : Parcelable {

    fun getName(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> guideZh.split("|").getOrNull(0) ?: ""
            "es" -> guideEs.split("|").getOrNull(0) ?: ""
            "de" -> guideDe.split("|").getOrNull(0) ?: ""
            "fr" -> guideFr.split("|").getOrNull(0) ?: ""
            "sv" -> guideSv.split("|").getOrNull(0) ?: ""
            "ar" -> guideAr.split("|").getOrNull(0) ?: ""
            "ko" -> guideKo.split("|").getOrNull(0) ?: ""
            "ja" -> guideJa.split("|").getOrNull(0) ?: ""
            "ku" -> guideKu.split("|").getOrNull(0) ?: ""
            "fa" -> guideFa.split("|").getOrNull(0) ?: ""
            "iw" -> guideIw.split("|").getOrNull(0) ?: ""
            "tr" -> guideTr.split("|").getOrNull(0) ?: ""
            "zh-tw" -> guideZhTW.split("|").getOrNull(0) ?: ""
            "pt-br" -> guidePtBR.split("|").getOrNull(0) ?: ""
            "pt-pt" -> guidePt.split("|").getOrNull(0) ?: ""
            "it" -> guideIt.split("|").getOrNull(0) ?: ""
            else -> guideEn.split("|").getOrNull(0) ?: ""
        }
    }

    fun getDescribe(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> guideZh.split("|").getOrNull(1) ?: ""
            "es" -> guideEs.split("|").getOrNull(1) ?: ""
            "de" -> guideDe.split("|").getOrNull(1) ?: ""
            "fr" -> guideFr.split("|").getOrNull(1) ?: ""
            "sv" -> guideSv.split("|").getOrNull(1) ?: ""
            "ar" -> guideAr.split("|").getOrNull(1) ?: ""
            "ko" -> guideKo.split("|").getOrNull(1) ?: ""
            "ja" -> guideJa.split("|").getOrNull(1) ?: ""
            "ku" -> guideKu.split("|").getOrNull(1) ?: ""
            "fa" -> guideFa.split("|").getOrNull(1) ?: ""
            "iw" -> guideIw.split("|").getOrNull(1) ?: ""
            "tr" -> guideTr.split("|").getOrNull(1) ?: ""
            "zh-tw" -> guideZhTW.split("|").getOrNull(1) ?: ""
            "pt-br" -> guidePtBR.split("|").getOrNull(1) ?: ""
            "pt-pt" -> guidePt.split("|").getOrNull(1) ?: ""
            "it" -> guideIt.split("|").getOrNull(1) ?: ""
            else -> guideEn.split("|").getOrNull(1) ?: ""
        }
    }
}