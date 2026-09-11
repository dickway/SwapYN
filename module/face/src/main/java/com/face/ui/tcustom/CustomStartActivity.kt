package com.face.ui.tcustom

import android.content.Context
import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.R
import com.face.databinding.ActivityStartCustomBinding
import com.face.ui.BaseBindingActivity
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.video.CustomManager
import com.face.video.EmptyVideo
import com.face.video.VideoHeaderManager
import com.face.viewmodel.activity.CustomStartViewModel
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity


class CustomStartActivity : BaseBindingActivity<ActivityStartCustomBinding, CustomStartViewModel>(
    R.layout.activity_start_custom,
    CustomStartViewModel::class.java
) {
    val typeCustom by intentExtras("type_custom", "image")

    var videoView: EmptyVideo? = null

    val voideUrl = SPUtils.introductionVideo
        .split("|")
        .getOrNull(0)
        .takeIf { !it.isNullOrBlank() }
        ?: "https://d5z47om850v8h.cloudfront.net/admin/2025-05-12/custom2934556.mp4"

        companion object {
        fun jump(
            context: Context?,
            typeCustom: String = "",
        ) {
            context?.openActivity<CustomStartActivity> {
                putString("type_custom", typeCustom)
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        videoView = mBinding?.videoView
        videoView?.release()
        SPUtils.isMaterial = false
        if (GVM.INSTANT.isShowTool.value == 0) {
            if (GVM.INSTANT.isVip.value) {
                if (typeCustom == "image") {
                    openActivity<CustomPhotoActivity>()
                } else {
                    openActivity<CustomVideoActivity>()
                }
            } else {
                if (GVM.INSTANT.isShowTool.value == 0) {
                    GVM.INSTANT.payPage.value = "Home_tool0_$typeCustom"
                } else {
                    GVM.INSTANT.payPage.value = "Home_tool_$typeCustom"
                }
                VipActivity.jump(this@CustomStartActivity)
            }
            finish()
            return
        }

        mBinding?.apply {
            if (typeCustom == "image") {
                tvTitle.text = getString(R.string.frament_tool_pic_txt1)
                tvStart1.text = getString(R.string.frament_tool_pic_txt1)
                tvStart2.text = getString(R.string.frament_tool_pic_txt2)
            } else {
                tvTitle.text = getString(R.string.frament_tool_video_txt1)
                tvStart1.text = getString(R.string.frament_tool_video_txt1)
                tvStart2.text = getString(R.string.frament_tool_cutsom_t1)
            }
            playVideo(EmptyVideo.Tag)
        }
    }

    fun playVideo(tag: String) {
        LogUtils.e(">>>>>>>>$voideUrl")
        CustomManager.clearAllVideo(tag)
        videoView?.loadBgImage2(com.key.R.mipmap.img_custom_video)
        GSYVideoOptionBuilder()
            .setIsTouchWiget(false)
            .setVideoTitle("")
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setPlayTag(tag)
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
                    videoView?.setUp( url,true, null,VideoHeaderManager.headers,"")

                    videoView?.startPlayLogic()
                }
            })
            .build(videoView)
        videoView?.postDelayed({
            videoView?.startPlayLogic()
        }, 200)

//        videoView?.postDelayed({
//            LogUtils.e(">>>>>>>>>>>>执行了isPlaying:${videoView?.gsyVideoManager?.isPlaying}")
//            LogUtils.e(">>>>>>>>>>>>currentPositionWhenPlaying:${videoView?.currentPositionWhenPlaying}")
////            if (mCurrentState == -1){
////                LogUtils.e(">>>>>>>>>>>>执行了")
////                startPlayLogic()
////            }
//        }, 3000)

    }

    fun toStart() {
        if (GVM.INSTANT.isVip.value) {
            if (typeCustom == "image") {
                openActivity<CustomPhotoActivity>()
            } else {
                openActivity<CustomVideoActivity>()
            }
        } else {
            if (GVM.INSTANT.isShowTool.value == 0) {
                GVM.INSTANT.payPage.value = "Home_tool0_$typeCustom"
            } else {
                GVM.INSTANT.payPage.value = "Home_tool_$typeCustom"
            }
            VipActivity.jump(this@CustomStartActivity)
        }
    }

    override fun onPause() {
        super.onPause()
        videoView?.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        videoView?.onVideoResume()
        videoView?.postDelayed({
            // 播放器未激活或已销毁
            if (videoView?.isInPlayingState != true) {
                GSYVideoManager.releaseAllVideos()
                playVideo(EmptyVideo.Tag + 1)
            }
        }, 500)

        if (videoView == null) {
            finish()
        }
    }

    override fun onDestroy() {
        CustomManager.clearAllVideo(EmptyVideo.Tag)
        super.onDestroy()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}