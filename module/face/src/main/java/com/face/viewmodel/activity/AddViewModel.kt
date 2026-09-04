package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.apkfuns.logutils.LogUtils
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

class AddViewModel : BaseViewModel() {

    //是不是单张图片上传
    var isSingular = NotNullMutableLiveData(true)

    //图片是否选择
    var isChoose = NotNullMutableLiveData(false)

    var usePinot = NotNullMutableLiveData(0)

    var img1 = NotNullMutableLiveData("")
    var img2 = NotNullMutableLiveData("")

    var taskT =""
    var firstImg =""

    var taskId = MutableLiveData("")

    fun uploadPicture(filePath: String) {
        launch(Dispatchers.IO) {
            val file = FileUtil.compressImage2(filePath)
            val body: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                file.asRequestBody(("application/otcet-stream").toMediaType())
            )
            launchRequestOnIO({ Repository.uploadFile(body) }) {
                onSuccess = {
                    if (isSingular.value) {//是两张图还是一张图
                        sendAiTask(it?.src.toString())
                    } else {
                        if (firstImg.isNotEmpty()){//第二张图
                            sendAiTask("${firstImg}|${it?.src.toString()}")
                        }else{
                            firstImg = it?.src.toString()
                            if(img2.value.isEmpty()){//两张图时，第2张可以传空
                                sendAiTask("${firstImg}|")
                            }else{
                                uploadPicture(img2.value)
                            }
                        }
                    }
                }
                onFailed = { _, _, errorMsg ->
                    dismissLoading()
                    firstImg=""
                    toast(errorMsg)
                }
            }
        }
    }

    fun sendAiTask(sources: String) {
        launchRequestOnIO({
            Repository.sendAiTask(
                taskT,
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