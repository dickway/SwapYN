package com.face.viewmodel.activity

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.key.AiTaskType
import com.face.util.EventUtil
import com.face.util.FileUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class LiveAiViewModel : BaseViewModel() {
    var seekbarNum = 10

    var initBitmap: Bitmap? = null//最开始的图片

    val loadFailed = MutableLiveData(false)

    var taskId = MutableLiveData("")

    fun uploadPicture(mBitmap: Bitmap?) {//type:1是原始图片，2是mask图片
        if (mBitmap != null) {
            launch(Dispatchers.IO) {
                //压缩
                val file = FileUtil.compressImage(mBitmap)
                val body: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "file",
                    file.getName(),
                    file.asRequestBody(("application/otcet-stream").toMediaType())
                )
                launchRequestOnIO({ Repository.uploadFile(body) }) {
                    onStart = {
                        loadFailed.postValue(false)
                    }
                    onSuccess = {
                        sendAiTask(
                            AiTaskType.DYNAMIC,
                            it?.src?.trim().toString()
                        )
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

    fun sendAiTask(type: String, sources: String) {
        launchRequestOnIO({
            Repository.sendAiTask(
                type, "", sources
            )
        }) {
            onSuccess = { bean ->
                dismissLoading()
                EventUtil.taskSend(bean?.id, AiTaskType.DYNAMIC, sources)
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