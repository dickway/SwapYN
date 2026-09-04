package com.face.ui.txtai

import android.os.Bundle
import com.apkfuns.logutils.LogUtils
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.databinding.ActivityStartTxtimgBinding
import com.face.key.AiTaskType
import com.face.ui.FunZoneHistoryActivity
import com.face.util.SPUtils
import com.face.video.CustomManager
import com.face.video.EmptyVideo
import com.face.viewmodel.activity.TxtImgStartViewModel
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.face.ui.BaseBindingActivity
import com.face.video.VideoHeaderManager
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity

class TxtImgStartActivity : BaseBindingActivity<ActivityStartTxtimgBinding, TxtImgStartViewModel>(
    R.layout.activity_start_txtimg,
    TxtImgStartViewModel::class.java
) {
    var tagVideo = ""
    val voideUrl = SPUtils.introductionVideo
        .split("|")
        .getOrNull(1)
        .takeIf { !it.isNullOrBlank() }
        ?: "https://d5z47om850v8h.cloudfront.net/admin/2025-05-12/txt2img3542346.mp4"

    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            tagVideo= mBinding?.videoView?.getVideoKey() ?: ""
            mBinding?.videoView?.loadBgImage1(com.key.R.mipmap.img_aiimage)
            GSYVideoOptionBuilder()
                .setIsTouchWiget(false)
                .setVideoTitle("")
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setPlayTag(EmptyVideo.Tag)
                .setLooping(true)
                .setCacheWithPlay(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setPlayPosition(0)
                .setMapHeadData(VideoHeaderManager.headers)
                .setUrl(voideUrl)
                .setVideoAllCallBack(object : GSYSampleCallBack() {
                    override fun onPlayError(url: String?, vararg objects: Any?) {
                        super.onPlayError(url, *objects)
                        mBinding?.videoView?.setUp( url,true, null,VideoHeaderManager.headers,"")
                        mBinding?.videoView?.startPlayLogic()
                    }
                })
                .build(mBinding?.videoView)
            mBinding?.videoView?.postDelayed({
                mBinding?.videoView?.startPlayLogic()
            },200)
        }

//        mModel.GeneratingId.observe(this) {
//            if (it == "") {//没有生成中任务可以走下一个
//
//            } else if (it == "-1") {//初始值不执行
//
//            } else {//有生成中任务
//                BaseContentDialog(
//                    getString(R.string.dialog_paper_only),
//                    getString(R.string.dialog_paper_only_txt),
//                    getString(R.string.cancel),
//                    getString(R.string.btn_record),
//                    false,
//                    onRightData = { toTask(it) })
//                    .showIgnoreState(this)
//            }
//        }

        GVM.INSTANT.hasAD.observe(this) {//广告拿到奖励，取消弹窗
            dismissLoading()
        }

    }

    fun toRecord() {
        openActivity<FunZoneHistoryActivity> {
            putString("aiTaskType", AiTaskType.TEXT2IMG)
        }
    }

    fun onstart() {
        openActivity<TxtImgActivity>()
    }

    override fun onPause() {
        super.onPause()
        mBinding?.videoView?.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        mBinding?.videoView?.onVideoResume()
    }

    override fun onDestroy() {
        if (tagVideo.isNotEmpty()) CustomManager.clearAllVideo(tagVideo)
        super.onDestroy()
    }
    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}