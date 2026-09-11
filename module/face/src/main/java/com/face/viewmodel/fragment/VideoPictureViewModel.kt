package com.face.viewmodel.fragment

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.face.adapter.other.FaceAdapter
import com.face.net.Repository
import com.face.bean.AiFaceBean
import com.face.key.Constants
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.postIfNot
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.singleToast
import com.zzkj.structure.util.toast
import kotlinx.coroutines.delay

class VideoPictureViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)
    val homeRefreshing = MutableLiveData<Boolean>()
    val finishLoadMore = MutableLiveData<Boolean>()

    //数据加载完
    private var dataAll = false
    private var homePage = 1

    val videoList = NotNullMutableLiveData<List<AiFaceBean>>(listOf())
    var videoListAdapter: FaceAdapter = FaceAdapter()

    val showView = MutableLiveData(false)

    val isType1 = NotNullMutableLiveData(false)
    val isType2 = NotNullMutableLiveData(false)

    val isNoData = MutableLiveData(false)
    var typeList1 = NotNullMutableLiveData(listOf("Latest", "Popular"))//传值需要 "new" "hot"
    var typeList2 = NotNullMutableLiveData(SPUtils.typeVideoTags)

    var selectedType = ""

    //选中文字
    var selectedName1 = NotNullMutableLiveData("Latest")
    var selectedName2 =
        NotNullMutableLiveData(if (typeList2.value.isEmpty()) "" else typeList2.value.first())


    fun getData() {
        homeRefreshing.value=true
        getDeviceRefresh()
        isNoData.postValue(false)
        loadFailed.postIfNot(false)
        launchRequestOnIO({
            Repository.getMediaByTagHome(
                selectedName1.value, selectedType, selectedName2.value, homePage
            )
        }) {
            onSuccess = { it ->
                videoListAdapter =
                    FaceAdapter(if (selectedName2.value == "All") "" else selectedName2.value)
                videoList.value = it?.toMutableList() ?: mutableListOf()
                if (it.isNullOrEmpty()) {
                    isNoData.postValue(true)
                }
            }
            onFailed = { _, _, m ->
                loadFailed.postIfNot(true)
            }

            onComplete = {
                dataAll = false
                homePage = 1
                launch {
                    delay(1000)
                    homeRefreshing.postValue(false)
                }
            }
        }
    }


    fun getAiFace() {
        homePage += 1
        launchRequestOnIO({
            Repository.getMediaByTagHome(
                selectedName1.value, selectedType, selectedName2.value, homePage
            )
        }) {

            onSuccess = { it ->
                videoList.value = videoList.value.toMutableList().apply {
                    //重新加载时重置数据
                    if (it != null && !dataAll) {
                        addAll(it)
                    }
                }

                if (!it.isNullOrEmpty() && !dataAll) {
                    dataAll = it.size < Constants.pageSize//当前页不满分页条数表示加载完
                } else {
                    toast("no more data")
                }
            }

            onFailed = { _, _, m ->
                homePage -= 1
                singleToast(m)
            }

            onComplete = {
                finishLoadMore.value = true
            }
        }
    }


    fun getDeviceRefresh() {//刷新工具界面
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