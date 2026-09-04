package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.adapter.AMeCollectAdapter
import com.face.bean.CollectAMBean
import com.face.net.Repository
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.toast

class AColletViewModel : BaseViewModel() {
    val collectAdapter: AMeCollectAdapter = AMeCollectAdapter()
    var collectList = MutableLiveData<List<CollectAMBean>?>()
    val isNoData = MutableLiveData<Boolean?>()
    fun getCollect() {
        launchRequestWithLoadingOnIO({ Repository.getUserCollect() }) {
            onSuccess = { list ->
                isNoData.setIfNot((list?.size ?: 0) < 1)
                val scList=list?.filter { it.type == "media" }
                collectList.postValue(scList)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }
}