package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.bean.MyAllFaceBean
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.toast

class MyAllFaceViewModel : BaseViewModel() {
    val isNoData = NotNullMutableLiveData(false)

    val faceRefreshing = MutableLiveData<Boolean>()

    var tabTitles = SPUtils.tabTitles.toMutableList()

    var myFaceList = MutableLiveData<List<MyAllFaceBean>>(mutableListOf())

    fun getUserPics() {
        launchRequestOnIO({ Repository.getUserPics() }) {
            onStart = { faceRefreshing.value = true }
            onSuccess = { listBean ->
                //根据类型名字分组
                val groupByList = listBean?.toMutableList()?.groupBy {
                    it.type
                }

                isNoData.setIfNot(listBean?.isEmpty())
                if (!isNoData.value) {
                    val myList = mutableListOf<MyAllFaceBean>()
                    //默认类型将分组数据赋值
                    tabTitles.forEach {
                        val myAllFaceBean = MyAllFaceBean().apply {
                            name = it
                            listFace = groupByList?.get(it)?.toMutableList()
                        }
                        myList.add(myAllFaceBean)
                    }
                    //赋值刷新list
                    myFaceList.value = myList

                }
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
            onComplete = { faceRefreshing.value = false }
        }

    }

}