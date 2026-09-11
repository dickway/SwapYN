package com.a.viewmodel

import androidx.lifecycle.MutableLiveData
import com.a.adapter.ASwapAdapter
import com.face.bean.Face
import com.face.bean.MediaByBean
import com.face.bean.MyFaceImgBean
import com.face.bean.TaskBean
import com.face.bean.UseTypeBean
import com.face.bean.VideoAipointBean
import com.face.key.AiTaskType
import com.a.net.ARepository
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast

class ASwapViewModel : BaseViewModel() {

    private var videoAipoints =
        MoshiHelper.adapter(VideoAipointBean::class.java).fromJson(SPUtils.videoAipoints)
            ?: VideoAipointBean()

    var swapAdapter: ASwapAdapter = ASwapAdapter()
    var isShow = MutableLiveData(false)

    val loadFailed = MutableLiveData(false)
    val isCollect = NotNullMutableLiveData(false)//是否收藏
    var myFaceList = mutableListOf<MyFaceImgBean>()
    var mediaByBean = MutableLiveData<MediaByBean>()
    val taskBean = MutableLiveData<TaskBean?>()
    var swapList = MutableLiveData<List<Face>>(mutableListOf())//获取的图片能换头像
    var usePinot = NotNullMutableLiveData(100)
    var isAIPoint = NotNullMutableLiveData(false)//是否使用积分

    var myFaceImgBean: MyFaceImgBean? = null

    var mediaId = ""
    var position = 0//记录点击的第几个要换的face图片
    var collectStr = ""//收藏collect_id
    val sources = NotNullMutableLiveData(false)//是否可点击按钮
    val hasMultipleFaces = NotNullMutableLiveData(false)

    fun setSwapFaces(faces: List<Face>) {
        val previous = swapList.value.orEmpty()
        swapList.value = faces.mapIndexed { index, face ->
            face.copy().apply {
                isSelectBean = previous.getOrNull(index)
                    ?.takeIf { it.faceId == face.faceId }?.isSelectBean
            }
        }
        position = position.coerceIn(0, (faces.size - 1).coerceAtLeast(0))
        val multipleFaces = faces.size > 1
        if (hasMultipleFaces.value != multipleFaces) isShow.value = false
        hasMultipleFaces.value = multipleFaces
        swapAdapter.canDisableFace = multipleFaces
        submitFaceOptions()
    }

    fun selectTargetFace(index: Int) {
        if (index !in swapList.value.orEmpty().indices) return
        position = index
        updateSelectionState()
    }

    fun selectFace(face: MyFaceImgBean?): Boolean {
        val faces = swapList.value.orEmpty()
        val target = faces.getOrNull(position) ?: return false
        if (face == null || (face.isSelect && faces.size <= 1)) return false
        if (!face.isSelect && face.pic.isBlank()) return false
        target.isSelectBean = face.takeUnless { it.isSelect }
        swapList.value = faces.toList()
        updateSelectionState()
        return true
    }

    private fun updateSelectionState() {
        val faces = swapList.value.orEmpty()
        sources.value = faces.any { !it.isSelectBean?.pic.isNullOrBlank() }
        myFaceImgBean = faces.getOrNull(position)?.isSelectBean
        swapAdapter.selectIndex = myFaceImgBean?.let { selected ->
            myFaceList.indexOfFirst { !it.isSelect && it.pic == selected.pic }
        } ?: -1
    }


    private fun submitFaceOptions() {
        myFaceList = myFaceList.filterNot { it.isSelect }.toMutableList().apply {
            if (hasMultipleFaces.value) add(0, MyFaceImgBean(isSelect = true))
        }
        updateSelectionState()
        swapAdapter.submitList(myFaceList) { updateSelectionState() }
    }

    fun getData() {
        loadFailed.postValue(false)
        launchRequestWithLoadingOnIO({
            ARepository.getMediaByID(mediaId)
        }) {
            onSuccess = {
                if (it != null) {
                    mediaByBean.value = it

                    if (it.param.contains("300")) {
                        isAIPoint.value = true
                        usePinot.postValue(videoAipoints.type5)
                    } else if (it.param.contains("180")) {
                        isAIPoint.value = true
                        usePinot.postValue(videoAipoints.type3)
                    } else if (it.param.contains("20")) {
                        isAIPoint.value = false
                    } else {
                        isAIPoint.value = false
                    }
                }
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
                loadFailed.postValue(true)
            }
        }
    }


    fun getReport(mediaId: String? = "", content: String = "", bolck: Int) {
        launchRequestOnIO({
            ARepository.mediaBlack(mediaId ?: "", content, bolck)
        }) {
            onSuccess = { bean ->
                toast("Report successful, thank you for your feedback.")
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun getUserPics() {
        launchRequestOnIO({
            ARepository.getUserPics("")
        }) {
            onSuccess = { bean ->
                if (bean != null) {
                    myFaceList = bean.toMutableList()
                    // Refresh assignments too, so a deleted face cannot leave the button enabled.
                    swapList.value = swapList.value.orEmpty().map { face ->
                        face.apply {
                            isSelectBean = isSelectBean?.let { selected ->
                                bean.firstOrNull { !it.isSelect && it.pic == selected.pic }
                            }
                        }
                    }
                    submitFaceOptions()
                }
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }


    fun sendAiTask(type: String, sources: String) {
        launchRequestWithLoadingOnIO({
            ARepository.sendAiTask(
                type,
                mediaByBean.value?.id,
                sources
            )
        }) {
            onStart = {
                loadFailed.postValue(false)
            }
            onSuccess = { bean ->
                val num1 = GVM.INSTANT.userInfo.value.mediaNum//用户生成次数
                if (num1 >= SPUtils.freeNum.toInt()) {//大于免费生成次数才开始计算当日生成
                    SPUtils.nowNum += 1
                }
                if (GVM.INSTANT.userInfo.value.isVip()) {
                    SPUtils.vipUserCount = SPUtils.vipUserCount.onEach {
                        if (it.id == GVM.INSTANT.userInfo.value.userId) {
                            it.countUse++
                        }
                    }
                }
                onUseID(bean)
                EventUtil.taskSend(mediaByBean.value?.id, AiTaskType.FACESWAP, sources)
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }

    fun onUseID(taskB: TaskBean?) {
        if (isAIPoint.value) {
            SPUtils.useNum += usePinot.value
            SPUtils.useType = UseTypeBean(type = "SwapFace", task_id = taskB?.id ?: "")
            launchRequestOnIO({
                ARepository.subUserTFLOPS(
                    usePinot.value, type = MoshiHelper.convertObjectToJson(SPUtils.useType)
                )
            }) {
                onSuccess = {
                    SPUtils.useNum = 0
                    SPUtils.useType = UseTypeBean(type = "SwapFace", task_id = taskB?.id ?: "")
                    GVM.INSTANT.refreshUserInfo()
                }
                onComplete = {
                    taskBean.value = taskB
                }
            }
        } else {
            taskBean.value = taskB
        }
    }
}
