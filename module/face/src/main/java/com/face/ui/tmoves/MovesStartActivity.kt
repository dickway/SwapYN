package com.face.ui.tmoves

import android.os.Bundle
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.databinding.ActivityStartMovesBinding
import com.face.key.AiTaskType
import com.face.ui.FunZoneHistoryActivity
import com.face.util.SPUtils
import com.face.video.CustomManager
import com.face.video.EmptyVideo
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.face.ui.BaseBindingActivity
import com.face.ui.txtai.TxtImgActivity
import com.face.video.VideoHeaderManager
import com.face.viewmodel.activity.NullViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity

class MovesStartActivity : BaseBindingActivity<ActivityStartMovesBinding, NullViewModel>(
    R.layout.activity_start_moves,
    NullViewModel::class.java
) {
    var tagVideo = ""
    val voideUrl = SPUtils.introductionVideo
        .split("|")
        .getOrNull(5)
        .takeIf { !it.isNullOrBlank() }
        ?: "https://d5z47om850v8h.cloudfront.net/admin/2026-03-24/moves.mp4"

    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            tagVideo= mBinding?.videoView?.getVideoKey() ?: ""
            mBinding?.videoView?.loadBgImage4("https://d5z47om850v8h.cloudfront.net/admin/2026-03-26/a_img_moves.png")
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
    }

    fun toRecord() {
        openActivity<FunZoneHistoryActivity> {
            putString("aiTaskType", AiTaskType.IMG2VIDEO)
        }
    }

    fun onstart() {
        openActivity<MovesListActivity>()
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