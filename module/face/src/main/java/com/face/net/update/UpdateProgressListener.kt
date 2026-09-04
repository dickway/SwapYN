package com.face.net.update

import com.face.bean.ALinkBean

interface UpdateProgressListener {
    fun onFinish(index: Int, file: String)
    fun onProgress(progress: Int)
    fun onFailed(index: Int, url: String, errMsg: String?)
    fun onUrl(alink: ALinkBean?)
}