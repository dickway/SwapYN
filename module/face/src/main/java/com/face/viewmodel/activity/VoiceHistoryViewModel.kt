package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.R
import com.face.adapter.history.HistoryVoiceAdapter
import com.face.bean.ToolTaskBean
import com.face.key.AiTaskType
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.postIfNot
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class VoiceHistoryViewModel : BaseViewModel() {
    val isNoData = MutableLiveData(false)
    val loadFailed = MutableLiveData<Boolean?>()
    val isColse = MutableLiveData(true)
    val isPlay = NotNullMutableLiveData(true)


    val currentTime = NotNullMutableLiveData(0)
    val homeRefreshing = MutableLiveData<Boolean>()
    val selectedToolTask = MutableLiveData<ToolTaskBean?>()

    val historyAdapter: HistoryVoiceAdapter = HistoryVoiceAdapter()
    var historyList = MutableLiveData<List<ToolTaskBean>?>()


    fun getUserRecordPage() {
        homeRefreshing.postValue(true)
        launch(Dispatchers.IO) {
            val recordVoice = async { Repository.getUserRecordPage(AiTaskType.VOICE_CLONING) }
            val aiTask = async { Repository.getUserAiTask(AiTaskType.VOICE_CLONING) }
            if (recordVoice.await().mIsSuccess &&
                aiTask.await().mIsSuccess
            ) {
                homeRefreshing.postValue(false)
                loadFailed.postIfNot(false)
                //生成中的
                val cList = mutableListOf<ToolTaskBean>().apply {
                    aiTask.await().mData?.forEach { taskBean ->
                        val toolTaskBean = ToolTaskBean()
                        toolTaskBean.taskId = taskBean.id
                        toolTaskBean.state = taskBean.state
                        toolTaskBean.createTime = taskBean.createTime
                        toolTaskBean.errorMessage = taskBean.ee
                        toolTaskBean.name = getStringX(R.string.generating)
                        add(toolTaskBean)
                    }
                }
                historyList.postValue(cList.apply {
                    recordVoice.await().mData?.toMutableList()?.let { addAll(it) }
                })
            } else {
                homeRefreshing.postValue(false)
                loadFailed.postIfNot(true)
            }
        }
    }


    fun deleteTask(bean: ToolTaskBean?) {
        if (bean != null) {
            launchRequestWithLoadingOnIO({ Repository.deleteUserMedia(bean.taskId) }) {
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

    fun deleteRecord(bean: ToolTaskBean?) {
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
                Repository.updataUserGenerateRecords(id = id, AiTaskType.VOICE_CLONING, name = name)
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