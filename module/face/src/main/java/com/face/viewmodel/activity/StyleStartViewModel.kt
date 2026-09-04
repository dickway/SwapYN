package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.R
import com.face.adapter.other.StyleAdapter
import com.face.bean.StyleBean
import com.face.bean.TagConfigBean.Companion.toMap
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseApp
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.moshi.MoshiHelper

class StyleStartViewModel : BaseViewModel() {

    val styleAdapter: StyleAdapter = StyleAdapter()
    var styleList = MutableLiveData(SPUtils.styleList)
    var selectStyleBean: StyleBean? = null

    fun getStyle() {
        launchRequestOnIO({ Repository.getStyle("common_style") }) {
            onSuccess = {
                val styleStr = it?.toMap()?.get("style") ?: ""
                if (styleStr != "") {
                    val jsonStr = MoshiHelper.listAdapter(StyleBean::class.java).fromJson(styleStr)
                        ?.onEach { bean ->
                            when (bean.key) {
                                "cute" -> bean.video =
                                    "android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style1}" //可爱
                                "oil" -> bean.video =
                                    "android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style2}"//油画
                                "gongqijun" -> bean.video =
                                    "android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style3}"//日漫
                                "niantu" -> bean.video =
                                    "android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style4}"//粘土
                                else -> bean.video =
                                    "android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style5}"
                            }
                        }
                    SPUtils.styleList = jsonStr ?: mutableListOf()
                    styleList.postValue(jsonStr)
                    selectStyleBean = jsonStr?.firstOrNull()
                }
            }
        }
    }
}