package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.face.net.Repository
import com.face.R
import com.face.adapter.history.HistoryAdapter
import com.face.bean.TaskBean
import com.face.bean.TaskBean.Companion.ITEM_TYPE_HLIST
import com.face.bean.TaskBean.Companion.ITEM_TYPE_LIST
import com.face.bean.TaskBean.Companion.ITEM_TYPE_TEXT
import com.face.util.GVM
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast

class HistoryViewModel : BaseViewModel() {
    val isNoData = MutableLiveData<Boolean?>()
    val loadFailed = MutableLiveData<Boolean?>()
    val homeRefreshing = MutableLiveData<Boolean>()
    var mediaType = NotNullMutableLiveData("")
    var historyFaceList = MutableLiveData<List<TaskBean>>(listOf())

    val historyAdapter: HistoryAdapter = HistoryAdapter()
    var oldData = ""
    fun sendAiTask(refreshNum: Int = 0) {//自动刷新不需要展示加载圈
        if (refreshNum == 1) oldData = ""
        launchRequestOnIO({ Repository.getUserMedia(mediaType.value) }) {
            onStart = {
                isNoData.postValue(false)
                loadFailed.postValue(false)
            }
            onSuccess = { bean ->

                isNoData.postValue((bean?.size ?: 0) < 1)//有没有数据

                val newData = MoshiHelper.convertObjectToJson(bean)
                if (oldData != newData) {//数据有更新才刷新
                    oldData = newData
                    val mList = mutableListOf<TaskBean>()
                    mList.add(TaskBean(name = getStringX(R.string.history_txt)).apply {
                        itemType = ITEM_TYPE_TEXT
                    })
                    //重新分类 并根据时间 分组
                    var listCache1 = mutableListOf<TaskBean>()
                    val listCache2 = mutableListOf<TaskBean>()
                    bean?.toMutableList()?.forEach {
                        if (it.state == 2 || it.state == 3) {//0新建，1生成中，2完成,3失败
                            if (listCache1.size == 0) {
                                listCache1.add(it)
                            } else {
                                val lastBean = listCache1.last()
                                if (TimeUtil.isSameDay(it.createTime, lastBean.createTime)) {
                                    listCache1.add(it)
                                } else {
                                    val aBean: TaskBean = TaskBean().apply {
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
                        } else {
                            listCache2.add(it)
                        }
                    }

                    if (listCache2.size != 0) {
                        mList.firstOrNull()?.subList = listCache2//用于判断是否显示进行中标题
                        val bBean: TaskBean = TaskBean().apply {
                            id = listCache2.first().id
                            subList = listCache2
                            name = getStringX(R.string.production_title)
                            createTime = listCache2.first().createTime
                        }.apply {
                            itemType = ITEM_TYPE_HLIST
                        }
                        mList.add(1, bBean)
                    }

                    //最后一天有数据时，需要单独处理
                    if (listCache1.size != 0) {
                        val aBean2: TaskBean = TaskBean().apply {
                            id = listCache1.first().id
                            subList = listCache1
                            name = listCache1.first().getTime().toString()
                            createTime = listCache1.first().createTime
                        }.apply {
                            itemType = ITEM_TYPE_LIST
                        }
                        mList.add(aBean2)
                    }
                    historyFaceList.postValue(mList.toMutableList())
                }
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
            onComplete = {
                homeRefreshing.postValue(false)
            }
        }
    }

    fun deleteTask(bean: TaskBean?) {
        if (bean != null) {
            launchRequestWithLoadingOnIO({ Repository.deleteUserMedia(bean.id) }) {
                onSuccess = {
                    sendAiTask()
                    toast(getStringX(R.string.success))
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
            }
        }
    }

}