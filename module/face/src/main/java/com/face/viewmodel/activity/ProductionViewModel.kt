package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.bean.TaskBean
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class ProductionViewModel : BaseViewModel() {
    var proStr = MutableLiveData("")
    val loadFailed = MutableLiveData(false)
    var taskId = NotNullMutableLiveData("")
    var countdown = NotNullMutableLiveData("")
    val taskBean = MutableLiveData<TaskBean?>()
    var totalTime = NotNullMutableLiveData(0L)
    var progressNum = NotNullMutableLiveData(0)

    var repsAiTask: Job? = null
    fun sendAiTask() {
        launch {
            delay(1500)
            repsAiTask = launchRequestOnIO({
                Repository.queryAiTask(taskId.value)
            }) {
                onSuccess = { bean ->
                    if (taskBean.value == null || taskBean.value?.estimatedTime != bean?.estimatedTime) {
                        totalTime.postValue(bean?.estimatedTime?.minus(bean.createTime) ?: 0L)
                    }
                    taskBean.postValue(bean)
                }
                onFailed = { _, _, _ ->
                    taskBean.postValue(taskBean.value)
                }
            }
        }
    }
}