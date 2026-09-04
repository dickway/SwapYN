package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.bean.DynamicAddBean
import com.face.bean.TaskBean
import com.face.net.Repository
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.toast

class AddStartViewModel : BaseViewModel() {

    var GeneratingId = MutableLiveData("-1")

    fun getTask(taskType: String) {
        GeneratingId.value = "-1"
        launchRequestWithLoadingOnIO({
            Repository.getUserAiTask(taskType)
        }) {
            onStart = {
            }
            onSuccess = { bean ->
                //是否有生成中的
                var taskId = ""
                bean?.forEach { taskBean ->
                    if (taskBean.state == 1 || taskBean.state == 0) {
                        taskId = taskBean.id
                    }
                }
                GeneratingId.postValue(taskId)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }
}