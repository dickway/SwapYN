package com.face.viewmodel.fragment

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.adapter.share.ShareZoneAdapter
import com.face.bean.AiFaceBean
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.postIfNot
import com.zzkj.structure.util.ktx.toIntOrZero
import com.zzkj.structure.util.singleToast

open class ShareZoneModel : BaseViewModel() {


    lateinit var typeList1: List<String>
    lateinit var typeList2: List<String>

    val isNoData = MutableLiveData(false)
    val loadFailed = MutableLiveData(false)
    val isNoMoreData = NotNullMutableLiveData(false)
    var isVipNew = GVM.INSTANT.isVip.value

    val isLoading = MutableLiveData<Boolean>()
    val finishLoadMore = MutableLiveData<Boolean>()

    val isHintVip = MutableLiveData(false)

    var page = 1
    var isHotMedia = true


    val isType1 = NotNullMutableLiveData(false)
    val isType2 = NotNullMutableLiveData(false)

    //选中文字
    var selectedName1 = NotNullMutableLiveData("")
    var selectedName2 = NotNullMutableLiveData("")

    //分享列表
    val shareZeList = MutableLiveData<List<AiFaceBean>>()
    var shareZoneAdapter: ShareZoneAdapter = ShareZoneAdapter()


    val fristShow = SPUtils.showPage.split("|").firstOrNull().toIntOrZero()
    val secondShow = SPUtils.showPage.split("|").getOrNull(1).toIntOrZero()

    /**
     * 重置数据
     */
    fun getShareRefresh() {
        page = 1
        getShare()
    }

    /**
     * 上拉加载
     */
    fun getShareLoad() {
        if (page < secondShow || GVM.INSTANT.isVip.value) {//
            page += 1
            getShare()
        } else {
            isHintVip.value = true
            finishLoadMore.value = true
            isLoading.value = false
        }
    }

    /**
     * 热门分享
     */
    fun getShare() {
        getDeviceRefresh()
        loadFailed.postIfNot(false)
        isNoMoreData.postIfNot(false)
        val videoType =
            if (selectedName2.value == typeList2.getOrNull(4)) "image" else selectedName2.value


        launchRequestOnIO({
            if (isHotMedia) {
                Repository.shareHotMedia(page, videoType)
            } else {
                Repository.getShareZoneList(page, 0, videoType)
            }
        }) {
            onSuccess = { it ->
                if (it == null || it.isEmpty()) {
                    isNoMoreData.postIfNot(true)
                }
                if (page == 1) {
                    if (it == null || it.isEmpty()) {
                        isNoData.postValue(true)
                    } else {
                        isNoData.postValue(false)
                    }
                    shareZeList.value = it?.toMutableList()
                } else {
                    if (page > fristShow) {
                        it?.forEachIndexed { p, sn ->
                            sn.isVip = GVM.INSTANT.isVip.value
                            if (!GVM.INSTANT.isVip.value) {
                                sn.isBlur = true
                            }
                        }
                    }
                    shareZeList.value =
                        (shareZeList.value?.toMutableList() ?: mutableListOf()).apply {
                            addAll(it?.toMutableList() ?: mutableListOf())
                        }
                }
            }

            onFailed = { _, _, m ->
                singleToast(m)
                if (page != 1) {
                    page -= 1
                } else {
                    loadFailed.postIfNot(true)
                }
            }

            onComplete = {
                finishLoadMore.value = true
                isLoading.value = false
            }
        }
    }

    fun getDeviceRefresh() {//刷新ab
        if (GVM.INSTANT.isShowTool.value == 0) {
            launchRequestOnIO({ Repository.getDeviceCampaign() }) {
                onSuccess = { bean ->
                    if (GVM.INSTANT.isShowTool.value != bean) {//切换账号后不一致就重启app
                        GVM.INSTANT.isEventA.postValue(-1)
                    }
                    GVM.INSTANT.isShowTool.postValue(bean ?: 0)
                    SPbaseUtils.loadAB = bean ?: 0
                }
            }
        }
    }
}