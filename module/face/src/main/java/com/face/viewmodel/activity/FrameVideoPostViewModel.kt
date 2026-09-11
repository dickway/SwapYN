package com.face.viewmodel.activity

import android.graphics.Bitmap
import com.blankj.utilcode.util.LogUtils
import com.face.net.Repository
import com.face.R
import com.face.bean.ALinkBean
import com.face.bean.Part
import com.face.bean.VideoLong2Bean
import com.face.bean.VideoLongBean
import com.face.net.update.UpdateProgressListener
import com.face.net.update.UpdateUtil
import com.face.util.FileUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.longToast
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import kotlin.math.ceil

class FrameVideoPostViewModel : BaseViewModel() {

    val loadFailed = NotNullMutableLiveData(0)

    var numProgress = NotNullMutableLiveData(1)//转圈进度条

    var jsonS2 = ""
    var postUrl = ""//提交链接
    var uploadVideoPath = ""//返回素材链接

    var repsBody: Job? = null
    var type = MoshiHelper.convertObjectToJson(VideoLong2Bean()) //素材类型
    var hw = ""//素材宽高
    var faceUrl = ""//视频替换头像
    var faceId = NotNullMutableLiveData("")//返回素材id

    var listPair = mutableListOf<Part>()
    var alinkBean: ALinkBean? = null
    var chunkSize: Int = 60 * 1024 * 1024 // 小于72m不分片
    var fileSize: Long = 1 // 文件大小
    var numChunk: Int = 1 // 25MB分片

    var postUrlImg = ""//提交封面链接
    var postBitmap: Bitmap? = null//提交图片

    /**
     * 分片上传
     * */
    fun postVideo() {
        val file = File(postUrl)
        fileSize = file.length()
        if (fileSize > 1024 * 1024 * 60) {//小于72m 不进行分片
            chunkSize = 12 * 1024 * 1024
            numChunk = ceil(fileSize / chunkSize.toDouble()).toInt()
        }
        UpdateUtil.updateChunkFile(file, numChunk, chunkSize, updateProgressListener)
    }

    /**
     * 分片上传后合并得到链接
     * */
    fun upload2S() {
        repsBody = launchRequestOnIO({
            Repository.completeUpload(jsonS2)
        }) {
            onSuccess = { bean ->
                numProgress.postValue(93)
                uploadVideoPath = bean?.urlName.toString()
                postPicture()
            }
            onFailed = { _, _, errorMsg ->
                longToast(errorMsg)
                loadFailed.postValue(1)
                cancelRepository()
            }
        }
    }

    fun postPicture() {
        if (postBitmap != null) {
            launch(Dispatchers.IO) {
                //压缩
                val file = FileUtil.compressImage(postBitmap)
                val body: MultipartBody.Part =
                    MultipartBody.Part.createFormData(
                        "file",
                        file.getName(),
                        file.asRequestBody(("application/otcet-stream").toMediaType())
                    )
                val timer = Timer()
                launchRequestOnIO({ Repository.uploadFile(body) }) {
                    onStart = {
                        dismissLoading()
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                if (numProgress.value < 97) numProgress.postValue(numProgress.value + 1)
                            }
                        }, Date(), 500)
                    }
                    onSuccess = {
                        numProgress.postValue(97)
                        postUrlImg = it?.src ?: ""
                        saveUserAiMedia()
                    }

                    onFailed = { _, _, errorMsg ->
                        loadFailed.postValue(3)
                    }
                    onComplete = {
                        timer.cancel()
                    }
                }
            }
        }
    }

    /**
     * 得到链接再次保存素材
     * */
    fun saveUserAiMedia() {
        if (faceUrl.isNotEmpty() && uploadVideoPath.isNotEmpty()) {
            val timer = Timer()
            launchRequestOnIO({
                Repository.saveUserAiMedia(
                    "video",
                    uploadVideoPath,
                    faceUrl.substring(1),
                    hw,
                    type,
                    postUrlImg
                )
            }) {
                onStart = {
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            if (numProgress.value < 100) numProgress.postValue(numProgress.value + 1)
                        }
                    }, Date(), 500)
                }
                onSuccess = { data ->
                    faceId.postValue(data.toString())
                }
                onFailed = { _, _, errorMsg ->
                    toast(errorMsg)
                    loadFailed.postValue(3)
                }
                onComplete = {
                    timer.cancel()
                }
            }
        }
    }

    //重试和请求回调
    val updateProgressListener = object : UpdateProgressListener {
        override fun onUrl(bean: ALinkBean?) {//获取分片上传的链接
            alinkBean = bean
        }

        override fun onFinish(partNumber: Int, etagStr: String) {//分片上传成功
            listPair.add(Part(partNumber, etag = etagStr))
            val progress = (listPair.size * 100 / numChunk)
            if (progress >= 92) {
                numProgress.postValue(91)
            } else {
                numProgress.postValue(progress)
            }
            if (listPair.size >= numChunk) {
                //判断合并是id是否有重复，有就重新上传
                val hasDuplicateName = listPair.map { it.partNumber }.let { names ->
                    names.size != names.toSet().size
                }
                if (hasDuplicateName){
                    loadFailed.postValue(1)
                }else{
                    alinkBean?.parts = listPair
                    jsonS2 = MoshiHelper.convertObjectToJson(alinkBean)
                    upload2S()
                }
            }
        }

        override fun onProgress(progress: Int) {
            if (progress >= 92) {
                numProgress.postValue(91)
            } else {
                numProgress.postValue(progress)
            }
        }

        override fun onFailed(partNumber: Int, url: String, errMsg: String?) {
            cancelRepository()
            if (partNumber == -1) {
                loadFailed.postValue(1)
            } else {
                loadFailed.postValue(2)
            }
        }
    }

    /**
     * 失败重新开始
     * */
    fun postRetry() {
        if (loadFailed.value == 1) {
            listPair = mutableListOf()
            numProgress.postValue(0)
            postVideo()
        } else if (loadFailed.value == 2) {
            //重试取出还没有完成的链接
            val aNotInB = alinkBean?.parts?.filter { itemA ->
                listPair.none { itemB -> itemA.partNumber == itemB.partNumber }
            } ?: mutableListOf()
            UpdateUtil.retryChunkFile(
                aNotInB,
                File(postUrl),
                chunkSize,
                updateProgressListener
            )
        } else if (loadFailed.value == 3) {
            postPicture()
        } else {
            saveUserAiMedia()
        }
        loadFailed.value = 0
    }


    /**
     * 取消请求
     * */
    fun cancelRepository() {
        UpdateUtil.cancelRepository()
        repsBody?.cancel()
        repsBody = null
    }
}