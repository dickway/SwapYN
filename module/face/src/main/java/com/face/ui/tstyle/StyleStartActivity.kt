package com.face.ui.tstyle

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
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
import com.face.databinding.ActivityStartStyleBinding
import com.face.key.AiTaskType
import com.face.ui.ToolPicHistoryActivity
import com.face.ui.VipActivity
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.viewmodel.activity.StyleStartViewModel
import com.zzkj.structure.base.BaseApp
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast

class StyleStartActivity : BaseBindingActivity<ActivityStartStyleBinding, StyleStartViewModel>(
    R.layout.activity_start_style,
    StyleStartViewModel::class.java
) {
    override fun init(savedInstanceState: Bundle?) {
        SPUtils.isMaterial = false
        mModel.getStyle()
        mModel.styleAdapter.onItemClick = { _, bean, _ ->
            mModel.selectStyleBean = bean
            mBinding?.apply {
                videoView.setVideoPath(bean?.video)
                videoView.seekTo(10)
            }
        }


        mBinding?.apply {
            videoView.setVideoPath("android.resource://${BaseApp.INSTANCE.packageName}/${com.key.R.raw.style1}")
            videoView.seekTo(10)
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.start()
                mp.setOnInfoListener { mp, what, extra ->
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        videoView.setBackgroundColor(Color.TRANSPARENT)
                    }
                    true
                }
            }
        }
    }

    fun toRecord() {
        GVM.INSTANT.payPage.value = "Home_tool_cartoon"
        if (!GVM.INSTANT.isVip.value&& !SPUtils.toolUse) {
            VipActivity.jump(this@StyleStartActivity)
            return
        }
        openActivity<ToolPicHistoryActivity> {
            putString("aiTaskType", AiTaskType.IMG2CARTOON)
        }
    }

    fun selectorPhoto() {
        GVM.INSTANT.payPage.value = "Home_tool_cartoon"
        if (!GVM.INSTANT.isVip.value&& !SPUtils.toolUse) {
            VipActivity.jump(this@StyleStartActivity)
            return
        }
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
                                fragment.openActivity<StyleAiActivity> {
                                    putString("img_url", sandboxPath ?: availablePath)
                                    putString("style", mModel.selectStyleBean?.key)
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
        object : CountDownTimer(300, 100) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
            }
        }.start()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}