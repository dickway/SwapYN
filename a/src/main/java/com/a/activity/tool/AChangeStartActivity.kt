package com.a.activity.tool

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.a.activity.AVipActivity
import com.face.util.GVM
import com.a.BR
import com.a.R
import com.a.activity.AToolGeneratingActivity
import com.a.activity.AToolTaskHistoryActivity
import com.a.databinding.ActivityAtoolStartchangeBinding
import com.a.dialog.ABaseContentDialog
import com.a.viewmodel.AChangeStartViewModel
import com.face.key.AiTaskType
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast

class AChangeStartActivity :
    BaseBindingActivity<ActivityAtoolStartchangeBinding, AChangeStartViewModel>(
        R.layout.activity_atool_startchange,
        AChangeStartViewModel::class.java
    ) {
    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            videoView.setVideoPath("android.resource://$packageName/${com.key.R.raw.tool5}")
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
            if (it == "") {//没有生成中任务可以走下一个
                selectorPhoto()
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


    fun onstart() {
        GVM.INSTANT.payPage.value = "AAHome_tool_age"
        if (!GVM.INSTANT.isVip.value && !SPUtils.toolUse) {
            openActivity<AVipActivity>()
            return
        }
        mModel.getTask()
    }

    fun selectorPhoto() {
        GVM.INSTANT.payPage.value = "AHome_tool_age"
        if (!GVM.INSTANT.isVip.value && !SPUtils.toolUse) {
            openActivity<AVipActivity>()
            return
        }
        SPUtils.isMaterial = false
        val language = when (SPbaseUtils.spLanguage) {
            "zh" -> LanguageConfig.CHINESE
            "es" -> LanguageConfig.SPANISH
            "de" -> LanguageConfig.GERMANY
            "fr" -> LanguageConfig.FRANCE
            "sv" -> LanguageConfig.SV
            else -> LanguageConfig.ENGLISH
        }
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setLanguage(language)
            .isDisplayCamera(false)
            .isInfo(false)
            .isGif(false)
            .isWebp(false)
            .setMaxSelectNum(1)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>,frament: Fragment) {
                    if (result.isNotEmpty()) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                frament.openActivity<AChangeActivity> {
                                    putString("img_url", sandboxPath ?: availablePath)
                                }
                            } else {
                                toast("Image loading failed, please choose another image")
                            }
                        }
                    } else {
                        toast("Image loading failed, please choose another image")
                    }
                }

                override fun onCancel() {

                }

                override fun onCamera() {

                }

                override fun onHint(hoACT: FragmentActivity) {

                }
            })
    }


    fun toRecord() {
        GVM.INSTANT.payPage.value = "AHome_tool_age_record"
        if (!GVM.INSTANT.isVip.value && !SPUtils.toolUse) {
            openActivity<AVipActivity>()
            return
        }
        openActivity<AToolTaskHistoryActivity> {
            putString("aiTaskType", AiTaskType.CHANGE_AGE)
        }
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

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}