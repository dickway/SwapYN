package com.face.ui.paperwork

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.ad.AdUtil
import com.face.databinding.ActivityStartPaperworkBinding
import com.face.key.AiTaskType
import com.face.ui.FunZoneHistoryActivity
import com.face.ui.ToolGeneratingActivity
import com.face.ui.VipActivity
import com.face.util.SPUtils
import com.face.view.BaseContentDialog
import com.face.viewmodel.activity.PaperworkStartViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity

class PaperworkStartActivity :
    BaseBindingActivity<ActivityStartPaperworkBinding, PaperworkStartViewModel>(
        R.layout.activity_start_paperwork,
        PaperworkStartViewModel::class.java
    ) {
    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            videoView.setVideoPath("android.resource://$packageName/${com.key.R.raw.idphoto}")
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
            if (it == "") {//没有生成中任务可以走广告流程
                toStart()
            } else if (it == "-1") {//初始值不执行

            } else {//有生成中任务
                BaseContentDialog(
                    getString(R.string.dialog_paper_only),
                    getString(R.string.dialog_paper_only_txt),
                    getString(R.string.cancel),
                    getString(R.string.btn_record),
                    false,
                    onRightData = { toTask(it) })
                    .showIgnoreState(this)
            }
        }
    }

    fun toTask(taskId: String) {
        openActivity<ToolGeneratingActivity> {
            putString("taskId", taskId)
            putString("taskType", AiTaskType.ID_CARD )
        }
    }

    fun toRecord() {
        GVM.INSTANT.payPage.value = "Home_tool_paper"
        if (!GVM.INSTANT.isVip.value&& !SPUtils.toolUse) {
            VipActivity.jump(this@PaperworkStartActivity)
            return
        }
        openActivity<FunZoneHistoryActivity>()
    }

    fun toStart() {
        openActivity<PaperworkAiActivity>()
    }


    fun onstart() {
        GVM.INSTANT.payPage.value = "Home_tool_paper"
        if (!GVM.INSTANT.isVip.value&& !SPUtils.toolUse) {
            VipActivity.jump( this@PaperworkStartActivity)
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