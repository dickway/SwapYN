package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.a.adapter.AFacePageAdapter
import com.a.adapter.ATypeAdapter
import com.face.net.Repository
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.TimeUtil
import kotlinx.coroutines.flow.collectLatest

class APictureViewModel : BaseViewModel() {
    val isNoData = MutableLiveData<Boolean?>()
    val refreshError = MutableLiveData<Boolean?>()

    val faceAdapter = AFacePageAdapter()

    val typeAdapter = ATypeAdapter()

    var typeList2 = SPUtils.commonAuditTags.toMutableList().apply {
        add(0,"All")
    }

    //选中文字
    var selectedName1 = NotNullMutableLiveData("Latest")
    var selectedName2 =
        NotNullMutableLiveData(if (typeList2.isEmpty()) "" else typeList2.first())

    fun getPicture() {
        isNoData.value = null
        val isHotStr = if (selectedName1.value == "Latest") "new" else "hot"
        launch {
            Repository.getMediaByTag(isHotStr, "image", selectedName2.value)
                .cachedIn(viewModelScope)
                .collectLatest {
                    getDeviceRefresh()
                    faceAdapter.submitData(it)
                }
        }
    }

    fun getDeviceRefresh() {//刷新工具界面
        launchRequestOnIO({ Repository.getDeviceCampaign() }) {
            onSuccess = { bean ->
                GVM.INSTANT.aIsAB.postValue(bean)
                GVM.INSTANT.isShowTool.postValue(bean)
            }
        }
    }
}