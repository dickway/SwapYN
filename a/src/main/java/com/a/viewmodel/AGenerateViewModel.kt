package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.R
import com.a.adapter.AHistoryAdapter
import com.face.bean.TaskBean
import com.a.net.ARepository
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.toast
import kotlin.collections.toMutableList

class AGenerateViewModel : BaseViewModel() {
    val historyAdapter: AHistoryAdapter = AHistoryAdapter()

    val isNoData = MutableLiveData(false)
    val loadFailed = MutableLiveData<Boolean?>()
    val homeRefreshing = MutableLiveData<Boolean>()
    var historyFaceList = MutableLiveData<List<TaskBean>>(listOf())

    fun sendAiTask() {//自动刷新不需要展示加载圈
        launchRequestOnIO({ ARepository.getUserMedia("All") }) {
            onStart = {
                loadFailed.setIfNot(false)
                homeRefreshing.postValue(true)
            }
            onSuccess = { bean ->
                isNoData.setIfNot((bean?.size ?: 0) < 1)
                historyFaceList.postValue(bean?.toMutableList())
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.setIfNot(true)
                toast(errorMsg)
            }
            onComplete = {
                homeRefreshing.postValue(false)
            }
        }
    }

    fun deleteTask(bean: TaskBean?) {
        if (bean != null) {
            launchRequestWithLoadingOnIO({ ARepository.deleteUserMedia(bean.id) }) {
                onSuccess = {
                    sendAiTask()
                    toast(getStringX(R.string.asuccess))
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        }
    }
}
