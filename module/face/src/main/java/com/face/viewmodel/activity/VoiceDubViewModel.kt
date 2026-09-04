package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.bean.TaskBean
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import kotlinx.coroutines.delay

class VoiceDubViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)

    var countdown = NotNullMutableLiveData("")
    var totalTime = NotNullMutableLiveData(0L)
    var progressNum = NotNullMutableLiveData(0)
    var taskId = ""
    val taskBean = MutableLiveData<TaskBean?>()

    fun queryAiTask() {
        if (taskId.isNotEmpty()) {
            launchRequestOnIO({
                Repository.queryAiTask(taskId)
            }) {
                onSuccess = { bean ->
                    if (taskBean.value == null) {
                        totalTime.postValue(bean?.estimatedTime?.minus(bean.createTime) ?: 0L)
                    }
                    taskBean.postValue(bean)
                }
                onFailed = { _, _, _ ->
                }
                onComplete = {
                    launch {
                        delay(5000)
                        if (taskBean.value?.state == 0 || taskBean.value?.state == 1) {
                            queryAiTask()
                        }
                    }

                }
            }
        }
    }

}