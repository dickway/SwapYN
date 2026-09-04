package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.download.DownloadProgressListener
import com.face.net.download.DownloadUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.toast
import java.io.File

class ToolCompletionViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)

    var downloadUrl = ""//下载链接
    var showUrl = ""//显示链接

    val pathName = NotNullMutableLiveData("")
    val downName = NotNullMutableLiveData("")

    val isDownloadSate = NotNullMediatorLiveData(false)//是否下载中
    val isDownload = NotNullMediatorLiveData(false)//是否下载完成


    fun download() {
        showLoading("Downloading...\n1%", false)
        isDownloadSate.postValue(true)
        val houType = downloadUrl.split(".").last()
        pathName.value =
            "${(System.currentTimeMillis() / 1000)}.$houType"
        downName.value = "downloadImng.$houType"

        DownloadUtil.downloadFile(
            downloadUrl,
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