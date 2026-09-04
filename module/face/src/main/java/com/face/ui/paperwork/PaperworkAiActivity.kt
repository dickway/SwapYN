package com.face.ui.paperwork

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.face.net.Repository
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.ad.AdUtil
import com.face.bean.UseTypeBean
import com.face.databinding.ActivityAipaperworkBinding
import com.face.key.AiTaskType
import com.face.ui.CameraActivity
import com.face.ui.ToolGeneratingActivity
import com.face.ui.UploadFaceActivity
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.view.AdsVipPoperShowDialog
import com.face.view.BaseHintDialog2
import com.face.view.BaseRedDialog
import com.face.view.SwapHintDialog
import com.face.viewmodel.activity.PaperworkAiViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaperworkAiActivity :
    BaseBindingActivity<ActivityAipaperworkBinding, PaperworkAiViewModel>(
        R.layout.activity_aipaperwork,
        PaperworkAiViewModel::class.java
    ) {


    override fun init(savedInstanceState: Bundle?) {
        if (SPUtils.adsPaperworkShow) {
            onHint()
        }
        GVM.INSTANT.needRefresh.postValue(false)
        mModel.getUserPics()
        GVM.INSTANT.needRefresh.observe(this) {
            if (it && mModel.userAdapter.selectIndex != -1) {
                GVM.INSTANT.needRefresh.postValue(false)
                mBinding?.rlChoose?.scrollToPosition(0)
            }
        }
        mModel.colorAdapter.onItemClick = { _, bean, index ->
            mModel.postColor.postValue(bean?.name)
//            onBackground(bean?.color ?: "")
        }
        mModel.sizeAdapter.onItemClick = { _, bean, index ->
            mModel.postSize.postValue(bean?.pxSize)
        }
        mModel.userAdapter.onItemClick = { _, bean, index ->
            mModel.postImgUrl.postValue(bean?.pic)
        }
        mModel.taskId.observe(this) {
            if (it.isNotEmpty()) {
                SPUtils.poperworkNew += 1
                openActivity<ToolGeneratingActivity> {
                    putString("taskId", it)
                    putString("taskType", AiTaskType.ID_CARD)
                }
                finish()
            }
        }

        GVM.INSTANT.hasAD.observe(this) {//广告拿到奖励，取消弹窗
            dismissLoading()
        }
    }

    fun onStyle(num: Int = 1) {
        mModel.style.postValue(num)
        when (num) {
            1 -> {
                mModel.postStyle.postValue("causal")
            }

            2 -> {
                mModel.postStyle.postValue("formal")
//                mModel.sizeList.value = mModel.sizeList.value.map {
//                    it.copy(style = "formal")
//                }
            }
        }
    }

    fun onHint() {
        SPUtils.adsPaperworkShow = false
        BaseHintDialog2(
            getResources().getQuantityString(
                R.plurals.paperwork_start_viphint,
                SPUtils.poperworkNum,
                SPUtils.poperworkNum
            )
        ).showIgnoreState(mActivity)
    }


    fun onUploadClick() {
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
            .isGif(false)
            .isWebp(false)
            .setMaxSelectNum(1)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>, fragment: Fragment) {
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

    fun postSend() {
        if (SPUtils.poperworkNew >= SPUtils.poperworkNum && !GVM.INSTANT.hasAD.value) {

            if (SPUtils.nowAdsNum == "0") {
                BaseRedDialog(
                    getString(R.string.dialog_ads_vip_title),
                    String.format(
                        resources.getString(R.string.dialog_ads_vip_context2),
                        SPUtils.vipUsageCount
                    ),
                    "${String.format(
                        resources.getString(R.string.consume_point),
                        SPUtils.useAd.toString()
                    )} (${GVM.INSTANT.userInfo.value.tflops})",
                    getString(R.string.cancel),
                    unClick = "1",
                    onBtnOK = {
                        onNotAds()
                    }
                ).showIgnoreState(mActivity)
            } else {
                AdsVipPoperShowDialog(
                    onToAct = { toShowAD() },
                    onNotAds = { onNotAds() }).showIgnoreState(this)
            }

        } else {
            mModel.sendAiTask()
        }
    }

    private fun toShowAD() {
        if (!AdUtil.showRewardedAdForRewarded()) {
            showLoading(false)
            GlobalScope.launch(Dispatchers.Main) {
                delay(6 * 1000) // 延迟6秒
                AdUtil.cancelAds=true
                dismissLoading()
                if (!AdUtil.showAds) toast(getString(R.string.ad_load_fail))
            }
        }
    }

    fun onNotAds() {
        showLoading()
        SPUtils.useType = UseTypeBean(type = "PaperworkAD")
        launchRequestOnIO({
            Repository.subUserTFLOPS(
                SPUtils.useAd,
                type = MoshiHelper.convertObjectToJson(SPUtils.useType)
            )
        }) {
            onSuccess = {
                GVM.INSTANT.refreshUserInfo {
                    dismissLoading()
                    GVM.INSTANT.hasAD.value = true
                    mModel.sendAiTask()
                }
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                toast(errorMsg)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        mModel.getUserPics()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}