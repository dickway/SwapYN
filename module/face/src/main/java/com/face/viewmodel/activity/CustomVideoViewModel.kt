package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.face.R
import com.face.net.Repository
import com.face.bean.AiFaceBean
import com.face.util.GVM
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.ktx.toIntOrZero
import com.zzkj.structure.util.toast

class CustomVideoViewModel : BaseViewModel() {

    //视频素材界面删除素材，列表是否为空
    var nullListSize = MutableLiveData(false)

    val loadFailed = NotNullMutableLiveData(false)
    val homeRefreshing = MutableLiveData(true)
    var customFaceList = MutableLiveData<List<AiFaceBean>>()
    var customList: MutableList<AiFaceBean>? = mutableListOf()
    val loadData = NotNullMutableLiveData(false)

    var param = ""
    var type = "video"
    var subNum = -1
    var subType = "20"

    fun getUserAiMedia() {
        loadFailed.setIfNot(false)
        launchRequestOnIO({ Repository.getUserAiMedia(type, param) }) {
            onSuccess = {
                loadData.postValue(true)
                customList = it?.toMutableList()
                userMediaNum()
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

    fun deleteUserAiMedia(media: AiFaceBean) {
        launchRequestWithLoadingOnIO({ Repository.deleteUserAiMedia(media.id) }) {
            onSuccess = {
                homeRefreshing.postValue(true)
                getUserAiMedia()
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun saveMediaShare(mediaId: String, result: () -> Unit) {
        launchRequestWithLoadingOnIO({ Repository.saveMediaShare(mediaId) }) {
            onSuccess = {
                homeRefreshing.value = true
                getUserAiMedia()
                result()
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun userMediaNum() {
        launchRequestOnIO({ Repository.userMediaNum() }) {
            onSuccess = { data ->
                when(subType){
                    "20"->{ subNum = data?.video20Num ?: 0 }
                    "180"->{ subNum = data?.video180Num ?: 0 }
                    "300"->{ subNum = data?.video300Num ?: 0 }
                    else ->  subNum = data?.imageNum ?: 0
                }
                LogUtils.e(">>>>>$subType>>>>$subNum")
                customFaceList.value =customList?.apply {
                    add(0, AiFaceBean(mediaType = type, subNum = subNum).apply {
                        itemType = AiFaceBean.ITEM_TYPE_TYPE
                    })
                }
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun payUserMediaNum(num: Int) {
        launchRequestOnIO({ Repository.payUserMediaNum(num, subType) }) {
            onSuccess = { data ->
                GVM.INSTANT.refreshUserInfo()
                subNum = data.toString().toIntOrZero()
                customFaceList.value = customFaceList.value?.toMutableList()?.apply {
                    if (isNotEmpty()) {
                        this[0] = this[0].copy(
                            subNum = subNum
                        ).apply {
                            itemType = AiFaceBean.ITEM_TYPE_TYPE
                        }
                    }
                }
                toast(getStringX(R.string.success))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }
}