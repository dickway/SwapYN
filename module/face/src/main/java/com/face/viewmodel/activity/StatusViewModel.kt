package com.face.viewmodel.activity

import com.face.net.Repository
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO

class StatusViewModel : BaseViewModel() {
//    var taskBean = MutableLiveData(TaskBean())
    //请求为了消除红点
    fun queryAiTask(taskBeanId: String) {
        if(!taskBeanId.equals("")){
            launchRequestOnIO({ Repository.queryAiTask(taskBeanId) }) {
                onSuccess = { bean ->
//                    taskBean.value=bean
                }
                onFailed = { _, _, errorMsg ->
                }
            }
        }
    }
}