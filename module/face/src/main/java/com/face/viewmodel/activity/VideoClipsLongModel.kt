package com.face.viewmodel.activity

import android.util.Log
import com.apkfuns.logutils.LogUtils
import com.face.bean.ALinkBean
import com.face.bean.Part
import com.face.net.Repository
import com.face.net.update.UpdateProgressListener
import com.face.net.update.UpdateUtil
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.moshi.MoshiHelper
import java.io.File
import kotlin.math.ceil

class VideoClipsLongModel : BaseViewModel() {
//    var listFailedPair = mutableListOf<Part>()
//    var listPair = mutableListOf<Part>()
//    var alinkBean: ALinkBean? = null
//    var chunkSize: Int = 60 * 1024 * 1024 // 小于72m不分片
//    var fileSize: Long = 1 // 文件大小
//    var numChunk: Int = 1 // 25MB分片
//    fun postVideo(postUrl: String) {
//        val file = File(postUrl)
//        fileSize = file.length()
//        if (fileSize > 1024 * 1024 * 60) {//小于72m 不进行分片
//            chunkSize = 12 * 1024 * 1024
//            numChunk = ceil(fileSize / chunkSize.toDouble()).toInt()
//        }
//        UpdateUtil.updateChunkFile(file, numChunk, chunkSize, object : UpdateProgressListener {
//            override fun onUrl(bean: ALinkBean?) {//获取分片上传的链接
//                alinkBean = bean
//            }
//
//            override fun onFinish(partNumber: Int, etagStr: String) {//分片上传成功
//                listPair.add(Part(partNumber, etag = etagStr))
//                val num = (listPair.size* 100 /numChunk)
//                if (num >= 100) {
//                    LogUtils.e(">>>>>>>>>上传完成:$alinkBean")
//                    alinkBean?.parts = listPair
//                    upload2S(MoshiHelper.convertObjectToJson(alinkBean))
//                } else {
//                    LogUtils.e(">>>>>>>onFinish:$num %  etagStr：${listPair.size}/$numChunk")
//                }
//            }
//
//            override fun onProgress(progress: Int) {
//                LogUtils.e(">>>>>>>>>onProgress:$progress %")
//            }
//
//            override fun onFailed(partNumber: Int,url: String, errMsg: String?) {
//                listFailedPair.add(Part(partNumber, url = url))
//                LogUtils.e(">>>>>>>>>>>>onFailed$partNumber:$errMsg")
//            }
//        })
//    }
//
//    fun upload2S(json: String) {
//        launchRequestOnIO({
//            Repository.completeUpload(json)
//        }) {
//            onSuccess = { bean ->
//                LogUtils.e(">>>>>>>>>>>>合并成功：${bean}")
//            }
//            onFailed = { _, _, errorMsg ->
//                LogUtils.e(">>>>>>>>>>>>合并失败：$errorMsg")
//            }
//        }
//    }
}