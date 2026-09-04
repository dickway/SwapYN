package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.R
import com.face.net.Repository
import com.face.adapter.other.FaceBrowsingAdapter
import com.face.bean.AiFaceBean
import com.face.bean.ToolTaskBean
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.toast

class BrowsingViewModel : BaseViewModel() {

    val historyAdapter: FaceBrowsingAdapter = FaceBrowsingAdapter()
    var historyList = MutableLiveData<List<AiFaceBean>?>()
    val isNoData = MutableLiveData<Boolean?>()
    fun getUserViewHistory() {
        launchRequestWithLoadingOnIO({ Repository.getUserViewHistory() }) {
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


    fun deleteTask() {
        launchRequestWithLoadingOnIO({ Repository.removeUserView() }) {
            onSuccess = {
                getUserViewHistory()
                toast(getStringX(R.string.success))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

}