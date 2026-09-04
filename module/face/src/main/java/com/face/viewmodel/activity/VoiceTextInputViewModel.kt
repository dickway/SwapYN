package com.face.viewmodel.activity

import com.face.net.Repository
import com.face.bean.ToolTaskBean
import com.face.key.AiTaskType
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.toast

class VoiceTextInputViewModel : BaseViewModel() {
    val inputNum = NotNullMutableLiveData(0)
    var completionTaskBean = NotNullMutableLiveData(ToolTaskBean())

    var sources = ""
    var typeVoice = "self"


    fun sendAiTask() {
        launchRequestWithLoadingOnIO({
            Repository.sendAiTask(
                AiTaskType.VOICE_CLONING, "", sources
            )
        }) {
            onSuccess = { bean ->
                val taskBean = ToolTaskBean()
                taskBean.taskId = bean?.id ?: ""
                taskBean.state = bean?.state ?: 0
                taskBean.errorMessage = bean?.ee ?: ""
                completionTaskBean.postValue(taskBean)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }
}