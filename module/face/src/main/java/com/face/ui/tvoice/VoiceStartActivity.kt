package com.face.ui.tvoice

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.face.BR
import com.face.R
import com.face.databinding.ActivityStartVoiceBinding
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.viewmodel.activity.VoiceStartViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity


class VoiceStartActivity : BaseBindingActivity<ActivityStartVoiceBinding, VoiceStartViewModel>(
    R.layout.activity_start_voice,
    VoiceStartViewModel::class.java
) {
    override fun init(savedInstanceState: Bundle?) {
        SPUtils.isMaterial = false
        mBinding?.apply {
            videoView.setVideoPath("android.resource://$packageName/${com.key.R.raw.voice}")
            videoView.seekTo(10)
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.start()
                mp.setOnInfoListener { mp, what, extra ->
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        videoView.setBackgroundColor(Color.TRANSPARENT);
                        imgView.visibility = View.INVISIBLE
                    }
                    true
                }

            }
        }


    }


    fun toRecord() {
        GVM.INSTANT.payPage.value = "Home_tool_voice"
        if (!GVM.INSTANT.isVip.value) {
            VipActivity.jump(this@VoiceStartActivity)
            return
        }
        openActivity<VoiceHistoryActivity>()
    }

    fun onstart() {
        GVM.INSTANT.payPage.value = "Home_tool_voice"
        if (!GVM.INSTANT.isVip.value) {
            VipActivity.jump(this@VoiceStartActivity)
            return
        }
        openActivity<VoiceMaterialActivity>()
    }

    override fun onResume() {
        super.onResume()
        mBinding?.imgView?.visibility = View.VISIBLE
        object : CountDownTimer(300, 100) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                mBinding?.imgView?.visibility = View.INVISIBLE
            }
        }.start()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}