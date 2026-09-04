package com.face.viewmodel.fragment

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.adapter.history.MeHistoryHAdapter
import com.face.bean.TaskBean
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast

class MineViewModel : BaseViewModel() {

    val meHistoryHAdapter = MeHistoryHAdapter()

    var historyList = MutableLiveData<List<TaskBean>>(listOf())


    fun sendAiTask() {//自动刷新不需要展示加载圈
        launchRequestOnIO({ Repository.getUserMedia("All") }) {
            onSuccess = { bean ->
                val mList = mutableListOf<TaskBean>()
                var numSort = 0
                bean?.toMutableList()?.forEach {
                    if (it.state == 1 || it.state == 0) {
                        mList.add(numSort, it)
                        numSort++
                    } else {
                        mList.add(it)
                    }
                }
                historyList.value = mList.toMutableList()
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun onUserAds() {//重复扣取积分失败的情况
        if (SPUtils.useNum > 0) {
            launchRequestOnIO({ Repository.subUserTFLOPS(
                SPUtils.useNum, MoshiHelper.convertObjectToJson(
                    SPUtils.useType)) }) {
                onSuccess = {
                    SPUtils.useNum = 0
                    GVM.INSTANT.refreshUserInfo()
                }
            }
        }
    }
}