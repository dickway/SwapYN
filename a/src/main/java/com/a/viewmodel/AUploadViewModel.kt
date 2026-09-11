package com.a.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.face.R
import com.face.adapter.other.UploadTypeAdapter
import com.face.bean.TaskBean
import com.face.key.AiTaskType
import com.a.net.ARepository
import com.face.util.EventUtil
import com.face.util.FileUtil
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class AUploadViewModel : BaseViewModel() {
    val uploadTypeAdapter = UploadTypeAdapter()
    var uploadList = NotNullMutableLiveData(SPUtils.tabTitles)
    var numProgress = NotNullMutableLiveData(1)
    var uploadSate = MutableLiveData(0)//1上传中，2上传失败，3上传完成
    var repsBody1: Job? = null
    var repsBody2: Job? = null
    var repsBody3: Job? = null
    var uploadImgPath = NotNullMutableLiveData("")


    var tvFailure = ""
    var taskBean = MutableLiveData(TaskBean())


    var updateBitmap: Bitmap? = null//提交图片
    fun uploadPicture() {
        if (updateBitmap != null) {
            EventUtil.clickUploadFace()
            launch(Dispatchers.IO) {
//                showLoading(getStringX(R.string.compress))
                //压缩
                val file = FileUtil.compressImage(updateBitmap)
                val body: MultipartBody.Part =
                    MultipartBody.Part.createFormData(
                        "file",
                        file.getName(),
                        file.asRequestBody(("application/otcet-stream").toMediaType())
                    )
                repsBody1?.cancel()
                val timer = Timer()
                repsBody1 = launchRequestOnIO({ ARepository.uploadFile(body) }) {
                    onStart = {
                        dismissLoading()
                        uploadSate.postValue(1)
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                if (numProgress.value < 80) numProgress.postValue(numProgress.value + 1)
                            }
                        }, Date(), 350)
                    }
                    onSuccess = {
                        numProgress.postValue(81)
                        uploadImgPath.postValue(it?.src.toString())
                        sendAiTask(it?.src.toString())
                    }

                    onFailed = { _, _, _ ->
                        tvFailure = getStringX(R.string.upload_failed)
                        uploadSate.postValue(2)
                    }
                    onComplete = {
                        timer.cancel()
                    }
                }
            }
        }

    }

    fun cancelRepository() {
        uploadImgPath.postValue("")
        numProgress.postValue(0)
        taskBean.postValue(TaskBean())
        repsBody1?.cancel()
        repsBody1 = null
        repsBody2?.cancel()
        repsBody2 = null
        repsBody3?.cancel()
        repsBody3 = null
        updateBitmap = null
    }


    //检查人脸图片
    fun sendAiTask(uploadImg: String) {
        val timer = Timer()
        repsBody2?.cancel()
        repsBody2 =
            launchRequestOnIO({ ARepository.sendAiTask(AiTaskType.DETECT_FACE, "", uploadImg) }) {
                onStart = {
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            if (numProgress.value < 89) numProgress.postValue(numProgress.value + 1)
                        }
                    }, Date(), 500)
                }
                onSuccess = { bean ->
                    numProgress.postValue(90)
                    taskBean.postValue(bean)
                }
                onFailed = { _, _, errorMsg ->
                    tvFailure = getStringX(R.string.check)
                    uploadSate.postValue(2)
                }
                onComplete = {
                    timer.cancel()
                }
            }
    }

    fun queryAiTask(taskBeanId: String) {
        if (taskBeanId != "") {

            repsBody3?.cancel()
            repsBody3 = launchRequestOnIO({
                //循环请求,延迟一秒请求
                ARepository.queryAiTask(taskBeanId)
            }) {

                onStart = {
                    val valueNum = numProgress.value + 1
                    if (valueNum < 100) numProgress.postValue(valueNum)
                }

                onSuccess = { bean ->//0:创建，1，任务中，2:完成，3:任务异常
                    when (bean?.state) {
                        2 -> {//完成
                            EventUtil.clickCheck("success", taskBeanId)
                            val faceUrl = bean.result.faceUrl.firstOrNull()?.takeIf { it.isNotBlank() }
                            if (faceUrl != null) {
                                uploadImgPath.postValue(faceUrl)
                                uploadSate.postValue(3)
                            } else {
                                tvFailure = getStringX(R.string.upload_check_failure)
                                uploadSate.postValue(2)
                            }
                        }

                        3 -> {//失败
                            EventUtil.clickCheck("fail", taskBeanId)
                            tvFailure = bean.ee
                            uploadSate.postValue(2)
                            cancelRepository()
                        }
                    }
                }
                onFailed = { _, _, errorMsg ->

                }
                onComplete = {
                    launch(Dispatchers.IO) {
                        delay(1000)
                        //触发数据,重新请求
                        if (uploadSate.value == 1) taskBean.postValue(taskBean.value)
                    }
                }
            }
        }
    }
}
