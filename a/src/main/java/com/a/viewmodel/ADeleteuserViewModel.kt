package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.net.ARepository
import com.face.util.EventUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.toast

class ADeleteuserViewModel : BaseViewModel() {
    val isDisable = MutableLiveData(true)
    val isNext = MutableLiveData(false)
    val isDelete = MutableLiveData(false)

    fun deleteUserInfo() {
        launchRequestWithLoadingOnIO({ ARepository.deleteUserInfo() }) {
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
