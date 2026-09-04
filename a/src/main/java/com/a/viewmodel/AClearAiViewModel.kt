package com.a.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.R
import com.face.bean.ToolTaskBean
import com.face.bean.TaskBean
import com.face.key.AiTaskType
import com.face.util.FileUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class AClearAiViewModel : BaseViewModel() {
    var seekbarNum = NotNullMutableLiveData(40)

    var showSave = NotNullMutableLiveData(false)
    val loadFailed = MutableLiveData(false)
    val loading = MutableLiveData(false)
    var showOriginacl = NotNullMutableLiveData(false)
    var showNext = NotNullMutableLiveData(false)
    var showUp = NotNullMutableLiveData(false)

    var initBitmap: Bitmap? = null//最开始的图片
    var nextBitmap: Bitmap? = null//缓存下一次消除图片
    var upBitmap: Bitmap? = null//缓存上一次消除图片
    var nextWaterBitmap: Bitmap? = null//缓存下一次消除水印图片
    var upWaterBitmap: Bitmap? = null//缓存上一次消除水印图片

    var postUrl = NotNullMutableLiveData("")//当前图片连接
    var postWaterUrl = NotNullMutableLiveData("")//当前图片水印连接

    var nextUrl = ""//缓存下一次消除连接
    var upUrl = ""//缓存上一次消除连接
    var nextWaterUrl = ""//缓存下一次消除水印连接
    var upWaterUrl = ""//缓存上一次消除水印连接

    var postBitmap: Bitmap? = null//当前需要提交的图片
    var maskBitmap: Bitmap? = null//提交mask图片

    var originalUrl = ""
    var maskUrl = ""

    var countdown = NotNullMutableLiveData("")
    var totalTime = NotNullMutableLiveData(0L)
    var progressNum = NotNullMutableLiveData(0)
    var taskId = ""
    val taskBean = MutableLiveData<TaskBean?>()

    var saveAiclearBean = MutableLiveData<ToolTaskBean?>()
    fun uploadPicture(mBitmap: Bitmap?, type: Int) {//type:1是原始图片，2是mask图片
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
                        if (type == 1) {
                            originalUrl = it?.src.toString()
                            uploadPicture(maskBitmap, 2)
                        } else {
                            maskUrl = it?.src.toString()
                            sendAiTask(AiTaskType.LAMA_CLEANER, "${originalUrl},${maskUrl}")
                        }
                    }
                    onFailed = { _, _, errorMsg ->
                        dismissLoading()
                        loading.postValue(false)
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
            onStart = {
                loadFailed.postValue(false)
            }
            onSuccess = { bean ->
                taskId = bean?.id ?: ""
                taskBean.postValue(null)
                queryAiTask()
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                loading.postValue(false)
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }


    fun queryAiTask() {
        if (taskId.isNotEmpty()) {

            launchRequestOnIO({
                Repository.queryAiTask(taskId)
            }) {
                onSuccess = { bean ->
                    if (taskBean.value == null) {
                        loading.postValue(true)
                        dismissLoading()
                        totalTime.postValue(bean?.estimatedTime?.minus(bean.createTime) ?: 0L)
                    }
                    taskBean.postValue(bean)
                }
                onComplete = {
                    launch(Dispatchers.IO) {
                        delay(1000)
                        if (taskBean.value?.state == 0 || taskBean.value?.state == 1 || taskBean.value == null) queryAiTask()
                    }
                }
            }
        }
    }

    fun saveUserGenerateRecords(url: String?, name: String?) {
        launchRequestWithLoadingOnIO({
            Repository.saveUserGenerateRecords(url = url, AiTaskType.LAMA_CLEANER, name = name)
        }) {
            onSuccess = { bean ->
                saveAiclearBean.postValue(bean)
                toast(getStringX(R.string.success))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

}