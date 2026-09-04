package com.face.viewmodel.activity

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.bean.TaskBean
import com.face.key.AiTaskType
import com.face.util.FileUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class CutoutAiViewModel : BaseViewModel() {
    var seekbarNum = NotNullMutableLiveData(40)

    var showSave = NotNullMutableLiveData(false)
    val loadFailed = MutableLiveData(false)
    var loadingView = NotNullMutableLiveData(0)

    var initBitmap: Bitmap? = null//最开始的图片
    var cutBitmap: Bitmap? = null//提交cut图片


    var countdown = NotNullMutableLiveData("")
    var totalTime = NotNullMutableLiveData(0L)
    var progressNum = NotNullMutableLiveData(0)
    var taskId = ""
    val taskBean = MutableLiveData<TaskBean?>()

    fun uploadPicture(mBitmap: Bitmap?) {
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
                        sendAiTask(AiTaskType.RMBG, it?.src.toString())
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
                        loadingView.postValue(2)
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

    fun onCancelData() {
        if (taskBean.value != null) {
            launchRequestWithLoadingOnIO({ Repository.cancelTask(taskBean.value!!.id) }) {
                onSuccess = {
                    loadingView.postValue(4)
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        }
    }
}