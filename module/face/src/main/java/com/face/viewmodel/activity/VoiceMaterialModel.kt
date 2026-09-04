package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.R
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

class VoiceMaterialModel : BaseViewModel() {
    var voiceList = MutableLiveData<List<ToolTaskBean>>(mutableListOf())
    val loadData = NotNullMutableLiveData(false)
    val loadFailed = NotNullMutableLiveData(false)
    val homeRefreshing = MutableLiveData<Boolean>()
    var showSave = NotNullMutableLiveData(false)
    var toolTaskBean = ToolTaskBean()
    var Generating = ToolTaskBean()
    fun getUserRecordPage() {
        homeRefreshing.postValue(true)
        launch(Dispatchers.IO) {
            val recordVoice = async { Repository.getUserRecordPage(AiTaskType.VOICE_ANALYSE) }
            val aiTask = async { Repository.getUserAiTask(AiTaskType.VOICE_ANALYSE) }
            if (recordVoice.await().mIsSuccess &&
                aiTask.await().mIsSuccess
            ) {
                loadData.postIfNot(true)
                homeRefreshing.postValue(false)
                loadFailed.postIfNot(false)
                //生成中的
                var isGenerat = false
                val cList = mutableListOf<ToolTaskBean>().apply {
                    aiTask.await().mData?.forEach { taskBean ->
                        val toolTaskBean = ToolTaskBean()
                        toolTaskBean.taskId = taskBean.id
                        toolTaskBean.state = taskBean.state
                        toolTaskBean.createTime = taskBean.createTime
                        toolTaskBean.errorMessage = taskBean.ee
                        toolTaskBean.name = getStringX(R.string.ai_sound)
                        add(toolTaskBean)
                        if (taskBean.state == 1 || taskBean.state == 0) {
                            isGenerat = true
                            Generating = toolTaskBean
                        }
                    }
                }
                if (!isGenerat) {
                    Generating = ToolTaskBean()
                }

                voiceList.postValue(cList.apply {
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
                Repository.updataUserGenerateRecords(id = id, AiTaskType.VOICE_ANALYSE, name = name)
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


    var GeneratingId = MutableLiveData("-1")
    fun getTask() {
        if (toolTaskBean.id != "") {
            GeneratingId.value = "-1"
            launchRequestWithLoadingOnIO({
                Repository.getUserAiTask(AiTaskType.VOICE_CLONING)
            }) {
                onStart = {
                }
                onSuccess = { bean ->
                    //是否有生成中的
                    var taskId = ""
                    bean?.forEach { taskBean ->
                        if (taskBean.state == 1 || taskBean.state == 0) {
                            taskId = taskBean.id
                        }
                    }
                    GeneratingId.postValue(taskId)
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        }
    }
}