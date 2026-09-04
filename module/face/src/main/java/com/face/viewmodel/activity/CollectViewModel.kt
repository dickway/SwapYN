package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.adapter.collect.CollectFragmentAdapter
import com.face.bean.AiFaceBean
import com.face.net.Repository
import com.face.bean.CollectAMBean
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast

class CollectViewModel : BaseViewModel() {

    val isNoData = MutableLiveData(false)

    val collectAdapter = CollectFragmentAdapter()

    val homeRefreshing = MutableLiveData<Boolean>()

    val dataList = MutableLiveData<List<CollectAMBean>>()

    var type = NotNullMutableLiveData("Results")

    private var oldData = ""

    fun getUserCollect() {

        launchRequestOnIO({
            Repository.getUserCollect()
        }) {
            onStart = {
                homeRefreshing.value = true
            }

            onSuccess = { result ->
                val newData = MoshiHelper.convertObjectToJson(result)
                if (oldData != newData) {
                    oldData = newData
                    buildModules(result ?: emptyList())
                }
            }

            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }

            onComplete = {
                homeRefreshing.value = false
            }
        }
    }

    private fun buildModules(source: List<CollectAMBean>) {

        val isResult = type.value == "Results"

        val target =  mutableListOf<CollectAMBean>()

        var lastDay: String? = ""

        source.forEach { bean ->

            // 只处理当前类型
            val isMedia = bean.type == "media"

            if (isResult && isMedia) return@forEach
            if (!isResult && !isMedia) return@forEach

            val day = bean.getTime()

            // 新的一天 -> 插入 Header
            if (day != lastDay) {

                target.add(
                    CollectAMBean(timeDay = day).apply {
                        itemType = CollectAMBean.ITEM_TYPE_TIME
                    }
                )

                lastDay = day
            }

            // 插入内容 item（避免修改原数据）
            target.add(
                bean.copy().apply {
                    itemType = CollectAMBean.ITEM_TYPE_BEAN
                }
            )
        }

        dataList.value = target

        isNoData.value = target.isEmpty()
    }

}