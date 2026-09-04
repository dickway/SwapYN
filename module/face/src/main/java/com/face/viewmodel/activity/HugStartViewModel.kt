package com.face.viewmodel.activity

import com.zzkj.structure.base.BaseViewModel

class HugStartViewModel : BaseViewModel() {
//    var GeneratingId = MutableLiveData("-1")
//
//    fun getTask() {
//        GeneratingId.value = "-1"
//        launchRequestWithLoadingOnIO({
//            Repository.getUserAiTask(AiTaskType.TEXT2IMG)
//        }) {
//            onStart = {
//            }
//            onSuccess = { bean ->
//                //是否有生成中的
//                var taskId = ""
//                bean?.forEach { taskBean ->
//                    if (taskBean.state == 1 || taskBean.state == 0) {
//                        taskId = taskBean.id
//                    }
//                }
//                GeneratingId.postValue(taskId)
//            }
//            onFailed = { _, _, errorMsg ->
//                toast(errorMsg)
//            }
//        }
//    }
}