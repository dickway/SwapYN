package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.R
import com.face.bean.TaskBean
import com.a.net.ARepository
import com.face.net.download.DownloadProgressListener
import com.face.net.download.DownloadUtil
import com.face.util.EventUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.toast
import java.io.File

class ACompletionViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)

    val downloadUrl = NotNullMutableLiveData("")//下载链接
    val showUrl = NotNullMutableLiveData("")//显示链接

    val taskid = NotNullMutableLiveData("")
    val pathName = NotNullMutableLiveData("")
    val downName = NotNullMutableLiveData("")

    val isDownloadSate = NotNullMediatorLiveData(false)//是否下载中
    val isDownload = NotNullMediatorLiveData(false)//是否下载完成

    val isShowVideo = NotNullMediatorLiveData(false)//是否是视频

    val taskBean = MutableLiveData(TaskBean())

    fun getData() {
        if (taskBean.value?.id == "")
            launchRequestOnIO({ ARepository.queryAiTask(taskid.value) }) {
                onSuccess = { bean ->
                    taskBean.value = bean
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
    }

    fun download() {
        showLoading("Downloading...\n0%", false)
        EventUtil.clickDownload(downloadUrl.value)
        isDownloadSate.postValue(true)
        val houType = downloadUrl.value.split(".").last()
        pathName.value =
            "${(System.currentTimeMillis() / 1000)}.$houType"
        downName.value = "downloadImg.$houType"

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
