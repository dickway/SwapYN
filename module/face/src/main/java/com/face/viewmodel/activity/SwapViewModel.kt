package com.face.viewmodel.activity

import com.face.net.Repository
import com.face.R
import com.face.video.ZoomVideo
import com.flyjingfish.openimagelib.photoview.PhotoView
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.NotNullMediatorLiveData
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.toast

class SwapViewModel : BaseViewModel() {
    val isShow = NotNullMediatorLiveData(true)//数据是否加载完成
    var cacheView: ZoomVideo? = null//当前界面的播放布局
    var photoView: PhotoView? = null//当前界面的图片布局


    fun getReport(mediaId: String? = "", content: String = "", bolck: Int) {
        launchRequestOnIO({
            Repository.mediaBlack(mediaId ?: "", content, bolck)
        }) {
            onSuccess = {
                toast(getStringX(R.string.report_s))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

}