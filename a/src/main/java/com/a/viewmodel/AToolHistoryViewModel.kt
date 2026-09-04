package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.adapter.AToolHistoryAdapter
import com.apkfuns.logutils.LogUtils
import com.face.net.Repository
import com.face.R
import com.face.adapter.history.HistoryClearAdapter
import com.face.bean.ToolTaskBean
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.toast

class AToolHistoryViewModel : BaseViewModel() {
    val isNoData = MutableLiveData(false)
    val loadFailed = MutableLiveData(false)
    val homeRefreshing = MutableLiveData<Boolean>()

    var historyFaceList = MutableLiveData<List<ToolTaskBean>>(listOf())

    val historyAdapter: AToolHistoryAdapter = AToolHistoryAdapter()

    var aiType=""

    fun getUserRecordPage() {
        homeRefreshing.postValue(true)
        launchRequestOnIO({
            Repository.getUserRecordPage(aiType)
        }) {
            onSuccess = { bean ->
                isNoData.setIfNot(bean?.size == 0)
                historyFaceList.value = bean?.toMutableList()?:listOf()
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.setIfNot(true)
                toast(errorMsg)
            }
            onComplete = {
                homeRefreshing.postValue(false)
            }
        }
    }


    fun updataUserGenerateRecords(id: String?, name: String?) {
        if (id != "") {
            launchRequestWithLoadingOnIO({
                Repository.updataUserGenerateRecords(id = id, aiType, name = name)
            }) {
                onSuccess = { it ->
                    getUserRecordPage()
                    toast(getStringX(R.string.success))
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        }
    }
}