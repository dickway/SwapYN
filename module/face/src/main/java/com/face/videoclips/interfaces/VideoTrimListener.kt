package com.face.videoclips.interfaces

interface VideoTrimListener {
    fun onStartTrim()
    fun onFinishTrim(url: String?, duration: Long)
    fun onError(errorMsg: String?)
    fun onCancel()
}
