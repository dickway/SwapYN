package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.apkfuns.logutils.LogUtils
import com.face.net.Repository
import com.face.R
import com.face.adapter.tool.HistoryPaperworkAdapter
import com.face.bean.ToolTaskBean
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class PaperworkHistoryViewModel : BaseViewModel() {
    val isNoData = MutableLiveData(false)
    val loadFailed = MutableLiveData(false)
    val homeRefreshing = MutableLiveData<Boolean>()

    var aiType = ""

    var historyFaceList = MutableLiveData<List<ToolTaskBean>>(listOf())

    val historyAdapter: HistoryPaperworkAdapter = HistoryPaperworkAdapter()


    fun getUserRecordPage() {
        homeRefreshing.postValue(true)
        launch(Dispatchers.IO) {
            val recordIdcard = async { Repository.getUserRecordPage(aiType) }
            val aiTask = async { Repository.getUserAiTask(aiType) }
            if (recordIdcard.await().mIsSuccess &&
                aiTask.await().mIsSuccess
            ) {
                homeRefreshing.postValue(false)
                val mList = mutableListOf<ToolTaskBean>()

                //生成中和失败的
                val cList = mutableListOf<ToolTaskBean>().apply {
                    aiTask.await().mData?.forEach { taskBean ->
                        val toolTaskBean = ToolTaskBean()
                        toolTaskBean.taskId = taskBean.id
                        toolTaskBean.state = taskBean.state
                        toolTaskBean.type = taskBean.taskType
                        toolTaskBean.createTime = taskBean.createTime
                        toolTaskBean.errorMessage = taskBean.ee
                        toolTaskBean.recordUrl = taskBean.getUrl()
                        add(toolTaskBean)
                    }
                }
                val aBean: ToolTaskBean = ToolTaskBean().apply {
                    itemType = ToolTaskBean.ITEM_TYPE_HLIST
                    name = "task"
                    subList = cList
                }
                mList.add(aBean)

                val recordIdcardList = recordIdcard.await().mData
                //重新分类 并根据时间 分组
                var listCache1 = mutableListOf<ToolTaskBean>()
                recordIdcardList?.forEach {
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
                historyFaceList.postValue(mList.toMutableList())

                val size = cList.size + (recordIdcardList?.size ?: 0)
                isNoData.postValue(size < 1)

            } else {
                homeRefreshing.postValue(false)
                loadFailed.postValue(true)
            }
        }
    }


    fun deleteTask(bean: ToolTaskBean?) {
        if (bean?.state == 3) {
            launchRequestWithLoadingOnIO({ Repository.deleteUserMedia(bean.taskId) }) {
                onSuccess = {
                    getUserRecordPage()
                    toast(getStringX(R.string.success))
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        } else if (bean?.state == 2) {
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