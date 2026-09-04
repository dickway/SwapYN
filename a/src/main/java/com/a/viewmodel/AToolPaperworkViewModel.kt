package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.a.R
import com.a.adapter.AColorAdapter
import com.a.adapter.ASizeAdapter
import com.a.adapter.AUserAdapter
import com.face.net.Repository
import com.face.bean.ColorBean
import com.face.bean.SizeBean
import com.face.key.AiTaskType
import com.face.util.EventUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.toast

class AToolPaperworkViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)
    val style = MutableLiveData(0)
    val userAdapter: AUserAdapter = AUserAdapter()
    val colorAdapter: AColorAdapter = AColorAdapter()
    val sizeAdapter: ASizeAdapter = ASizeAdapter()

    var postImgUrl = MutableLiveData("")
    var postStyle = MutableLiveData("")
    var postColor = MutableLiveData("")
    var postSize = MutableLiveData("")

    var taskId = MutableLiveData("")

    val enableBtn = NotNullMediatorLiveData(false)

    val colorList: List<ColorBean> =
        mutableListOf(
            ColorBean("#FFFFFF", "White"),
            ColorBean("#D3D3D3", "Grey"),
            ColorBean("#438EDB", "Blue"),
            ColorBean("#FF0000", "Red")
        )

    val sizeList = NotNullMutableLiveData(
        listOf(
            SizeBean("", "", "22 x 32 mm", 11, 16, "260*378", R.mipmap.a_icon_size),
            SizeBean("", "", "25 x 35 mm", 5, 7, "295*413",  R.mipmap.a_icon_size1),
            SizeBean("", "", "26 x 32 mm", 13, 16, "307*378",  R.mipmap.a_icon_size2),
            SizeBean("", "", "30 x 40 mm", 3, 4, "354*472",  R.mipmap.a_icon_size3),
            SizeBean("", "", "35 x 49 mm", 11, 16, "389*567", R.mipmap.a_icon_size4),
            SizeBean("", "", "35 x 45 mm", 7, 9, "413*531",  R.mipmap.a_icon_size5),
            SizeBean("", "", "51 x 51 mm", 1, 1, "602*602",  R.mipmap.a_icon_size6)
        )
    )

    private val enableBtnObserver = Observer<String?> {
        enableBtn.value = postImgUrl.value?.isNotEmpty() == true
                && postStyle.value?.isNotEmpty() == true
                && postColor.value?.isNotEmpty() == true
                && postSize.value?.isNotEmpty() == true
    }

    init {
        enableBtn.addSource(postImgUrl, enableBtnObserver)
        enableBtn.addSource(postStyle, enableBtnObserver)
        enableBtn.addSource(postColor, enableBtnObserver)
        enableBtn.addSource(postSize, enableBtnObserver)
    }


    fun getUserPics() {
        colorAdapter.submitList(colorList)
        launchRequestOnIO({
            Repository.getUserPics("")
        }) {
            onStart = {
                loadFailed.postValue(false)
            }
            onSuccess = { bean ->
                userAdapter.submitList(bean?.toMutableList() ?: mutableListOf())
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }


    fun sendAiTask() {
        val sources =
            "${postImgUrl.value}|${postStyle.value}|${postColor.value?.lowercase()}|${postSize.value}"

        launchRequestWithLoadingOnIO({
            Repository.sendAiTask(
                AiTaskType.ID_CARD,
                "",
                sources
            )
        }) {
            onStart = {
                loadFailed.postValue(false)
            }
            onSuccess = { bean ->
                EventUtil.taskSend(bean?.id, AiTaskType.ID_CARD, sources)
                taskId.postValue(bean?.id)
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }


}