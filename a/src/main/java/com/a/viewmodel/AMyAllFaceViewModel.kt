package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.face.R
import com.face.bean.MyFaceImgBean
import com.a.net.ARepository
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

    val myFaceList = MutableLiveData<List<MyFaceImgBean>>(emptyList())

    var faceList = mutableListOf<MyFaceImgBean>()

    fun getUserPics() {
        launchRequestOnIO({ ARepository.getUserPics() }) {
            onStart = { faceRefreshing.value = true }
            onSuccess = { listBean ->
                loadFailed.postValue(false)
                faceList = listBean?.toMutableList() ?: mutableListOf()
                publishFaces()
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
            onComplete = { faceRefreshing.value = false }
        }

    }


    fun deleteUserPic(bean: MyFaceImgBean?) {
        if (bean == null || bean.isSelect || bean.id.isBlank()) return
        launchRequestWithLoadingOnIO({ ARepository.deleteUserPic(bean.id) }) {
            onSuccess = {
                faceList.removeAll { it.id == bean.id }
                publishFaces()
                toast(getStringX(R.string.success))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    private fun publishFaces() {
        isNoData.setIfNot(faceList.isEmpty())
        isSelect.value = faceList.isNotEmpty()
        if (faceList.isEmpty()) isShowDete = false
        myFaceList.value = if (faceList.isEmpty()) emptyList() else
            listOf(MyFaceImgBean(isSelect = true)) + faceList
    }
}
