package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.adapter.other.ColorAdapter
import com.face.adapter.other.SizeAdapter
import com.face.adapter.other.TxtImgSizeAdapter
import com.face.adapter.other.UserAdapter
import com.face.bean.ColorBean
import com.face.bean.SizeBean
import com.face.key.AiTaskType
import com.face.util.EventUtil
import com.face.util.SPUtils
import com.luck.picture.lib.language.LanguageConfig
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.toast

class TxtImgViewModel : BaseViewModel() {
    //默认文字
    val txtEdit = when (SPbaseUtils.spLanguage) {
        "zh" -> SPUtils.txtimgZH
        "zh-tw" -> SPUtils.txtimgZHTW
        "es" -> SPUtils.txtimgES
        "de" -> SPUtils.txtimgDE
        "fr" -> SPUtils.txtimgFR
        "sv" -> SPUtils.txtimgSV
        "ar" -> SPUtils.txtimgAR
        "ko" -> SPUtils.txtimgKO
        "ja" -> SPUtils.txtimgJA
        "ku" -> SPUtils.txtimgKU
        "iw" -> SPUtils.txtimgIW
        "fa" -> SPUtils.txtimgFA
        "tr" -> SPUtils.txtimgTR
        "pt-br" -> SPUtils.txtimgPTBR
        "pt-pt" -> SPUtils.txtimgPT
        "it" -> SPUtils.txtimgIT
        else -> SPUtils.txtimgEN
    }

    //nsfw 0关闭
    var nsfw = 1

    //文字数量
    val txtNum = MutableLiveData(0)

    val sizeAdapter: TxtImgSizeAdapter = TxtImgSizeAdapter()


    var postTxt =""

    //    model：1对应写实，0对应动漫
    var postStyle = MutableLiveData("1")
    var postSize = MutableLiveData("512*512")

    var taskId = MutableLiveData("")


    val sizeList = NotNullMutableLiveData(
        listOf(
            SizeBean("", "", "512 x 512 px", 1, 1, "512*512", com.key.R.mipmap.img_icon_txt_size1),
            SizeBean("", "", "512 x 682 px", 3, 4, "512*682", com.key.R.mipmap.img_icon_txt_size2),
        )
    )

    fun sendAiTask() {
        val sources =
            "${postTxt}|${nsfw}|${postStyle.value}|${postSize.value}"

        launchRequestWithLoadingOnIO({
            Repository.sendAiTask(
                AiTaskType.TEXT2IMG,
                "",
                sources
            )
        }) {
            onSuccess = { bean ->
                EventUtil.taskSend(bean?.id, AiTaskType.TEXT2IMG, sources)
                taskId.postValue(bean?.id)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }


}