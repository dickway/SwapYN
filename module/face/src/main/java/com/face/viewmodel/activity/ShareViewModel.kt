package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.bean.ShareBean
import com.face.util.GVM
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.toast

class ShareViewModel : BaseViewModel() {

    val isShow = MutableLiveData(false)
    val shareBean = MutableLiveData(ShareBean())
    fun getUserAiMedia() {
        if (GVM.INSTANT.isShowTool.value != 0) {
            launchRequestOnIO({ Repository.getShareLink() }) {
                onSuccess = {
                    shareBean.value = it
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        } else {
            shareBean.value =
                ShareBean(url = "https://play.google.com/store/apps/details?id=com.face")
        }
    }
}