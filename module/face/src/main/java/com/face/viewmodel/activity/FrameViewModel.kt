package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.apkfuns.logutils.LogUtils
import com.face.R
import com.face.net.Repository
import com.face.bean.TargetBean
import com.face.bean.TargetImgBean
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
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class FrameViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)

    var runNum = -1//需要提交的几张图
    var newRunCount = 0//当前提交第几张图

    //返回的人脸
    var listData = mutableListOf<TargetImgBean>()

    //提交图片得到的链接
    var postImgList = mutableListOf<String>()

    //返回的人脸
    var faceList = mutableListOf<TargetBean>()

    var taskId = ""
    val taskBean = MutableLiveData<TaskBean?>()

    fun uploadPicture() {
        loadFailed.postValue(false)
        showLoading(getStringX(R.string.post_face_analysis), false)
        launch(Dispatchers.IO) {
            //压缩
            val file =
                FileUtil.compressImage(listData[newRunCount].mBitmap)
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "file",
                    file.getName(),
                    file.asRequestBody(("application/otcet-stream").toMediaType())
                )
            launchRequestOnIO({ Repository.uploadFile(body) }) {
                onSuccess = {
                    postImgList.add(it?.src.toString())
                    newRunCount += 1
                    if (listData.size > newRunCount) {
                        uploadPicture()
                    } else {
                        sendAiTask()
                    }
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                    dismissLoading()
                    loadFailed.postValue(true)
                    postImgList = mutableListOf()
                }
            }
        }
    }

    fun sendAiTask() {
        var postUrlImg = ""
        postImgList.forEach {
            postUrlImg += "|$it"
        }
        launchRequestOnIO({
            Repository.sendAiTask(
                AiTaskType.FACE_ANALYSE,
                "",
                postUrlImg.substring(1)
            )
        }) {
            onSuccess = { bean ->
                taskId = bean?.id ?: ""
                queryAiTask()
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
                dismissLoading()
            }
        }
    }

    fun queryAiTask() {
        if (taskId.isNotEmpty()) {
            launchRequestOnIO({
                Repository.queryAiTask(taskId)
            }) {
                onSuccess = { bean ->//1上传中，2检查中，3完成，4失败
                    taskBean.value = bean
                }
                onComplete = {
                    launch(Dispatchers.IO) {
                        delay(1000)
                        if (taskBean.value?.state == 0 || taskBean.value?.state == 1 || taskBean.value == null) {
                            queryAiTask()
                        }
                    }
                }
            }
        }
    }
}
