package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.adapter.ACollectAdapter
import com.face.bean.CollectAMBean
import com.a.net.ARepository
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.toast
import kotlin.collections.filter
import kotlin.collections.isNullOrEmpty

class ACollectViewModel : BaseViewModel() {

    val collectAdapter: ACollectAdapter = ACollectAdapter()
    var collectList = MutableLiveData<List<CollectAMBean>?>()
    val isNoData = MutableLiveData<Boolean?>()
    fun getCollect() {
        launchRequestWithLoadingOnIO({ ARepository.getUserCollect() }) {
            onSuccess = { list ->
                if (list.isNullOrEmpty()) {
                    isNoData.postValue(true)
                }
                val scList=list?.filter { it.type == "media" }
                collectList.postValue(scList)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }
}
