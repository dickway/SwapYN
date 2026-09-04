package com.face.net.update

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File


class ProgressRequestBody(
    private val startByte: Long,
    private val endByte: Long,
    private val file: File,
    private val listener: UpdateProgressListener
) : RequestBody() {


    override fun contentType(): MediaType? = "application/octet-stream".toMediaTypeOrNull()

    override fun contentLength(): Long = endByte - startByte


    override fun writeTo(sink: BufferedSink) {
//        file.inputStream().use { inputStream ->
//            inputStream.skip(startByte)
//            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
//            var uploaded = 0L
//            var read: Int
//            while ((inputStream.read(buffer).also { read = it }) != -1) {
//                if (uploaded + read > contentLength()) {
//                    read = (contentLength() - uploaded).toInt()
//                }
//                sink.write(buffer, 0, read)
//                uploaded += read
//                if (file.length() < SING_SIZE) notifyProgress(uploaded)
//                if (uploaded >= contentLength()) break
//            }
//        }

        file.inputStream().use { inputStream ->
            inputStream.skip(startByte)
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var uploaded = 0L
            var read: Int
            var lastProgress = -1 // 记录上次通知的进度百分比

            while ((inputStream.read(buffer).also { read = it }) != -1) {
                if (uploaded + read > contentLength()) {
                    read = (contentLength() - uploaded).toInt()
                }
                sink.write(buffer, 0, read)
                uploaded += read

                // 计算当前进度百分比
                val currentProgress = (uploaded * 100 / contentLength()).toInt()

                // 只有当进度变化时才通知（避免频繁通知相同的进度）
                if (currentProgress > lastProgress) {
                    lastProgress = currentProgress
                    if (file.length() < SING_SIZE) notifyProgress(currentProgress)
                }

                if (uploaded >= contentLength()) break
            }

            // 确保最后通知100%
            if (file.length() < SING_SIZE && lastProgress < 100) {
                notifyProgress(100)
            }
        }
    }

    private fun notifyProgress(Progress: Int) {
        listener.onProgress(Progress)
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 4096
        private const val SING_SIZE = 1024 * 1024 * 60
    }
}