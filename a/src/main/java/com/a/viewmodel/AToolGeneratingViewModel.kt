package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.net.ARepository
import com.face.bean.TaskBean
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class AToolGeneratingViewModel : BaseViewModel() {

    var loadingView = NotNullMutableLiveData(0)

    var countdown = NotNullMutableLiveData("")
    var totalTime = NotNullMutableLiveData(0L)
    var progressNum = NotNullMutableLiveData(0)

    var taskId = ""
    val taskBean = MutableLiveData<TaskBean?>()

    fun queryAiTask() {
        if (taskId.isNotEmpty()) {
            launchRequestOnIO({
                ARepository.queryAiTask(taskId)
            }) {
                onSuccess = { bean ->
                    if (taskBean.value == null) {
                        loadingView.postValue(2)
                        dismissLoading()
                        totalTime.postValue(bean?.estimatedTime?.minus(bean.createTime) ?: 0L)
                    }
                    taskBean.postValue(bean)

                }
                onComplete = {
                    launch(Dispatchers.IO) {
                        delay(2000)
                        if (taskBean.value?.state == 0 || taskBean.value?.state == 1 || taskBean.value == null) queryAiTask()
                    }
                }
            }
        }
    }
}
