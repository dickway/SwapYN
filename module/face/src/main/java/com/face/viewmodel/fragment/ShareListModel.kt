package com.face.viewmodel.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.face.bean.AiFaceBean
import com.face.net.Repository
import com.face.util.GVM
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.singleToast
import com.zzkj.structure.util.toast
import kotlin.collections.forEachIndexed

class ShareListModel : BaseViewModel() {

    val isNoData = MutableLiveData(false)
    val loadFailed = MutableLiveData(false)
    val isLoading = MutableLiveData<Boolean>(false)
    val isNoMoreData = NotNullMutableLiveData(false)
    var page = 1

    var videoType ="image"

    //分享列表
    val shareZeList = MutableLiveData<MutableList<AiFaceBean>>()

    val isFirstShow = shareZeList.map {
        it.isNullOrEmpty()
    }

    fun deleteMediaShare(media: AiFaceBean,result:()->Unit ) {
        launchRequestWithLoadingOnIO({ Repository.deleteMediaShare(media.id) }) {
            onSuccess = {
                result()
                getShareData(isOwner=1)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun getShareData(isRefresh: Boolean = true, isOwner: Int = 0) {
        if (isLoading.value == true) {
            isLoading.value = false
            return
        }
        if (isRefresh) {
            page = 1
            isNoMoreData.value = false
        } else {
            isLoading.value = true
        }
        loadFailed.value = false
        launchRequestOnIO({
            Repository.getShareZoneList(page, is_owner = isOwner,videoType)
        }) {
            onSuccess = { it ->
                if (it.isNullOrEmpty()) {
                    isNoMoreData.value = true
                    if (page == 1) {
                        isNoData.value = true
                        shareZeList.value = mutableListOf()
                    }
                } else {
                    isNoData.value = false
                    it.forEachIndexed { p, sn ->
                        sn.isVip = GVM.INSTANT.isVip.value
                        if (!GVM.INSTANT.isVip.value) {
                            sn.isBlur = !(p == 0 || p == 1)
                        }
                    }
                    if (page == 1) {
                        shareZeList.value = it.toMutableList()
                    } else {
                        shareZeList.value =
                            (shareZeList.value?.toMutableList() ?: mutableListOf()).apply {
                                addAll(it)
                            }
                    }
                    page++
                }
            }

            onFailed = { _, _, m ->
                singleToast(m)
                if (shareZeList.value.isNullOrEmpty()) {
                    isNoMoreData.value = true
                    loadFailed.value = true
                }
            }
            onComplete = {
                isLoading.postValue(false)
            }
        }
    }
}