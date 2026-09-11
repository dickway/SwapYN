package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.adapter.ATaskHistoryAdapter
import com.blankj.utilcode.util.LogUtils
import com.a.net.ARepository
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

class AToolTaskHistoryViewModel : BaseViewModel() {
    val isNoData = MutableLiveData(false)
    val loadFailed = MutableLiveData(false)
    val homeRefreshing = MutableLiveData<Boolean>()

    var aiType = ""

    var historyFaceList = MutableLiveData<List<ToolTaskBean>>(listOf())

    val historyAdapter: ATaskHistoryAdapter = ATaskHistoryAdapter()


    fun getUserRecordPage() {
        homeRefreshing.postValue(true)
        launch(Dispatchers.IO) {
            val recordIdcard = async { ARepository.getUserRecordPage(aiType) }
            val aiTask = async { ARepository.getUserAiTask(aiType) }
            if (recordIdcard.await().mIsSuccess &&
                aiTask.await().mIsSuccess
            ) {
                homeRefreshing.postValue(false)
                val mList = mutableListOf<ToolTaskBean>()

                //生成中和失败的
                aiTask.await().mData?.forEach { taskBean ->
                    val toolTaskBean = ToolTaskBean( )
                    toolTaskBean.itemType = ToolTaskBean.ITEM_TYPE_HLIST
                    toolTaskBean.taskId = taskBean.id
                    toolTaskBean.state = taskBean.state
                    toolTaskBean.type = taskBean.taskType
                    toolTaskBean.createTime = taskBean.createTime
                    toolTaskBean.errorMessage = taskBean.ee
                    toolTaskBean.recordUrl = taskBean.getUrl()
                    mList.add(toolTaskBean)
                }

                val recordIdcardList = recordIdcard.await().mData?: mutableListOf()
                //重新分类 并根据时间 分组
                mList.addAll(recordIdcardList)

                historyFaceList.postValue(mList.toMutableList())

                isNoData.postValue(mList.size < 1)

            } else {
                homeRefreshing.postValue(false)
                loadFailed.postValue(true)
            }
        }
    }


    fun deleteTask(bean: ToolTaskBean?) {
        if (bean?.state == 3) {
            launchRequestWithLoadingOnIO({ ARepository.deleteUserMedia(bean.taskId) }) {
                onSuccess = {
                    getUserRecordPage()
                    toast(getStringX(R.string.success))
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        } else if (bean?.state == 2) {
            launchRequestWithLoadingOnIO({ ARepository.deleteUserRecord(bean.id) }) {
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
                ARepository.updataUserGenerateRecords(id = id, aiType, name = name)
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
