package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.adapter.other.TargetPicAdapter
import com.face.bean.TargetBean
import com.face.bean.TargetImgBean
import com.face.key.AiTaskType
import com.face.util.FileUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class TargetPicModel : BaseViewModel() {

    val targetPicAdapter = TargetPicAdapter()

    //视频素材
    var videoImgList = MutableLiveData(mutableListOf<TargetImgBean>())

    //视频素材
    var videoList = mutableListOf<TargetImgBean>()

    //返回的人脸
    var faceList = mutableListOf<TargetBean>()

    val isSelect = NotNullMutableLiveData(false)

    var imgNetwork = ""

    var beanId = ""

    var repsAuto = NotNullMutableLiveData(0)
    var isAct = false
    fun uploadPicture() {
        launch(Dispatchers.IO) {
            //压缩
            val file =
                FileUtil.compressImage(videoImgList.value?.get(targetPicAdapter.selectIndex)?.mBitmap)
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "file",
                    file.getName(),
                    file.asRequestBody(("application/otcet-stream").toMediaType())
                )
            launchRequestOnIO({ Repository.uploadFile(body) }) {
                onSuccess = {
                    imgNetwork = it?.src.toString()
                    sendAiTask(it?.src.toString())
                }
                onFailed = { _, _, errorMsg ->
                    repsAuto.postValue(3)
                    toast(errorMsg)
                }
            }
        }
    }

    fun sendAiTask(uploadImg: String) {
        launchRequestOnIO({ Repository.sendAiTask(AiTaskType.FACE_ANALYSE, "", uploadImg) }) {
            onSuccess = { bean ->
                beanId = bean?.id.toString()
                repsAuto.postValue(0)
                queryAiTask()
            }
            onFailed = { _, _, errorMsg ->
                repsAuto.postValue(3)
                toast(errorMsg)
            }
        }
    }

    fun queryAiTask() {
        if (beanId != "") {

            launchRequestOnIO({
                //循环请求,延迟一秒请求
                Repository.queryAiTask(beanId)
            }) {

                onSuccess = { bean ->//1上传中，2检查中，3完成，4失败
                    faceList.clear()
                    when (bean?.state) {
                        2 -> {//完成
                            bean.result.faceUrl.forEach { str ->
                                if (str != "") {
                                    faceList.add(TargetBean(path = str))
                                }
                            }
                            repsAuto.postValue(1)
                        }

                        3 -> {//失败
                            repsAuto.postValue(3)
                            toast(bean.ee)
                        }
                    }
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                }
                onComplete = {
                    launch(Dispatchers.IO) {
                        delay(1000)
                        if (repsAuto.value == 0) {
                            queryAiTask()
                        }
                    }
                }
            }
        }
    }
}
