package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.adapter.share.PointsHistAdapter
import com.face.bean.ShareZoneBean
import com.face.net.Repository
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.singleToast

class PointsHistModel : BaseViewModel() {
    val isNoData = MutableLiveData(false)
    val loadFailed = MutableLiveData(false)
    val isLoading = MutableLiveData<Boolean>(false)
    val isNoMoreData = NotNullMutableLiveData(false)
    private var page = 1
    //历史列表
    val histList = MutableLiveData<MutableList<ShareZoneBean>>()
    var histAdapter: PointsHistAdapter = PointsHistAdapter()

    fun getUserItemLog(isRefresh: Boolean = true) {
        if (isLoading.value == true) {
            isLoading.value = false
            return
        }
        if (isRefresh) {
            page = 1
            isNoMoreData.value = false
        }
        isLoading.value = true
        launchRequestOnIO({
            Repository.getUserItemLog(
                page,
            )
        }) {
            onSuccess = { it ->
                if (it?.isNullOrEmpty()==true) {
                    isNoMoreData.value = true
                    if (page == 1) {
                        isNoData.value = true
                        histList.value = mutableListOf()
                    }
                } else {
                    isNoData.value = false
                    if (page == 1) {
                        histList.value = it!!
                    } else {
                        histList.value =
                            (histList.value?.toMutableList() ?: mutableListOf()).apply {
                                addAll(it!!)
                            }
                    }
                    page++
                }
            }
            onFailed = { _, _, m ->
                singleToast(m)
                if (histList.value.isNullOrEmpty()) {
                    isNoMoreData.value = true
//                    isNoData.value = true
                    loadFailed.value = true
                }
            }
            onComplete = {
                isLoading.postValue(false)
            }
        }
    }
}