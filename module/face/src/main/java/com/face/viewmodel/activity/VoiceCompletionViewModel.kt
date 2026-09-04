package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.R
import com.face.bean.ToolTaskBean
import com.face.key.AiTaskType
import com.face.net.download.DownloadProgressListener
import com.face.net.download.DownloadUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.toast
import java.io.File

class VoiceCompletionViewModel : BaseViewModel() {
    var loadingView = NotNullMutableLiveData(0)
    val isDownloadSate = NotNullMediatorLiveData(false)//是否下载中
    val downloadUrl = NotNullMutableLiveData("")//下载链接
    val isDownload = NotNullMediatorLiveData(false)//是否下载完成

    var dataBean = MutableLiveData(ToolTaskBean())

    var isPrepareAsync = false //是否音频初始完成
    var isPlay = NotNullMutableLiveData(false)

    val pathName = NotNullMutableLiveData("")
    val downName = NotNullMutableLiveData("")
    fun updataUserGenerateRecords(id: String?, name: String?) {
        if (id != "") {
            launchRequestWithLoadingOnIO({
                Repository.updataUserGenerateRecords(id = id, AiTaskType.VOICE_CLONING, name = name)
            }) {
                onSuccess = { it ->
                    dataBean.value = dataBean.value.apply {
                        this?.name = it?.name ?: ""
                    }
                    toast(getStringX(R.string.success))
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        }
    }


    fun getUserGenerate(id: String?) {
        launchRequestWithLoadingOnIO({
            Repository.getUserGenerate(id)
        }) {
            onSuccess = { it ->
                dataBean.value = it
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }


    fun download() {
        showLoading("Downloading...\n0%", false)
        isDownloadSate.postValue(true)
        var houType = downloadUrl.value.split(".").last()
        pathName.value =
            "${(System.currentTimeMillis() / 1000)}.$houType"
        downName.value = "downloadVoice.$houType"

        DownloadUtil.downloadFile(
            downloadUrl.value,
            downName.value,
            object : DownloadProgressListener {
                override fun onFinish(file: File?) {
                    dismissLoading()
                    isDownload.postValue(true)
                    isDownloadSate.postValue(false)
                }

                override fun onProgress(progress: Int) {
                    showLoading("Downloading...\n${progress}%", false)
                }

                override fun onFailed(errMsg: String?) {
                    toast("errMsg:$errMsg")
                    dismissLoading()
                    isDownloadSate.postValue(false)
                }

            })

    }


}