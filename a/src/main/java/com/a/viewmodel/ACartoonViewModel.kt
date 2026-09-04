package com.a.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.a.adapter.ACartoonAdapter
import com.face.adapter.other.StyleAdapter
import com.face.bean.StyleBean
import com.face.bean.TagConfigBean.Companion.toMap
import com.face.net.Repository
import com.face.bean.TaskBean
import com.face.key.AiTaskType
import com.face.util.FileUtil
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseApp
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ACartoonViewModel : BaseViewModel() {

    val styleAdapter: ACartoonAdapter = ACartoonAdapter()
    var styleList = MutableLiveData(SPUtils.styleList)

    val loadFailed = MutableLiveData(false)
    var loadingView = NotNullMutableLiveData(0)

    var initBitmap: Bitmap? = null//提交的图片

    var hightImg = 0
    var wigthImg = 0

    var countdown = NotNullMutableLiveData("")
    var totalTime = NotNullMutableLiveData(0L)
    var progressNum = NotNullMutableLiveData(0)
    var taskId = ""
    var style = "cute"
    val taskBean = MutableLiveData<TaskBean?>()

    fun getStyle() {
        launchRequestOnIO({ Repository.getStyle("common_style") }) {
            onSuccess = {
                val styleStr = it?.toMap()?.get("style") ?: ""
                if (styleStr != "") {
                    val jsonStr = MoshiHelper.listAdapter(StyleBean::class.java).fromJson(styleStr)
//                        ?.onEach { bean ->
//                            when (bean.key) {
//                                "cute" -> bean.video =
//                                    "android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style1}" //可爱
//                                "oil" -> bean.video =
//                                    "android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style2}"//油画
//                                "gongqijun" -> bean.video =
//                                    "android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style3}"//日漫
//                                "niantu" -> bean.video =
//                                    "android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style4}"//粘土
//                                else -> bean.video =
//                                    "android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style5}"
//                            }
//                        }
                    SPUtils.styleList = jsonStr ?: mutableListOf()
                    styleList.postValue(jsonStr)
                    style = jsonStr?.firstOrNull()?.key ?: "cute"
                }
            }
        }
    }

    fun uploadPicture() {
        if (initBitmap != null) {
            launch(Dispatchers.IO) {
                //压缩
                val file = FileUtil.compressImage(initBitmap)
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
                            AiTaskType.IMG2CARTOON,
                            "${
                                it?.src?.trim().toString()
                            }|$style|$wigthImg*$hightImg" //source|style|w*h
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