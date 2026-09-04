package com.face.viewmodel.activity

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.apkfuns.logutils.LogUtils
import com.face.BuildConfig
import com.face.net.Repository
import com.face.R
import com.face.bean.MediaByBean
import com.face.bean.TaskBean
import com.face.bean.UseTypeBean
import com.face.bean.VideoAipointBean
import com.face.net.download.DownloadProgressListener
import com.face.net.download.DownloadUtil
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import java.io.File

class SwapNewViewModel : BaseViewModel() {
    val isHdModel = NotNullMediatorLiveData(SPUtils.isHdFace)
    val isInPageLifetimeVip = GVM.INSTANT.userInfo.value.vipLv//记录进入界面是不是永久VIP
    val isInPageVip = GVM.INSTANT.isVip.value//记录进入界面是不是VIP
    val isBlurView = NotNullMediatorLiveData(false)
    var showInterstitial=false

    val isScore = NotNullMediatorLiveData(false)//是否显示评分
    val isReport = NotNullMediatorLiveData(false)//是否显示举报
    val isVideo = NotNullMediatorLiveData(false)//是否是视频
    var collectStr = ""//收藏collect_id
    val proShow = NotNullMediatorLiveData(false)
    var taskContent=""
    var mediaId = ""
    private var videoAipoints =
        MoshiHelper.adapter(VideoAipointBean::class.java).fromJson(SPUtils.videoAipoints)
            ?: VideoAipointBean()

    val loadFailed = MutableLiveData(false)
    var isSingle = NotNullMutableLiveData(false)
    var usePinot = NotNullMutableLiveData(100)
    var mediaByBean = MutableLiveData<MediaByBean>()
    val isCollect = NotNullMediatorLiveData(false)//是否收藏
    val isLoad = NotNullMediatorLiveData(false)//是否加载
    val taskBean = MediatorLiveData<TaskBean?>()
    var isSelectPro = 0
    var sharePointsNum=0
    var errorCode = MutableLiveData(0)


    var isShowDownload = NotNullMutableLiveData(SPUtils.versionDownloadShow.contains("${getStringX(
        com.key.R.string.app_ai_name)}${BuildConfig.VERSION_CODE}"))
    var isDownloadSate = false//是否下载中
    var pathName = ""
    var downName = ""
    val isDownload = NotNullMediatorLiveData(false)//是否下载完成

    fun getData() {
        launchRequestWithLoadingOnIO({
            Repository.getMediaByID(mediaId)
        }) {
            onSuccess = {
                isLoad.value=true
                it?.apply {
                    if (userId.isNotEmpty() && GVM.INSTANT.userInfo.value.userId.toString()!=userId){
                        sharePointsNum =if (getIsShareState() && mediaType == "image") {
                            SPUtils.videoPic.toInt()
                        } else if (param.contains("300", true)) {
                            SPUtils.videoPoints5m.toInt()
                        } else if (param.contains("180", true)) {
                            SPUtils.videoPoints3m.toInt()
                        } else {
                            SPUtils.videoPoints20s.toInt()
                        }
                        if (!GVM.INSTANT.isVip.value){
                            isBlurView.value=true
                        }else{
                            isReport.value=true
                        }
                    }

                    if (getIsShareState() && mediaType == "image") {
                        usePinot.value = videoAipoints.typePic + sharePointsNum
                    }else if (it.param.contains("300")) {
                        usePinot.value=videoAipoints.type5+sharePointsNum
                    } else if (it.param.contains("180")) {
                        usePinot.value=videoAipoints.type3+sharePointsNum
                    } else if (it.param.contains("20")) {
                        usePinot.value=videoAipoints.type2+sharePointsNum
                    } else {
                        usePinot.value = 0
                    }
                    //增强收费
                    if (mediaType == "video" && isHdModel.value && GVM.INSTANT.userInfo.value.vipLv != 5) {
                        usePinot.value =usePinot.value + SPUtils.hdPoints
                    }
                    LogUtils.e(">>>>>>>>>usePinot:${usePinot.value}")
                    mediaByBean.value = it
                }
            }
            onFailed = { _, code, errorMsg ->
                toast(errorMsg)
                errorCode.postValue(code)
                loadFailed.postValue(true)
            }
        }
    }

    fun retryDate(){
        loadFailed.postValue(false)
        getData()
        getUserPics()
    }


    fun getUserPics() {
        launchRequestOnIO({
            Repository.getUserPics("")
        }) {
            onSuccess = { bean ->
                val oldList = GVM.INSTANT.swapSelectList.value?.toMutableList() ?: mutableListOf()
                val differenceList = bean?.toMutableList()?.minus(oldList) ?: mutableListOf()
                GVM.INSTANT.swapSelectList.value = bean?.toMutableList() ?: mutableListOf()
                if (oldList.isNotEmpty() && differenceList.size == 1) {//有新数据更新，默认选中
                    //当前选中素材的头像的下标
                    val selectNum = GVM.INSTANT.selectNum.value
                    //刷新素材头像对应的选中数据
                    GVM.INSTANT.swapTabList.value?.apply {
                        get(selectNum).isSelectBean = differenceList[0]
                    }
                    //设置选中的头像
                    GVM.INSTANT.selectBean.value = differenceList[0]
                    //刷新弹窗
                    GVM.INSTANT.selectNum.value = selectNum
                }
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }


    fun getReport(mediaId: String? = "", content: String = "", bolck: Int) {
        launchRequestOnIO({
            Repository.mediaBlack(mediaId ?: "", content, bolck)
        }) {
            onSuccess = {
                toast(getStringX(R.string.report_s))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun sendAiTask(type: String, sources: String) {
        launchRequestWithLoadingOnIO({
            Repository.sendAiTask(
                type,
                mediaByBean.value?.id,
                sources
            )
        }) {
            onStart = {
                loadFailed.postValue(false)
            }
            onSuccess = { bean ->
                val num1 = GVM.INSTANT.userInfo.value.mediaNum//用户生成次数
                if (num1 >= SPUtils.freeNum.toInt()) {//大于免费生成次数才开始计算当日生成
                    SPUtils.nowNum += 1
                }

                if (GVM.INSTANT.userInfo.value.isVip()) {
                    SPUtils.vipUserCount = SPUtils.vipUserCount.onEach {
                        if (it.id == GVM.INSTANT.userInfo.value.userId) {
                            it.countUse++
                        }
                    }
                }
                onUseID(bean)
                EventUtil.taskSend(mediaByBean.value?.id, type, sources)
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }

    fun onUseID(taskB: TaskBean?) {
        if (usePinot.value>0) {
            SPUtils.useNum += usePinot.value
            SPUtils.useType = UseTypeBean(type = "SwapFace", task_id = taskB?.id ?: "")
            launchRequestOnIO({
                Repository.subUserTFLOPS(
                    usePinot.value, type = MoshiHelper.convertObjectToJson(SPUtils.useType)
                )
            }) {
                onSuccess = {
                    SPUtils.useNum = 0
                    SPUtils.useType = UseTypeBean(type = "SwapFace", task_id = taskB?.id ?: "")
                    GVM.INSTANT.refreshUserInfo()
                }
                onComplete = {
                    taskBean.value = taskB
                }
            }
        } else {
            taskBean.value = taskB
        }
    }


    fun download(downloadUrl: String) {
        if (downloadUrl.isEmpty()) return
        showLoading("Downloading...\n0%", false)
        isDownloadSate=true
        val houType =downloadUrl.split(".").last()
        pathName =
            "${(System.currentTimeMillis() / 1000)}.$houType"
        downName = "download_file.$houType"

        DownloadUtil.downloadFile(
            downloadUrl,
            downName,
            object : DownloadProgressListener {
                override fun onFinish(file: File?) {
                    dismissLoading()
                    isDownload.postValue(true)
                    isDownloadSate=false
                }

                override fun onProgress(progress: Int) {
                    showLoading("Downloading...\n${progress}%", false)
                }

                override fun onFailed(errMsg: String?) {
                    toast("errMsg:$errMsg")
                    dismissLoading()
                    isDownloadSate=false
                }

            })

    }
}