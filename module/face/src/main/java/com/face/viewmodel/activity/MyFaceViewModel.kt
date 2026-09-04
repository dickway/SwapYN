package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.R
import com.face.bean.MyFaceImgBean
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast

class MyFaceViewModel : BaseViewModel() {

    val loadFailed = NotNullMutableLiveData(false)

    var myFaceList = MutableLiveData<MutableList<MyFaceImgBean>>(mutableListOf())

    var showDelete = NotNullMutableLiveData(true)

    var typeName = NotNullMutableLiveData("")
    fun getUserPics() {
        launchRequestWithLoadingOnIO({ Repository.getUserPics(typeName.value) }) {
            onSuccess = { bean ->
                if (!bean.isNullOrEmpty()) {
                    myFaceList.value = bean.toMutableList()
                }
                loadFailed.setIfNot(false)
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
                loadFailed.setIfNot(true)
            }
        }

    }

    fun deleteUserPic(bean: MyFaceImgBean) {
        launchRequestWithLoadingOnIO({ Repository.deleteUserPic(bean.id) }) {
            onSuccess = {
                myFaceList.value?.remove(bean)
                myFaceList.value = myFaceList.value
                toast(getStringX(R.string.success))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun updateUserPicSort(listSort: List<MyFaceImgBean>) {
        val jsonStr = MoshiHelper.listAdapter(MyFaceImgBean::class.java).toJson(listSort)
        launchRequestWithLoadingOnIO({ Repository.updateUserPicSort(jsonStr) }) {
            onSuccess = {
                myFaceList.value = myFaceList.value
                toast(getStringX(R.string.success))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }
}