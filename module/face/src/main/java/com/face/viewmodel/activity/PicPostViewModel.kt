package com.face.viewmodel.activity

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.bean.TaskBean
import com.face.key.AiTaskType
import com.face.util.FileUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class PicPostViewModel : BaseViewModel() {

    val loadFailed = NotNullMutableLiveData(false)
    var uploadSate = MutableLiveData(0)//1上传中，2检查中，3完成，4失败,5结束
    var numProgress = NotNullMutableLiveData(1)//转圈进度条
    var checkingProgress = NotNullMutableLiveData(0)//横向进度条
    var updateUrl = ""//提交链接
    var uploadImgPath = NotNullMutableLiveData("")//返回素材链接
    var type = "image"
    var repsBody3: Job? = null
    var taskBean = MutableLiveData(TaskBean())
    var hw = ""//素材宽高
    var faceUrl = ""//素材替换头像
    var onlyReps = true
    var faceId = NotNullMutableLiveData("")//返回素材id
    var updateBitmap: Bitmap? = null//提交图片

    fun uploadPicture() {
        if (updateBitmap != null) {
            launch(Dispatchers.IO) {
                //压缩
                val file = FileUtil.compressImage(updateBitmap)
                val body: MultipartBody.Part =
                    MultipartBody.Part.createFormData(
                        "file",
                        file.getName(),
                        file.asRequestBody(("application/otcet-stream").toMediaType())
                    )
                val timer = Timer()
                launchRequestOnIO({ Repository.uploadFile(body) }) {
                    onStart = {
                        dismissLoading()
                        loadFailed.postValue(false)
                        uploadSate.postValue(1)
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                if (numProgress.value < 89) numProgress.postValue(numProgress.value + 1)
                            }
                        }, Date(), 80)
                    }
                    onSuccess = {
                        numProgress.postValue(90)
                        uploadImgPath.postValue(it?.src.toString())
                        sendAiTask(it?.src.toString())
                    }

                    onFailed = { _, _, errorMsg ->
                        loadFailed.postValue(true)
                        cancelRepository()
                    }
                    onComplete = {
                        timer.cancel()
                    }
                }
            }
        }
    }

    fun sendAiTask(uploadImg: String) {
        val timer = Timer()
        launchRequestOnIO({ Repository.sendAiTask(AiTaskType.FACE_ANALYSE, "", uploadImg) }) {
            onStart = {
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        if (numProgress.value < 99) numProgress.postValue(
                            numProgress.value + 1
                        )
                    }
                }, Date(), 100)
            }
            onSuccess = { bean ->
                uploadSate.postValue(2)
                numProgress.postValue(100)
                taskBean.postValue(bean)
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                cancelRepository()
            }
            onComplete = {
                timer.cancel()
            }
        }
    }

    fun queryAiTask(taskBeanId: String) {
        if (taskBeanId != "") {
            launch(Dispatchers.IO) {
                delay(1000)
                repsBody3?.cancel()
                repsBody3 = launchRequestOnIO({
                    //循环请求,延迟一秒请求
                    Repository.queryAiTask(taskBeanId)
                }) {

                    onStart = {
                        val valueNum = checkingProgress.value + (5..20).random()
                        if (valueNum < 100) checkingProgress.postValue(valueNum)
                    }

                    onSuccess = { bean ->//1上传中，2检查中，3完成，4失败
                        when (bean?.state) {
                            2 -> {//完成
                                checkingProgress.postValue(100)
                                faceUrl = bean.result.faceUrl.let { it ->
                                    var url = ""
                                    val result = it.take(3)
                                    result.forEach { str ->
                                        url = if (url == "") {
                                            str
                                        } else {
                                            "$url,$str"
                                        }
                                    }
                                    url
                                }
                                uploadSate.postValue(3)
                            }

                            3 -> {//失败
                                toast(bean.ee)
                                cancelRepository()
                                uploadSate.postValue(4)
                            }
                        }
                    }
                    onFailed = { _, _, errorMsg ->
                        loadFailed.postValue(true)
                    }
                    onComplete = {
                        //触发数据,重新请求
                        taskBean.postValue(taskBean.value)
                    }
                }
            }
        }
    }


    fun saveUserAiMedia() {
        if (onlyReps) {
            onlyReps = false
            launchRequestOnIO({
                Repository.saveUserAiMedia(
                    "image",
                    uploadImgPath.value,
                    faceUrl,
                    hw ,
                    param= "{\"sub\":\"1\"}"
                )
            }) {
                onSuccess = { bean ->
                    faceId.postValue(bean.toString())
                }
                onFailed = { _, _, errorMsg ->
                    loadFailed.postValue(true)
//                    toast(errorMsg)
//                    cancelRepository()
//                    uploadSate.postValue(4)
                }
            }
        }
    }


    fun cancelRepository() {
        uploadImgPath.postValue("")
        numProgress.postValue(0)
        checkingProgress.postValue(0)
        taskBean.postValue(TaskBean())
        repsBody3?.cancel()
        repsBody3 = null
    }
}