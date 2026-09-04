package com.face.video

import com.face.util.SPUtils

object VideoHeaderManager {
    val headers: MutableMap<String, String> by lazy {
        hashMapOf(
            "referer" to SPUtils.videoHeader
        )
    }
}
