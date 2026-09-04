package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.face.R
import com.face.bean.MyFaceImgBean
import com.face.net.Repository
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.toast

class AMyAllFaceViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)

    val isNoData = NotNullMutableLiveData(false)

    val isSelect = NotNullMutableLiveData(false)

    var isShowDete = false

    val faceRefreshing = MutableLiveData<Boolean>()

    var myFaceList = MutableLiveData<List<MyFaceImgBean>?>()

    var faceList = mutableListOf<MyFaceImgBean>()

    fun getUserPics() {
        launchRequestOnIO({ Repository.getUserPics() }) {
            onStart = { faceRefreshing.value = true }
            onSuccess = { listBean ->
                loadFailed.postValue(false)
                isNoData.setIfNot(listBean?.isEmpty())
                faceList = listBean?.toMutableList() ?: mutableListOf()
                if (faceList.size > 0) {
                    myFaceList.postValue(listBean?.toMutableList()?.apply {
                        add(0, MyFaceImgBean(isSelect = true))
                    })
                }
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
            onComplete = { faceRefreshing.value = false }
        }

    }


    fun deleteUserPic(bean: MyFaceImgBean?) {
        launchRequestWithLoadingOnIO({ Repository.deleteUserPic(bean?.id) }) {
            onSuccess = {
                faceList.remove(bean)
                myFaceList.value = myFaceList.value?.toMutableList()?.apply {
                    remove(bean)
                }
                toast(getStringX(R.string.success))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }
}