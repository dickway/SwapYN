package com.face.ui.fragment

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.face.BR
import com.face.R
import com.face.databinding.FragmentTool1Binding
import com.face.ui.paperwork.PaperworkStartActivity
import com.face.ui.tclear.ClearStartActivity
import com.face.ui.tcustom.CustomStartActivity
import com.face.ui.tcutout.CutoutStartActivity
import com.face.ui.trestoration.RestorationStartActivity
import com.face.ui.tstyle.StyleStartActivity
import com.face.ui.tvoice.VoiceStartActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.MainToolDialog
import com.face.viewmodel.fragment.ToolViewModel
import com.zzkj.structure.base.BaseApp
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import kotlinx.coroutines.delay

class ToolFragment1 : BaseBindingFragment<FragmentTool1Binding, ToolViewModel>(
    R.layout.fragment_tool1, ToolViewModel::class.java
) {
    private var retryCount = 0
    private val maxRetries = 3 // 最多尝试 3 次

    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            if (GVM.INSTANT.isShowTool.value==0) {
                mBinding?.pictureView?.visibility = View.GONE
                mBinding?.videoView?.visibility = View.GONE
            }

            videoClear.setVideoPath("android.resource://${context?.packageName}/${com.key.R.raw.clear1}")
            videoClear.seekTo(10)
            videoClear.setOnErrorListener { mp, what, extra ->
                imgClare.visibility = View.VISIBLE
                  // 如果还没有超过最大重试次数
                if (retryCount < maxRetries) {
                    retryCount++
                    launch {
                        delay(1000)
                        videoClear.setVideoPath("android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.clear1}")
                    }
                }
                true
            }
            videoClear.setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.start()
                mp.setOnInfoListener { mp, what, extra ->
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        videoClear.setBackgroundColor(Color.TRANSPARENT)
                        imgClare.visibility = View.INVISIBLE
                    }
                    true
                }
            }
        }

//        mBinding?.apply {
//            if (GVM.INSTANT.isShowTool.value != 0) {
//                pictureView.visibility=View.
//            } else {
//
//            }
//        }

    }

    fun onHint(type: String) {
        if (GVM.INSTANT.checkingLogin(requireActivity())) {
            if (type == "image") {
                if (SPUtils.toolPicHint) {
                    MainToolDialog(type).showIgnoreState(activity)
                } else {
                    CustomStartActivity.jump(mActivity,type)
                }
            } else {
                if (SPUtils.toolVideoHint) {
                    MainToolDialog(type).showIgnoreState(activity)
                } else {
                    CustomStartActivity.jump(mActivity,type)
                }
            }
        }
    }

    fun onAiClear() {
        openActivity<ClearStartActivity>()
    }

    fun onCutout() {
        openActivity<CutoutStartActivity>()
    }

    fun onPaperwork() {
        openActivity<PaperworkStartActivity>()
    }


    fun onRestoration() {
        openActivity<RestorationStartActivity>()
    }

    fun onStyle() {
        openActivity<StyleStartActivity>()
    }

    fun onAiVoice() {
        openActivity<VoiceStartActivity>()
    }

    override fun onResume() {
        super.onResume()
        mBinding?.videoClear?.isFocusable = false;
        mBinding?.imgClare?.visibility = View.VISIBLE
        object : CountDownTimer(300, 100) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                mBinding?.imgClare?.visibility = View.INVISIBLE
            }
        }.start()
    }


    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this).addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}