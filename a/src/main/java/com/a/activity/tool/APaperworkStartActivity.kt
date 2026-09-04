package com.a.activity.tool

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.a.activity.AToolTaskHistoryActivity
import com.a.activity.AVipActivity
import com.a.databinding.ActivityAtoolStartpaperworkBinding
import com.a.dialog.ABaseContentDialog
import com.a.viewmodel.APaperworkStartViewModel
import com.face.util.GVM
import com.a.BR
import com.a.activity.AToolGeneratingActivity
import com.face.ad.AdUtil
import com.face.util.SPUtils
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity

class APaperworkStartActivity :
    BaseBindingActivity<ActivityAtoolStartpaperworkBinding, APaperworkStartViewModel>(
        com.a.R.layout.activity_atool_startpaperwork,
        APaperworkStartViewModel::class.java
    ) {
    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            videoView.setVideoPath("android.resource://$packageName/${com.key.R.raw.tool2}")
            videoView.seekTo(10)
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.start()
                mp.setOnInfoListener { mp, what, extra ->
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        videoView.setBackgroundColor(Color.TRANSPARENT)
                        imgView.visibility = View.INVISIBLE
                    }
                    true
                }
            }
        }


        mModel.GeneratingId.observe(this) {
            if (it == "") {//没有生成中任务可以进入生成界面
                openActivity<APaperworkActivity>()
            } else if (it == "-1") {//初始值不执行

            } else {//有生成中任务
                ABaseContentDialog(
                    "There is already a task being generated",
                    "Only one task can be performed at a time",
                    "Cancel",
                    "Go to View",
                    onCancelData = { toTask(it) })
                    .showIgnoreState(this)
            }
        }
    }

    fun toTask(taskId: String) {
        openActivity<AToolGeneratingActivity> {
            putString("taskId", taskId)
        }
    }

    fun toRecord() {
        GVM.INSTANT.payPage.value = "AHome_tool_paper_record"
        if (!GVM.INSTANT.isVip.value && !SPUtils.toolUse) {
            openActivity<AVipActivity>()
            return
        }
        openActivity<AToolTaskHistoryActivity>()
    }


    fun onstart() {
        GVM.INSTANT.payPage.value = "AHome_tool_paper"
        if (!GVM.INSTANT.isVip.value && !SPUtils.toolUse) {
            openActivity<AVipActivity>()
            return
        }
        mModel.getTask()
    }


    override fun onResume() {
        super.onResume()
        SPUtils.isMaterial = false
        mBinding?.imgView?.visibility = View.VISIBLE
        object : CountDownTimer(300, 100) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                mBinding?.imgView?.visibility = View.INVISIBLE
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        GVM.INSTANT.hasAD.postValue(false)
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}