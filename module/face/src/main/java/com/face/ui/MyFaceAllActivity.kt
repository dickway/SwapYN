package com.face.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.face.BR
import com.face.R
import com.face.adapter.myface.MyAllFaceAdapter
import com.face.databinding.ActivityMyallfaceBinding
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.view.PrivacyDialog
import com.face.view.SwapHintDialog
import com.face.viewmodel.activity.MyAllFaceViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast


class MyFaceAllActivity : BaseBindingActivity<ActivityMyallfaceBinding, MyAllFaceViewModel>(
    R.layout.activity_myallface,
    MyAllFaceViewModel::class.java
) {
    private var myAllFaceAdapter = MyAllFaceAdapter()

    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("facelist")
        myAllFaceAdapter.apply {
            onFaceEdit = this@MyFaceAllActivity.onFaceEdit
        }
    }

    private val onFaceEdit: (String?) -> Unit =
        { data: String? ->
            openActivity<MyFaceActivity>() {
                putString("type_name", data)
            }

        }

    fun onAddClick() {
        if (SPUtils.privacyHint) {
            PrivacyDialog {
                if (SPUtils.openFace) {
                    toAddFace()
                } else {
                    SPUtils.openFace = true
                    SwapHintDialog(::toAddFace).showIgnoreState(this)
                }
            }.showIgnoreState(this)
        } else {
            if (SPUtils.openFace) {
                toAddFace()
            } else {
                SPUtils.openFace = true
                SwapHintDialog(::toAddFace).showIgnoreState(this)
            }
        }
    }

    fun toAddFace() {
        SPUtils.isMaterial=false
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
            .setMaxSelectNum(1)
            .isWebp(false)
            .isGif(false)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>,fragment: Fragment) {
                    if (result.size > 0) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                fragment.openActivity<UploadFaceActivity> {
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
                    openActivity<CameraActivity>()
                }

                override fun onHint(hoACT: FragmentActivity) {
                    SwapHintDialog().showIgnoreState(hoACT)
                }
            })
    }


    override fun onResume() {
        super.onResume()
        mModel.getUserPics()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.adapter, myAllFaceAdapter)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}