package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.adapter.other.CashAdapter
import com.face.adapter.other.GiftAdapter
import com.face.bean.AiFaceBean
import com.face.bean.CaskBean
import com.face.bean.GiftBean
import com.face.bean.StyleBean
import com.face.bean.TagConfigBean.Companion.toMap
import com.face.net.Repository
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseApp
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast

class GiftListViewModel : BaseViewModel() {

    var giftAdapter = GiftAdapter()

    val isNoData = MutableLiveData(false)

    val loadFailed = MutableLiveData(false)

    val giftList = MutableLiveData<List<GiftBean>>()
    fun getGifts() {
        loadFailed.postValue(false)
        launchRequestWithLoadingOnIO({ Repository.getGift("exchange_config") }) {
            onSuccess = { listBean->

                isNoData.postValue(listBean.isNullOrEmpty())
                giftList.postValue(listBean ?: mutableListOf())

//                val styleStr = it?.toMap()?.get("style") ?: ""
//                if (styleStr != "") {
//                    val jsonStr = MoshiHelper.listAdapter(StyleBean::class.java).fromJson(styleStr)
//

//                }
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }
}