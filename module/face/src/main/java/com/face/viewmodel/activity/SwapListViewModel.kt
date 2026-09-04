package com.face.viewmodel.activity

import android.R.attr.versionName
import android.os.Build
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.apkfuns.logutils.LogUtils
import com.face.BuildConfig
import com.face.net.Repository
import com.face.bean.AiFaceBean
import com.face.bean.TaskBean
import com.face.bean.UseTypeBean
import com.face.bean.VideoAipointBean
import com.face.net.download.DownloadProgressListener
import com.face.net.download.DownloadUtil
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.key.R
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import java.io.File

class SwapListViewModel : BaseViewModel() {

    val isHdModel = NotNullMediatorLiveData(SPUtils.isHdFace)

    val isScore = NotNullMediatorLiveData(false)//是否显示评分
    val isReport = NotNullMediatorLiveData(false)//是否显示举报

    var showInterstitial = false
    val isInPageVip = GVM.INSTANT.isVip.value//记录进入界面是不是VIP
    var isInPageLifetimeVip = GVM.INSTANT.userInfo.value.vipLv//记录进入界面是不是永久VIP
    val indexBlur = MediatorLiveData<Int>()//-1表示不禁止滑动

    //    val isRefresh =MediatorLiveData<Boolean>()//数据是否加载完成
    var isBlurView = NotNullMutableLiveData(false)//是否显示宣传

    val isVideo = NotNullMediatorLiveData(false)//是否是视频
    var mediaByBean = MutableLiveData(AiFaceBean())
    val mediaListBean = NotNullMutableLiveData<MutableList<AiFaceBean>>(mutableListOf())
    var mediaSize = 0

    var videoAipoints =
        MoshiHelper.adapter(VideoAipointBean::class.java).fromJson(SPUtils.videoAipoints)
            ?: VideoAipointBean()

    val loadFailed = MutableLiveData(false)
    var isSingle = NotNullMutableLiveData(false)
    var usePinot = NotNullMutableLiveData(100)
    val isCollect = NotNullMediatorLiveData(false)//是否收藏
    val taskBean = MediatorLiveData<TaskBean?>()
    var isSelectPro = 0
    var curSelectedPosition = 0
    var collectStr = ""//收藏collect_id

    var isShowDownload = NotNullMutableLiveData(SPUtils.versionDownloadShow.contains("${getStringX(R.string.app_ai_name)}${BuildConfig.VERSION_CODE}"))
    var isDownloadSate = false//是否下载中
    var pathName = ""
    var downName = ""
    val isDownload = NotNullMediatorLiveData(false)//是否下载完成


    fun getUserPics() {
        launchRequestOnIO({
            Repository.getUserPics("")
        }) {
            onSuccess = { bean ->
//                val oldList = GVM.INSTANT.swapSelectList.value?.toMutableList() ?: mutableListOf()
//                val newList = bean?.toMutableList() ?: mutableListOf()
//                val differenceList = newList.minus(oldList)
//
//                GVM.INSTANT.swapSelectList.value = newList
//
//                if (oldList.isNotEmpty() && differenceList.size == 1) {
//                    val selectNum = GVM.INSTANT.selectNum.value ?: return
//                    val tabList = GVM.INSTANT.swapTabList.value
//                    if (tabList != null && selectNum in tabList.indices) {
//                        val newBean = differenceList[0]
//                        tabList[selectNum].isSelectBean = newBean
//                        GVM.INSTANT.selectBean.value = newBean
//                        GVM.INSTANT.selectNum.value = selectNum
//                    }
//                }

                val oldList = GVM.INSTANT.swapSelectList.value?.toMutableList() ?: mutableListOf()
                val differenceList = bean?.toMutableList()?.minus(oldList) ?: mutableListOf()

                GVM.INSTANT.swapSelectList.value = bean?.toMutableList() ?: mutableListOf()
                if (oldList.isNotEmpty() && differenceList.size == 1) {//有新数据更新，默认选中
                    //当前选中素材的头像的下标
                    val selectNum = GVM.INSTANT.selectNum.value
                    //刷新素材头像对应的选中数据
                    GVM.INSTANT.swapTabList.value?.apply {
                        if (selectNum in indices) {
                            get(selectNum).isSelectBean = differenceList[0]
                        }
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

    fun retryDate() {
        loadFailed.postValue(false)
        mediaByBean.value = mediaListBean.value[curSelectedPosition / mediaSize]
        getUserPics()
    }

    fun getData(mediaId: String) {
        launchRequestOnIO({
            Repository.getMediaByID(mediaId)
        }) {
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
            onFailed = { throwable, _, errorMsg ->
                toast(errorMsg)
                loadFailed.postValue(true)
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

    fun getUserCollectUrl() {
        val mediaid = mediaByBean.value?.id
        launchRequestOnIO({
            Repository.getUserCollectMedia(mediaid)
        }) {
            onSuccess = {
                if (it != 0) {
                    collectStr = it.toString()
                    isCollect.value = true
                }
                mediaListBean.value.find { it.id == mediaid }
                    ?.let { bean ->
                        bean.collectId = it.toString()
                    }
            }
            onFailed = { _, _, errorMsg ->
                isCollect.value = false
                toast(errorMsg)
            }
        }
    }


    fun download(downloadUrl: String) {
        if (downloadUrl.isEmpty()) return
        showLoading("Downloading...\n0%", false)
        isDownloadSate=true
        val houType =downloadUrl.split(".").last()
        pathName =
            "${(System.currentTimeMillis() / 1000)}.$houType"
        downName = "download.$houType"

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