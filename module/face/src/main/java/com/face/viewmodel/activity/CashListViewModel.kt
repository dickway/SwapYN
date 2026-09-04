package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.adapter.other.CashAdapter
import com.face.bean.AiFaceBean
import com.face.bean.CaskBean
import com.face.net.Repository
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.toast

class CashListViewModel : BaseViewModel() {

    var cashAdapter = CashAdapter()

    val isNoData = MutableLiveData(false)

    val loadFailed = MutableLiveData(false)

    val cashList = MutableLiveData<List<CaskBean>>(mutableListOf())

    fun userGifts() {
        loadFailed.postValue(false)
        launchRequestOnIO({ Repository.userGifts() }) {
            onSuccess = { bean ->
                isNoData.postValue(bean.isNullOrEmpty())
                cashList.postValue(bean ?: mutableListOf())
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }
}