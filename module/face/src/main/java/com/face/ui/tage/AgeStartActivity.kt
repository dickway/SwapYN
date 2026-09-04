package com.face.ui.tage

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.databinding.ActivityStartAgeBinding
import com.face.key.AiTaskType
import com.face.ui.FunZoneHistoryActivity
import com.face.ui.ToolGeneratingActivity
import com.face.ui.VipActivity
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.view.BaseContentDialog
import com.face.viewmodel.activity.AgeStartViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast

class AgeStartActivity : BaseBindingActivity<ActivityStartAgeBinding, AgeStartViewModel>(
    R.layout.activity_start_age,
    AgeStartViewModel::class.java
) {
    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            videoView.setVideoPath("android.resource://$packageName/${com.key.R.raw.year}")
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
            putString("taskType",  AiTaskType.CHANGE_AGE)
            putString("taskId", taskId)
        }
    }


    fun toRecord() {
        GVM.INSTANT.payPage.value = "Home_tool_age_record"
        if (!GVM.INSTANT.isVip.value&& !SPUtils.toolUse) {
            VipActivity.jump(this@AgeStartActivity)
            return
        }
        openActivity<FunZoneHistoryActivity> {
            putString("aiTaskType", AiTaskType.CHANGE_AGE)
        }
    }

    fun onstart() {
        GVM.INSTANT.payPage.value = "Home_tool_age"
        if (!GVM.INSTANT.isVip.value&& !SPUtils.toolUse) {
            VipActivity.jump(this@AgeStartActivity)
            return
        }
        mModel.getTask()
    }

    fun selectorPhoto() {
        SPUtils.isMaterial = false
        val language = when (SPbaseUtils.spLanguage) {
            "zh" -> LanguageConfig.CHINESE
            "zh-tw" -> LanguageConfig.TRADITIONAL_CHINESE
            "es" -> LanguageConfig.SPANISH
            "de" -> LanguageConfig.GERMANY
            "fr" -> LanguageConfig.FRANCE
            "sv" -> LanguageConfig.SV
            "ar" -> LanguageConfig.AR
            "ja" -> LanguageConfig.JAPAN
            "ko" -> LanguageConfig.KOREA
            "ku" -> LanguageConfig.KU
            "iw" -> LanguageConfig.IW
            "fa" -> LanguageConfig.FA
            "tr" -> LanguageConfig.TR
            "pt-br" -> LanguageConfig.PT_BR
            "pt-pt" -> LanguageConfig.PORTUGAL
            "it" -> LanguageConfig.IT
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
                override fun onResult(result: ArrayList<LocalMedia?>,fragment: Fragment) {
                    if (result.size > 0) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                fragment.openActivity<AgeAiActivity> {
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