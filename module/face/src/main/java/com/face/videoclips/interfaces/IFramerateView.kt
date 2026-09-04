package com.face.videoclips.interfaces

import com.face.bean.TargetImgBean

interface IFramerateView {
    fun onTimeOut(time: Int)
    fun onData(listData: List<TargetImgBean>)
}
