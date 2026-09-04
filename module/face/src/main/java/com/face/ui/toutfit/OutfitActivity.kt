package com.face.ui.toutfit

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.apkfuns.logutils.LogUtils
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.ad.AdUtil
import com.face.databinding.ActivityKissBinding
import com.face.databinding.ActivityOutfitBinding
import com.face.key.AiTaskType
import com.face.ui.FunZoneHistoryActivity
import com.face.ui.ToolGeneratingActivity
import com.face.util.SPUtils
import com.face.ui.BaseBindingActivity
import com.face.ui.VipActivity
import com.face.util.GlideEngine
import com.face.view.BaseRedDialog
import com.face.view.NotAIPointsDialog
import com.face.viewmodel.activity.KissViewModel
import com.face.viewmodel.activity.OutfitViewModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers

class OutfitActivity : BaseBindingActivity<ActivityOutfitBinding, OutfitViewModel>(
    R.layout.activity_outfit,
    OutfitViewModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
        mModel.taskId.observe(this) {
            if (it.isNotEmpty()) {
                initData()
                openActivity<ToolGeneratingActivity> {
                    putString("taskId", it)
                    putString("taskType", AiTaskType.CHANGE_CLOTHES)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        GVM.INSTANT.refreshUserInfo()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initData()
    }

    fun initData() {
        //选择图片模式
        mModel.isChoose.value = false
        mModel.img1.value = ""
        mModel.img2.value = ""
        mModel.taskId.value = ""
        mModel.firstImg = ""
        mBinding?.apply {
            imgChoose1.loadImage(mModel.img1.value)
            imgChoose2.loadImage(mModel.img2.value)
        }
    }


    fun postSend() {
        if (!mModel.isChoose.value) {
            toast(getString(R.string.hug_hint))
            return
        }
        mModel.firstImg=""
        if (GVM.INSTANT.userInfo.value.tflops < mModel.outfitPinot.value.toInt()) {
            GVM.INSTANT.payPage.value="Buy_AIPoints_Outfit"
            NotAIPointsDialog().showIgnoreState(this)
        } else {
            BaseRedDialog(
                getString(R.string.note_dialog),
                getString(R.string.hint_spent),
                getString(R.string.vip_continue),
                getString(R.string.cancel),
                unLine=false,
                onBtnOK = {
                    showLoading(getString(R.string.picpost_uploading), false)
                    mModel.uploadPicture(mModel.img1.value)
                }
            ).showIgnoreState(mActivity)

        }
    }

    fun selectorPhoto(index: Int) {
        if (!GVM.INSTANT.isVip.value) {
            GVM.INSTANT.payPage.value = "Home_tool_outfit"
            VipActivity.jump(this@OutfitActivity)
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
            .isBack(true)
            .setMaxSelectNum(1)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>, fragment: Fragment) {
                    if (result.isNotEmpty()) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                when (index) {
                                    1 -> mModel.img1.value = sandboxPath ?: availablePath
                                    2 -> mModel.img2.value = sandboxPath ?: availablePath
                                }
                                mBinding?.apply {
                                    imgChoose1.loadImage(mModel.img1.value)
                                    imgChoose2.loadImage(mModel.img2.value)
                                }
                                if (mModel.img1.value.isNotEmpty() && mModel.img2.value.isNotEmpty()) mModel.isChoose.postValue(
                                    true
                                )
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
        openActivity<FunZoneHistoryActivity> {
            putString("aiTaskType", AiTaskType.CHANGE_CLOTHES)
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}