package com.face.viewmodel.fragment

import androidx.lifecycle.MutableLiveData
import com.face.R
import com.face.adapter.other.FaceAdapter
import com.face.bean.AiFaceBean
import com.face.bean.TaskBean
import com.face.bean.UsePointBean
import com.face.bean.UseTypeBean
import com.face.net.Repository
import com.face.net.download.DownloadProgressListener
import com.face.net.download.DownloadUtil
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import java.io.File

class CompletionViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)

    val isRate = NotNullMediatorLiveData(false)//是否是评分任务
    val isScore = NotNullMediatorLiveData(false)//是否显示评分
    val score = NotNullMediatorLiveData(0f)//评分

    val isZoom= MutableLiveData(false)

    val isShowAB = MutableLiveData(GVM.Companion.INSTANT.isShowTool.value)

    var isUsePoint = false//是否试用积分下载

    val useBean = MutableLiveData(
        MoshiHelper.adapter(UsePointBean::class.java).fromJson(SPUtils.usePoints)
            ?: UsePointBean()
    )

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

    fun onUserAds() {
        SPUtils.useNum += useBean.value?.pointsDownload ?: 0
        SPUtils.useType = UseTypeBean(type = "Download", download_url = downloadUrl.value)
        showLoading(cancelable = false)
        launchRequestOnIO({ Repository.subUserTFLOPS(
            SPUtils.useNum, MoshiHelper.convertObjectToJson(
                SPUtils.useType)) }) {
            onSuccess = {
                SPUtils.useNum = 0
                SPUtils.useType = UseTypeBean(type = "Download", download_url = downloadUrl.value)
                GVM.Companion.INSTANT.refreshUserInfo {
                    dismissLoading()
                }
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
            }
        }
    }


    fun getData() {
        sendAiTask()
    }

    val likeList = MutableLiveData<List<AiFaceBean>>(listOf())
    fun getUserLikeMedia() {
        loadFailed.postValue(false)
        if (GVM.Companion.INSTANT.isShowTool.value != 0) {
            launchRequestOnIO({ Repository.getUserLikeMedia() }) {
                onSuccess = { bean ->
                    loadFailed.postValue(false)
                    likeList.postValue(bean ?: mutableListOf())
                }
                onFailed = { _, _, errorMsg ->
                    loadFailed.postValue(true)
                    toast(errorMsg)
                }
            }
        } else {
            launchRequestOnIO({ Repository.getBanners() }) {
                onSuccess = { bean ->
                    loadFailed.postValue(false)
                    likeList.postValue(bean ?: mutableListOf())
                }
                onFailed = { _, _, errorMsg ->
                    loadFailed.postValue(true)
                    toast(errorMsg)
                }
            }
        }
    }


    fun sendAiTask() {
        if (taskBean.value?.id == "")
            launchRequestOnIO({ Repository.queryAiTask(taskid.value) }) {
                onSuccess = { bean ->
                    taskBean.value = bean
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
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
                    if (isUsePoint) {
                        isUsePoint = false
                        onUserAds()
                    }
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