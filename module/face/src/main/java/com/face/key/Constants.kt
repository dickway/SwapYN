package com.face.key

import com.zzkj.structure.base.BaseApp

/**
 * @author 再战科技
 * @date 2023/10/11
 * @description
 */
object Constants {

    //默认分页数据条数
    val pageSize = 40
    //opencv缓存路径
    val opencvPath = (BaseApp.INSTANCE.externalCacheDir ?: BaseApp.INSTANCE.cacheDir).path + "/face/opencv/"

    val clipsPath = (BaseApp.INSTANCE.externalCacheDir ?: BaseApp.INSTANCE.cacheDir).path + "/face/clips/"
    //图片缓存路径
    val savePath = (BaseApp.INSTANCE.externalCacheDir ?: BaseApp.INSTANCE.cacheDir).path + "/face/image/"
    //视频缓存路径
    val saveVideoPath = (BaseApp.INSTANCE.externalCacheDir ?: BaseApp.INSTANCE.cacheDir).path + "/face/video/"
    //音频缓存路径
    val voicePath = (BaseApp.INSTANCE.externalCacheDir ?: BaseApp.INSTANCE.cacheDir).path + "/face/voice/"

    //拍照选择图片的名字
    val saveImg = "CameraPhoto.png"
    //上传图片的名字
    val updateImg = "SavePhoto.png"
    //帧图片
    val frameImg = "FramePhoto.png"
}