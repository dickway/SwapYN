package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.bean.ToolTaskBean
import com.face.key.AiTaskType
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class VoiceCutViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)

    var seekbarNum = NotNullMutableLiveData(0)
    var isPlay = NotNullMutableLiveData(false)
    var playTime = 0
    var isPrepareAsync = false //是否音频初始完成

    var startTime = 0


    var toolTaskBean = NotNullMutableLiveData(ToolTaskBean())
    fun uploadPicture(postUrl: String?) {
        if (postUrl != null) {
            launch(Dispatchers.IO) {
                val file = File(postUrl)
                val body: MultipartBody.Part =
                    MultipartBody.Part.createFormData(
                        "file",
                        file.getName(),
                        file.asRequestBody(("application/otcet-stream").toMediaType())
                    )
                launchRequestOnIO({ Repository.uploadFile(body) }) {
                    onStart = {
                        loadFailed.postValue(false)
                    }
                    onSuccess = {
                        val language = when (SPbaseUtils.spLanguage) {
                            "zh" -> "zh-CN"
                            "es" -> "es-ES"
                            else -> "en-US"
                        }
                        val sources = it?.src.toString() + "|" + language
                        sendAiTask(sources)
                    }
                    onFailed = { _, _, errorMsg ->
                        dismissLoading()
                        loadFailed.postValue(true)
                        toast(errorMsg)
                    }
                }
            }
        }
    }

    fun sendAiTask( sources: String) {
        launchRequestOnIO({
            Repository.sendAiTask(
                AiTaskType.VOICE_ANALYSE, "", sources
            )
        }) {
            onStart = {
                loadFailed.postValue(false)
            }
            onSuccess = { bean ->
                dismissLoading()
                val taskBean = ToolTaskBean()
                taskBean.taskId = bean?.id ?: ""
                taskBean.state = bean?.state ?: 0
                taskBean.errorMessage = bean?.ee ?: ""
                toolTaskBean.postValue(taskBean)
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }


}