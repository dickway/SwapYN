package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.R
import com.face.adapter.other.ColorAdapter
import com.face.adapter.other.SizeAdapter
import com.face.adapter.other.UserAdapter
import com.face.bean.ColorBean
import com.face.bean.SizeBean
import com.face.key.AiTaskType
import com.face.util.EventUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.toast

class PaperworkAiViewModel : BaseViewModel() {
    val loadFailed = MutableLiveData(false)
    val style = MutableLiveData(0)
    val userAdapter: UserAdapter = UserAdapter()
    val colorAdapter: ColorAdapter = ColorAdapter()
    val sizeAdapter: SizeAdapter = SizeAdapter()

    var postImgUrl = MutableLiveData("")
    var postStyle = MutableLiveData("")
    var postColor = MutableLiveData("")
    var postSize = MutableLiveData("")

    var taskId = MutableLiveData("")

    val colorList: List<ColorBean> =
        mutableListOf(
            ColorBean("#FFFFFF", "White"),
            ColorBean("#D3D3D3", "Grey"),
            ColorBean("#438EDB", "Blue"),
            ColorBean("#FF0000", "Red")
        )

    val sizeList = NotNullMutableLiveData(
        listOf(
            SizeBean("", "", "22 x 32 mm", 11, 16, "260*378", com.key.R.mipmap.img_icon_size_w),
            SizeBean("", "", "25 x 35 mm", 5, 7, "295*413", com.key.R.mipmap.img_icon_size_w1),
            SizeBean("", "", "26 x 32 mm", 13, 16, "307*378", com.key.R.mipmap.img_icon_size_w2),
            SizeBean("", "", "30 x 40 mm", 3, 4, "354*472", com.key.R.mipmap.img_icon_size_w3),
            SizeBean("", "", "35 x 49 mm", 11, 16, "389*567", com.key.R.mipmap.img_icon_size_w4),
            SizeBean("", "", "35 x 45 mm", 7, 9, "413*531", com.key.R.mipmap.img_icon_size_w5),
            SizeBean("", "", "51 x 51 mm", 1, 1, "602*602", com.key.R.mipmap.img_icon_size_w6)
        )
    )


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