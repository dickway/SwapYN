package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.util.EventUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.toast

class ADeleteuserViewModel : BaseViewModel() {
    val isDisable = MutableLiveData(true)
    val isNext = MutableLiveData(false)
    val isDelete = MutableLiveData(false)

    fun deleteUserInfo() {
        launchRequestWithLoadingOnIO({ Repository.deleteUserInfo() }) {
            onSuccess = { bean ->
                EventUtil.deleteUser()
                isDelete.postValue(true)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

}