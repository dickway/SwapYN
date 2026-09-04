package com.face.net.download

import java.io.File

interface DownloadProgressListener {
    fun onFinish(file: File?)
    fun onProgress(progress: Int)
    fun onFailed(errMsg: String?)
}