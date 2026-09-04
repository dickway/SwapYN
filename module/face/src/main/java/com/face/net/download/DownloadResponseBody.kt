package com.face.net.download

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer


class DownloadResponseBody(
    private val responseBody: ResponseBody,
    private val downloadListener: DownloadProgressListener?,
) :
    ResponseBody() {
    private var bufferedSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource as BufferedSource
    }

    private fun source(source: Source): Source {
        var totalBytesRead = 0L
        return object : ForwardingSource(source) {
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                if (null != downloadListener) {
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    val progress = (totalBytesRead * 100 / responseBody.contentLength()).toInt()
                    downloadListener.onProgress(progress)
                }
                return bytesRead
            }
        }
    }
}