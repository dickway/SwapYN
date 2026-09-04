package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
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

class CustomPhotoViewModel : BaseViewModel() {

    val loadFailed = NotNullMutableLiveData(false)
    val homeRefreshing = MutableLiveData(true)
    var customFaceList = MutableLiveData<List<AiFaceBean>>(mutableListOf())
    var customList: MutableList<AiFaceBean>? = mutableListOf()
    val loadData = NotNullMutableLiveData(false)
    var subNum=-1

    fun getUserAiMedia() {
        homeRefreshing.postValue(true)
        loadFailed.setIfNot(false)
        launchRequestOnIO({ Repository.getUserAiMedia("image") }) {
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
            onStart = {
            }
            onSuccess = {
                getUserAiMedia()
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }


    fun userMediaNum() {
        launchRequestOnIO({ Repository.userMediaNum() }) {
            onSuccess = { data ->
                subNum=data?.imageNum?:0
                customFaceList.value = customList?.apply {
                    add(0, AiFaceBean(mediaType = "image",subNum = subNum).apply {
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
        launchRequestOnIO({ Repository.payUserMediaNum(num,"image") }) {
            onSuccess = { data ->
                GVM.INSTANT.refreshUserInfo()
                subNum=data.toString().toIntOrZero()
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