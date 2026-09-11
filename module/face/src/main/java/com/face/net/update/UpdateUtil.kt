package com.face.net.update

import com.blankj.utilcode.util.LogUtils
import com.face.bean.Part
import com.face.net.Repository
import com.zzkj.structure.net.launchRequestOnIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File


object UpdateUtil {

    fun updateChunkFile(
        file: File,
        totalChunks: Int = 1,//总分片数量
        chunkSize: Int = 15 * 1024 * 1024,// 1MB分片
        listener: UpdateProgressListener,
    ) {
       GlobalScope.launchRequestOnIO({
            Repository.getPresignedUrlPart(
                file.name,
                totalChunks
            )
        }) {
            onSuccess = { aLink ->
                listener.onUrl(aLink)
                val semaphore = Semaphore(3)
                aLink?.parts?.map {
                      GlobalScope.launch(Dispatchers.IO) {
                        semaphore.withPermit {
                            updateFile(file, chunkSize, it.partNumber, it.url, listener)
                        }
                    }
                }
            }
            onFailed = { _, _, errorMsg ->
                listener.onFailed(-1, "", errorMsg)
            }
        }
    }

    suspend fun updateFile(
        file: File,
        maxByte: Int = 0,//上传分片限制
        partNumber: Int = 0,//上传分片id
        url: String,//上传分片url
        listener: UpdateProgressListener
    ) {
        // 上传当前分片
        val startByte: Long = ((partNumber - 1) * maxByte).toLong()
        var endByte: Long = startByte + maxByte
        if (endByte > file.length()) endByte = file.length()

        try {
            val progressRequestBody =
                ProgressRequestBody(startByte, endByte, file, listener)
            val result: Response<ResponseBody> =
                Repository.uploadChunkFile(url, progressRequestBody)
            if (result.isSuccessful) {
                // 4. 记录成功上传的分片
                listener.onFinish(partNumber, result.headers()["ETag"] ?: "")
            } else {
                listener.onFailed(partNumber, url, result.message())
            }
        } catch (e: Exception) {
            listener.onFailed(partNumber, url, e.message)
        }
    }

    fun retryChunkFile(
        listFailedPair: List<Part>, file: File,
        chunkSize: Int = 15 * 1024 * 1024,// 1MB分片
        listener: UpdateProgressListener,
    ) {
        val semaphore = Semaphore(3)
        listFailedPair.map { part ->
            GlobalScope.launch(Dispatchers.IO) {
                semaphore.withPermit {
                    updateFile(file, chunkSize, part.partNumber, part.url, listener)
                }
            }
        }
    }


    fun cancelRepository() {
    }
}
