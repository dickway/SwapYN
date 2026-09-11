package com.face.viewmodel.activity

import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.face.net.Repository
import com.face.key.AiTaskType
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

class HugViewModel : BaseViewModel() {

    //选择图片模式
    val isSingular = NotNullMutableLiveData(true)

    //图片是否选择
    val isChoose = NotNullMutableLiveData(false)

    var hugPinot = NotNullMutableLiveData(SPUtils.hugPinot)

    var img1 = NotNullMutableLiveData("")
    var img2 = NotNullMutableLiveData("")
    var img3 = NotNullMutableLiveData("")

    var firstImg = ""

    var taskId = MutableLiveData("")

    fun uploadPicture(filePath: String) {//type:1是原始图片，2是mask图片
        launch(Dispatchers.IO) {
            val file = FileUtil.compressImage2(filePath)
            val body: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                file.asRequestBody(("application/otcet-stream").toMediaType())
            )
            launchRequestOnIO({ Repository.uploadFile(body) }) {
                onSuccess = {
                    if (isSingular.value) {
                        sendAiTask(it?.src.toString())
                    } else {
                        if (firstImg.isNotEmpty()) {
                            sendAiTask("${firstImg}|${it?.src.toString()}")
                        } else {
                            firstImg = it?.src.toString()
                            uploadPicture(img2.value)
                        }
                    }
                }
                onFailed = { _, _, errorMsg ->
                    dismissLoading()
                    firstImg = ""
                    toast(errorMsg)
                }
            }
        }
    }

    fun sendAiTask(sources: String) {
        launchRequestOnIO({
            Repository.sendAiTask(
                AiTaskType.IMG2HUG,
                "",
                sources
            )
        }) {
            onSuccess = { bean ->
                taskId.postValue(bean?.id)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
            onComplete = {
                dismissLoading()
            }
        }
    }
}