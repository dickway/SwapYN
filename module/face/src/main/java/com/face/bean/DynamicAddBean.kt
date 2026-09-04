package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.zzkj.structure.util.SPbaseUtils

@JsonClass(generateAdapter = true)
@Parcelize
data class DynamicAddBean(
    @Json(name = "desc_de")
    val descDe: String = "",
    @Json(name = "desc_en")
    val descEn: String = "",
    @Json(name = "desc_es")
    val descEs: String = "",
    @Json(name = "desc_fr")
    val descFr: String = "",
    @Json(name = "desc_sv")
    val descSv: String = "",
    @Json(name = "desc_zh")
    val descZh: String = "",
    @Json(name = "desc_ar")
    val descAr: String = "",
    @Json(name = "desc_ko")
    val descKo: String = "",
    @Json(name = "desc_ja")
    val descJa: String = "",
    @Json(name = "desc_ku")
    val descKu: String = "",
    @Json(name = "desc_fa")
    val descFa: String = "",
    @Json(name = "desc_iw")
    val descIw: String = "",
    @Json(name = "desc_tr")
    val descTr: String = "",
    @Json(name = "desc_zh_tw")
    val descZhTW: String = "",
    @Json(name = "desc_pt_br")
    val descPtBR: String = "",
    @Json(name = "desc_pt_pt")
    val descPt: String = "",
    @Json(name = "desc_it")
    val descIt: String = "",

    @Json(name = "event_name")
    val eventName: String = "",
    @Json(name = "is_single_task")
    val isSingleTask: Boolean = false,
    @Json(name = "is_one_choose")
    val isOneChoose: Boolean = true,
    @Json(name = "need_select")
    val needSelect: Boolean = true,
    @Json(name = "is_can_stop")
    val isCanStop: Boolean = true,
    @Json(name = "is_again")
    val isAgain: Boolean = false,
    @Json(name = "is_video")
    val isVideo: Boolean = false,
    @Json(name = "id")
    val id: Int = -1,
    @Json(name = "use_point")
    val usePoint: Int = 0,
    @Json(name = "sheep_points_de")
    val sheepPointsDe: String = "",
    @Json(name = "sheep_points_en")
    val sheepPointsEn: String = "",
    @Json(name = "sheep_points_es")
    val sheepPointsEs: String = "",
    @Json(name = "sheep_points_fr")
    val sheepPointsFr: String = "",
    @Json(name = "sheep_points_sv")
    val sheepPointsSv: String = "",
    @Json(name = "sheep_points_zh")
    val sheepPointsZh: String = "",
    @Json(name = "sheep_points_ar")
    val sheepPointsAr: String = "",
    @Json(name = "sheep_points_ko")
    val sheepPointsKo: String = "",
    @Json(name = "sheep_points_ja")
    val sheepPointsJa: String = "",
    @Json(name = "sheep_points_fa")
    val sheepPointsFa: String = "",
    @Json(name = "sheep_points_iw")
    val sheepPointsIw: String = "",
    @Json(name = "sheep_points_ku")
    val sheepPointsKu: String = "",
    @Json(name = "sheep_points_tr")
    val sheepPointsTr: String = "",
    @Json(name = "sheep_points_zh_tw")
    val sheepPointsZhTW: String = "",
    @Json(name = "sheep_points_pt_br")
    val sheepPointsPtBR: String = "",
    @Json(name = "sheep_points_pt_pt")
    val sheepPointsPt: String = "",
    @Json(name = "sheep_points_it")
    val sheepPointsIt: String = "",

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
    @Json(name = "name_ko")
    val nameKo: String = "",
    @Json(name = "name_ja")
    val nameJa: String = "",
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

    @Json(name = "task_type")
    val taskType: String = "",
    @Json(name = "url_img")
    val urlImg: String = "",
    @Json(name = "url_video")
    val urlVideo: String = "",
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
    @Json(name = "guide_zh_tw")
    val guideZhTW: String = "",
    @Json(name = "guide_it")
    val guideIt: String = "",

    @Json(name = "guide_desc_de")
    val guideDescDe: String = "",
    @Json(name = "guide_desc_en")
    val guideDescEn: String = "",
    @Json(name = "guide_desc_es")
    val guideDescEs: String = "",
    @Json(name = "guide_desc_fr")
    val guideDescFr: String = "",
    @Json(name = "guide_desc_sv")
    val guideDescSv: String = "",
    @Json(name = "guide_desc_zh")
    val guideDescZh: String = "",
    @Json(name = "guide_desc_ar")
    val guideDescAr: String = "",
    @Json(name = "guide_desc_ko")
    val guideDescKo: String = "",
    @Json(name = "guide_desc_ja")
    val guideDescJa: String = "",
    @Json(name = "guide_desc_fa")
    val guideDescFa: String = "",
    @Json(name = "guide_desc_iw")
    val guideDescIw: String = "",
    @Json(name = "guide_desc_ku")
    val guideDescKu: String = "",
    @Json(name = "guide_desc_tr")
    val guideDescTr: String = "",
    @Json(name = "guide_desc_pt_br")
    val guideDescPtBR: String = "",
    @Json(name = "guide_desc_pt_pt")
    val guideDescPt: String = "",
    @Json(name = "guide_desc_zh_tw")
    val guideDescZhTW: String = "",
    @Json(name = "guide_desc_it")
    val guideDescIt: String = "",

    @Json(name = "title2_de")
    val title2De: String = "",
    @Json(name = "title2_en")
    val title2En: String = "",
    @Json(name = "title2_es")
    val title2Es: String = "",
    @Json(name = "title2_fr")
    val title2Fr: String = "",
    @Json(name = "title2_sv")
    val title2Sv: String = "",
    @Json(name = "title2_zh")
    val title2Zh: String = "",
    @Json(name = "title2_ar")
    val title2Ar: String = "",
    @Json(name = "title2_ko")
    val title2Ko: String = "",
    @Json(name = "title2_ja")
    val title2Ja: String = "",
    @Json(name = "title2_fa")
    val title2Fa: String = "",
    @Json(name = "title2_iw")
    val title2Iw: String = "",
    @Json(name = "title2_ku")
    val title2Ku: String = "",
    @Json(name = "title2_tr")
    val title2Tr: String = "",
    @Json(name = "title2_pt_br")
    val title2PtBR: String = "",
    @Json(name = "title2_pt_pt")
    val title2Pt: String = "",
    @Json(name = "title2_zh_tw")
    val title2ZhTW: String = "",
    @Json(name = "title2_it")
    val title2It: String = "",

    @Json(name = "title1_de")
    val title1De: String = "",
    @Json(name = "title1_en")
    val title1En: String = "",
    @Json(name = "title1_es")
    val title1Es: String = "",
    @Json(name = "title1_fr")
    val title1Fr: String = "",
    @Json(name = "title1_sv")
    val title1Sv: String = "",
    @Json(name = "title1_zh")
    val title1Zh: String = "",
    @Json(name = "title1_ar")
    val title1Ar: String = "",
    @Json(name = "title1_ko")
    val title1Ko: String = "",
    @Json(name = "title1_ja")
    val title1Ja: String = "",
    @Json(name = "title1_fa")
    val title1Fa: String = "",
    @Json(name = "title1_iw")
    val title1Iw: String = "",
    @Json(name = "title1_ku")
    val title1Ku: String = "",
    @Json(name = "title1_tr")
    val title1Tr: String = "",
    @Json(name = "title1_pt_br")
    val title1PtBR: String = "",
    @Json(name = "title1_pt_pt")
    val title1Pt: String = "",
    @Json(name = "title1_zh_tw")
    val title1ZhTW: String = "",
    @Json(name = "title1_it")
    val title1It: String = "",

    @Json(name = "title2_desc_de")
    val title2DescDe: String = "",
    @Json(name = "title2_desc_en")
    val title2DescEn: String = "",
    @Json(name = "title2_desc_es")
    val title2DescEs: String = "",
    @Json(name = "title2_desc_fr")
    val title2DescFr: String = "",
    @Json(name = "title2_desc_sv")
    val title2DescSv: String = "",
    @Json(name = "title2_desc_zh")
    val title2DescZh: String = "",
    @Json(name = "title2_desc_ar")
    val title2DescAr: String = "",
    @Json(name = "title2_desc_ko")
    val title2DescKo: String = "",
    @Json(name = "title2_desc_ja")
    val title2DescJa: String = "",
    @Json(name = "title2_desc_fa")
    val title2DescFa: String = "",
    @Json(name = "title2_desc_iw")
    val title2DescIw: String = "",
    @Json(name = "title2_desc_ku")
    val title2DescKu: String = "",
    @Json(name = "title2_desc_tr")
    val title2DescTr: String = "",
    @Json(name = "title2_desc_pt_br")
    val title2DescPtBR: String = "",
    @Json(name = "title2_desc_pt_pt")
    val title2DescPt: String = "",
    @Json(name = "title2_desc_zh_tw")
    val title2DescZhTW: String = "",
    @Json(name = "title2_desc_it")
    val title2DescIt: String = "",

    @Json(name = "title1_desc_de")
    val title1DescDe: String = "",
    @Json(name = "title1_desc_en")
    val title1DescEn: String = "",
    @Json(name = "title1_desc_es")
    val title1DescEs: String = "",
    @Json(name = "title1_desc_fr")
    val title1DescFr: String = "",
    @Json(name = "title1_desc_sv")
    val title1DescSv: String = "",
    @Json(name = "title1_desc_zh")
    val title1DescZh: String = "",
    @Json(name = "title1_desc_ar")
    val title1DescAr: String = "",
    @Json(name = "title1_desc_ko")
    val title1DescKo: String = "",
    @Json(name = "title1_desc_ja")
    val title1DescJa: String = "",
    @Json(name = "title1_desc_fa")
    val title1DescFa: String = "",
    @Json(name = "title1_desc_iw")
    val title1DescIw: String = "",
    @Json(name = "title1_desc_ku")
    val title1DescKu: String = "",
    @Json(name = "title1_desc_tr")
    val title1DescTr: String = "",
    @Json(name = "title1_desc_pt_br")
    val title1DescPtBR: String = "",
    @Json(name = "title1_desc_pt_pt")
    val title1DescPt: String = "",
    @Json(name = "title1_desc_zh_tw")
    val title1DescZhTW: String = "",
    @Json(name = "title1_desc_it")
    val title1DescIt: String = "",
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

    fun getDesc(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> descZh
            "es" -> descEs
            "de" -> descDe
            "fr" -> descFr
            "sv" -> descSv
            "ar" -> descAr
            "ja" -> descJa
            "ko" -> descKo
            "ku" -> descKu
            "fa" -> descFa
            "iw" -> descIw
            "tr" -> descTr
            "zh-tw" -> descZhTW
            "pt-br" -> descPtBR
            "pt-pt" -> descPt
            "it" -> descIt
            else -> descEn
        }
    }

    fun getHintTitel(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> guideZh
            "es" -> guideEs
            "de" -> guideDe
            "fr" -> guideFr
            "sv" -> guideSv
            "ar" -> guideAr
            "ja" -> guideJa
            "ko" -> guideKo
            "ku" -> guideKu
            "fa" -> guideFa
            "iw" -> guideIw
            "tr" -> guideTr
            "zh-tw" -> guideZhTW
            "pt-br" -> guidePtBR
            "pt-pt" -> guidePt
            "it" -> guideIt
            else -> guideEn
        }
    }

    fun getHintContent(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> guideDescZh
            "es" -> guideDescEs
            "de" -> guideDescDe
            "fr" -> guideDescFr
            "sv" -> guideDescSv
            "ar" -> guideDescAr
            "ja" -> guideDescJa
            "ko" -> guideDescKo
            "ku" -> guideDescKu
            "fa" -> guideDescFa
            "iw" -> guideDescIw
            "tr" -> guideDescTr
            "zh-tw" -> guideDescZhTW
            "pt-br" -> guideDescPtBR
            "pt-pt" -> guideDescPt
            "it" -> guideDescIt
            else -> guideDescEn
        }
    }

    fun getImgTitle1(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> title1Zh
            "es" -> title1Es
            "de" -> title1De
            "fr" -> title1Fr
            "sv" -> title1Sv
            "ar" -> title1Ar
            "ja" -> title1Ja
            "ko" -> title1Ko
            "ku" -> title1Ku
            "fa" -> title1Fa
            "iw" -> title1Iw
            "tr" -> title1Tr
            "zh-tw" -> title1ZhTW
            "pt-br" -> title1PtBR
            "pt-pt" -> title1Pt
            "it" -> title1It
            else -> title1En
        }
    }

    fun getImgDesc1(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> title1DescZh
            "es" -> title1DescEs
            "de" -> title1DescDe
            "fr" -> title1DescFr
            "sv" -> title1DescSv
            "ar" -> title1DescAr
            "ja" -> title1DescJa
            "ko" -> title1DescKo
            "ku" -> title1DescKu
            "fa" -> title1DescFa
            "iw" -> title1DescIw
            "tr" -> title1DescTr
            "zh-tw" -> title1DescZhTW
            "pt-br" -> title1DescPtBR
            "pt-pt" -> title1DescPt
            "it" -> title1DescIt
            else -> title1DescEn
        }
    }

    fun getImgTitle2(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> title2Zh
            "es" -> title2Es
            "de" -> title2De
            "fr" -> title2Fr
            "sv" -> title2Sv
            "ar" -> title2Ar
            "ja" -> title2Ja
            "ko" -> title2Ko
            "ku" -> title2Ku
            "fa" -> title2Fa
            "iw" -> title2Iw
            "tr" -> title2Tr
            "zh-tw" -> title2ZhTW
            "pt-br" -> title2PtBR
            "pt-pt" -> title2Pt
            "it" ->  title2It
            else -> title2En
        }
    }

    fun getImgDesc2(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> title2DescZh
            "es" -> title2DescEs
            "de" -> title2DescDe
            "fr" -> title2DescFr
            "sv" -> title2DescSv
            "ar" -> title2DescAr
            "ja" -> title2DescJa
            "ko" -> title2DescKo
            "ku" -> title2DescKu
            "fa" -> title2DescFa
            "iw" -> title2DescIw
            "tr" -> title2DescTr
            "zh-tw" -> title2DescZhTW
            "pt-br" -> title2DescPtBR
            "pt-pt" -> title2DescPt
            "it" ->  title2DescIt
            else -> title2DescEn
        }
    }

    fun getSheepPointsName(): String {
        return when (SPbaseUtils.spLanguage) {
            "zh" -> sheepPointsZh
            "es" -> sheepPointsEs
            "de" -> sheepPointsDe
            "fr" -> sheepPointsFr
            "sv" -> sheepPointsSv
            "ar" -> sheepPointsAr
            "ja" -> sheepPointsJa
            "ko" -> sheepPointsKo
            "ku" -> sheepPointsKu
            "fa" -> sheepPointsFa
            "iw" -> sheepPointsIw
            "tr" -> sheepPointsTr
            "zh-tw" -> sheepPointsZhTW
            "pt-br" -> sheepPointsPtBR
            "pt-pt" -> sheepPointsPt
            "it" ->  sheepPointsIt
            else -> sheepPointsEn
        }
    }
}