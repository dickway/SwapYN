package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.apkfuns.logutils.LogUtils
import com.face.R
import com.face.net.Repository
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.toast

class CashViewModel : BaseViewModel() {

    val isGift = NotNullMutableLiveData(true)
    val giftNum = MutableLiveData(SPUtils.giftNum)

    fun giftOverNum() {
        launchRequestOnIO({ Repository.giftOverNum() }) {
            onSuccess = {
                SPUtils.giftNum = it.toString()
                giftNum.postValue(it.toString())
            }
        }
    }

}