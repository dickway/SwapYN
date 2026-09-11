package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.adapter.AMeWorkAdapter
import com.face.bean.TaskBean
import com.a.net.ARepository
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.toast

class AWorkViewModel : BaseViewModel() {
    val workAdapter: AMeWorkAdapter = AMeWorkAdapter()
    val isNoData = MutableLiveData(false)
    var workFaceList = MutableLiveData<List<TaskBean>>(listOf())

    fun sendAiTask() {//自动刷新不需要展示加载圈
        launchRequestOnIO({ ARepository.getUserMedia("All") }) {
            onSuccess = { bean ->
                isNoData.setIfNot((bean?.size ?: 0) < 1)
                workFaceList.postValue(bean?.toMutableList())
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

}
