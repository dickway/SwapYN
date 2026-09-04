package com.face.viewmodel.activity

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.key.AiTaskType
import com.face.util.EventUtil
import com.face.util.FileUtil
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class MovesViewModel : BaseViewModel() {

    //图片是否选择
    val isChoose = NotNullMutableLiveData(false)

    var movesPinot = SPUtils.movesPinot

    var postImg = ""//提交图片链接

    val loadFailed = MutableLiveData(false)

    var taskId = MutableLiveData("")

    fun uploadPicture(type: String) {
        launch(Dispatchers.IO) {
            val file = FileUtil.compressImage2(postImg)
            val body: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                file.asRequestBody(("application/otcet-stream").toMediaType())
            )
            launchRequestOnIO({ Repository.uploadFile(body) }) {
                onSuccess = {
                    sendAiTask("${it?.src.toString()}|${type}")
                }
                onFailed = { _, _, errorMsg ->
                    dismissLoading()
                    toast(errorMsg)
                }
            }
        }
    }

    fun sendAiTask(sources: String) {
        launchRequestOnIO({
            Repository.sendAiTask(
                AiTaskType.IMG2VIDEO, "", sources
            )
        }) {
            onSuccess = { bean ->
                dismissLoading()
                EventUtil.taskSend(bean?.id, AiTaskType.IMG2VIDEO, sources)
                taskId.postValue(bean?.id)
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }

}