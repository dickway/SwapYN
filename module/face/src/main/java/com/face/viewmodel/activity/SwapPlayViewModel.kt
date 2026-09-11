package com.face.viewmodel.activity

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
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

class SwapPlayViewModel : BaseViewModel() {

    var taskContent = ""
    var groupId = ""

    val loadFailed = MutableLiveData(false)
    var usePinot = NotNullMutableLiveData(SPUtils.playAiPinot)
    var mediaByBean = MutableLiveData<MediaByBean>()
    var mediaList = MutableLiveData<List<MediaByBean>>()

    val taskBean = MediatorLiveData<TaskBean?>()

    var isDownloadSate = false//是否下载中
    var pathName = ""
    var downName = ""
    val isDownload = NotNullMediatorLiveData(false)//是否下载完成

    fun getData() {
        launchRequestWithLoadingOnIO({
            Repository.getMediaByGroup(groupId)
        }) {
            onSuccess = { data ->
                mediaByBean.value = data?.toMutableList()?.firstOrNull {
                    it.tag == "idleAction"
                }

                val list = data?.toMutableList()
                list?.removeIf {
                    it.tag == "idleAction"
                }
                mediaList.postValue(list)
            }
            onFailed = { _, code, errorMsg ->
                toast(errorMsg)
                loadFailed.postValue(true)
            }
        }
    }

    fun retryDate() {
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


    fun sendAiTask(type: String, sources: String) {
        launchRequestWithLoadingOnIO({
            Repository.sendGroupAiTask(
                type,
                mediaByBean.value?.getGroupId(),
                sources
            )
        }) {
            onStart = {
                loadFailed.postValue(false)
            }
            onSuccess = { bean ->
                taskBean.postValue(bean)
                EventUtil.taskSend(mediaByBean.value?.id, type, sources)
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }

}