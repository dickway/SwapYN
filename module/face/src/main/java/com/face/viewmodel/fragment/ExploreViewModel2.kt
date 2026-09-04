package com.face.viewmodel.fragment

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.adapter.explore.Explore2Adapter
import com.face.bean.Explore2Bean
import com.face.util.GVM
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.singleToast
import com.zzkj.structure.util.toast

/**
 * @author 再战科技
 * @date 2023/10/16
 * @description
 */
class ExploreViewModel2 : BaseViewModel() {
    val loadFailed = MutableLiveData(false)
    val homeRefreshing = MutableLiveData(true)
    val finishLoadMore = MutableLiveData<Boolean>()

    val explores2 = NotNullMutableLiveData<List<Explore2Bean>>(listOf())
    var explore2Adapter: Explore2Adapter = Explore2Adapter()


    fun getData() {
        if (GVM.INSTANT.isShowTool.value != 0) {//渠道进入数据
            val nowTime = TimeUtil.getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd")
            launchRequestOnIO({
                Repository.getDateNewMedia(nowTime)
            }) {
                onStart = {
                    loadFailed.postValue(false)
                }
                onSuccess = { data ->
                    homeRefreshing.postValue(false)
                    explores2.postValue(data?.toMutableList() ?: mutableListOf())
                }
                onFailed = { _, _, errorMsg ->
                    homeRefreshing.postValue(false)
                    loadFailed.postValue(true)
                }

            }
        }
    }


    /**
     * 上拉加载更多数据
     * */
    fun getAiFace() {
        //最后一条数据的时间的前一天
        val time = TimeUtil.getString2Time(
            explores2.value.lastOrNull()?.date ?: ""
        ) - (24 * 60 * 60 * 1000)
        if (time < 1) return //时间为空就不查询了
        val dataTime = TimeUtil.getFormatDate(time, "yyyy-MM-dd")
        launchRequestOnIO({
            Repository.getDateNewMedia(dataTime)
        }) {
            onSuccess = { it ->
                finishLoadMore.value = true
                explores2.value = explores2.value.toMutableList().apply {
                    if (it != null) {
                        addAll(it)
                    }
                }
                if (it.isNullOrEmpty()) {
                    toast("no more data")
                }
            }

            onFailed = { _, _, m ->
                singleToast(m)
            }
        }
    }
}