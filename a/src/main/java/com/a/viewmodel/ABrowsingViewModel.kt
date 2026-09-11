package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.adapter.AFaceBrowsingAdapter
import com.face.bean.AiFaceBean
import com.a.net.ARepository
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.toast
import kotlin.collections.distinctBy

class ABrowsingViewModel : BaseViewModel() {

    val historyAdapter: AFaceBrowsingAdapter = AFaceBrowsingAdapter()
    var historyList = MutableLiveData<List<AiFaceBean>?>()
    val isNoData = MutableLiveData<Boolean?>()
    fun getUserViewHistory() {
        launchRequestWithLoadingOnIO({ ARepository.getUserViewHistory() }) {
            onSuccess = { list ->
                isNoData.setIfNot((list?.size ?: 0) < 1)
                val distinctList = list?.distinctBy { it.imageUrl }
                historyList.postValue(distinctList)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }
}
