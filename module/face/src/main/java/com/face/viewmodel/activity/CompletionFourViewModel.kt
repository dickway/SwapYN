package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.R
import com.face.bean.TaskBean
import com.face.net.download.DownloadProgressListener
import com.face.net.download.DownloadUtil
import com.face.util.EventUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.toast
import java.io.File

class CompletionFourViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)

    val downloadUrl = NotNullMutableLiveData("")//下载链接
    val showUrl = NotNullMutableLiveData("")//显示链接

    val isCollect = NotNullMediatorLiveData(false)//是否收藏
    var collectStr = ""//收藏collect_id
    val taskid = NotNullMutableLiveData("")
    val taskNum = NotNullMutableLiveData(0)
    val pathName = NotNullMutableLiveData("")
    val downName = NotNullMutableLiveData("")

    val isDownloadSate = NotNullMediatorLiveData(false)//是否下载中
    val isDownload = NotNullMediatorLiveData(false)//是否下载完成

    val isShowVideo = NotNullMediatorLiveData(false)//是否是视频
    val isVideo = NotNullMediatorLiveData(true)//是否播放声音
    val isOpen = NotNullMediatorLiveData(true)//是否开始播放
    val isPause = NotNullMediatorLiveData(false)//是否暂停播放

    val taskBean = MutableLiveData(TaskBean())
    fun saveCollect() {
        launchRequestOnIO({
            if (isCollect.value) {
                EventUtil.clickDisCollect(collectStr)
                Repository.removeUserCollect(collectStr)
            } else {
                Repository.saveCollect(taskid.value, "task", taskNum.value)
            }
        }) {
            onSuccess = {
                collectStr = it.toString()
                isCollect.value = !isCollect.value
                toast(getStringX(R.string.success))
            }
            onFailed = { _, _, errorMsg ->
                isCollect.value = isCollect.value
                toast(errorMsg)
            }
        }
    }

    fun sendAiTask() {
        launchRequestOnIO({ Repository.queryAiTask(taskid.value) }) {
            onSuccess = { bean ->
                loadFailed.postValue(false)
                taskBean.value = bean
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
                loadFailed.postValue(true)
            }
        }
    }

    fun getUserCollectUrl() {
        launchRequestOnIO({
            Repository.getUserCollectUrl(showUrl.value)
        }) {
            onSuccess = {
                if (it != 0) {
                    collectStr = it.toString()
                    isCollect.value = true
                }
            }
            onFailed = { _, _, errorMsg ->
                isCollect.value = false
                toast(errorMsg)
            }
        }
    }

    fun download() {
        showLoading("Downloading...\n0%", false)
        EventUtil.clickDownload(downloadUrl.value)
        isDownloadSate.postValue(true)
        var houType = downloadUrl.value.split(".").last()
        pathName.value =
            "${(System.currentTimeMillis() / 1000)}.$houType"
        downName.value = "downloadImng.$houType"

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