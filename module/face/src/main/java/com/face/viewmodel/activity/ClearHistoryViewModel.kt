package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.R
import com.face.adapter.history.HistoryClearAdapter
import com.face.bean.ToolTaskBean
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.toast

class ClearHistoryViewModel : BaseViewModel() {
    val isNoData = MutableLiveData(false)
    val loadFailed = MutableLiveData(false)
    val homeRefreshing = MutableLiveData<Boolean>()

    var historyFaceList = MutableLiveData<List<ToolTaskBean>>(listOf())

    val historyAdapter: HistoryClearAdapter = HistoryClearAdapter()

    var aiType=""

    fun getUserRecordPage() {
        homeRefreshing.postValue(true)
        launchRequestOnIO({
            Repository.getUserRecordPage(aiType)
        }) {
            onSuccess = { bean ->
                val mList = mutableListOf<ToolTaskBean>()
                //重新分类 并根据时间 分组
                var listCache1 = mutableListOf<ToolTaskBean>()
                bean?.toMutableList()?.forEach {
                    if (listCache1.size == 0) {
                        listCache1.add(it)
                    } else {
                        val lastBean = listCache1.last()
                        if (TimeUtil.isSameDay(it.createTime, lastBean.createTime)) {
                            listCache1.add(it)
                        } else {
                            val aBean: ToolTaskBean = ToolTaskBean().apply {
                                id = lastBean.id
                                name = lastBean.getTime().toString()
                                subList = listCache1
                                createTime = lastBean.createTime
                            }
                            mList.add(aBean)
                            listCache1 = mutableListOf()
                            listCache1.add(it)
                        }
                    }
                }

                //最后一天有数据时，需要单独处理
                if (listCache1.size != 0) {
                    val aBean2: ToolTaskBean = ToolTaskBean().apply {
                        id = listCache1.first().id
                        subList = listCache1
                        name = listCache1.first().getTime().toString()
                        createTime = listCache1.first().createTime
                    }.apply {
                        itemType = ToolTaskBean.ITEM_TYPE_LIST
                    }
                    mList.add(aBean2)
                }

                historyFaceList.value = mList.toMutableList()
                isNoData.setIfNot(mList.size == 0)
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.setIfNot(true)
                toast(errorMsg)
            }
            onComplete = {
                homeRefreshing.postValue(false)
            }
        }
    }

    fun deleteTask(bean: ToolTaskBean?) {
        if (bean != null) {
            launchRequestWithLoadingOnIO({ Repository.deleteUserRecord(bean.id) }) {
                onSuccess = {
                    getUserRecordPage()
                    toast(getStringX(R.string.success))
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        }
    }

    fun updataUserGenerateRecords(id: String?, name: String?) {
        if (id != "") {
            launchRequestWithLoadingOnIO({
                Repository.updataUserGenerateRecords(id = id, aiType, name = name)
            }) {
                onSuccess = { it ->
                    getUserRecordPage()
                    toast(getStringX(R.string.success))
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        }
    }
}