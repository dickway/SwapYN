package com.a.activity.tool

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.a.BR
import com.a.R
import com.a.activity.ACameraActivity
import com.a.activity.AToolGeneratingActivity
import com.a.activity.AUploadFaceActivity
import com.a.databinding.ActivityAtoolPaperworkBinding
import com.a.dialog.AAdsVipShowDialog
import com.a.dialog.ABaseHintDialog
import com.a.dialog.ASwapHintDialog
import com.a.viewmodel.AToolPaperworkViewModel
import com.a.net.ARepository
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.face.util.GVM
import com.face.ad.AdUtil
import com.face.bean.UseTypeBean
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class APaperworkActivity :
    BaseBindingActivity<ActivityAtoolPaperworkBinding, AToolPaperworkViewModel>(
        R.layout.activity_atool_paperwork,
        AToolPaperworkViewModel::class.java
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
                openActivity<AToolGeneratingActivity> {
                    putString("taskId", it)
                }
                finish()
            }
        }

        GVM.INSTANT.hasAD.observe(this) {//广告拿到奖励，取消弹窗
            dismissLoading()
        }
        mModel.enableBtn.observe(this, Observer {
            mBinding?.tv1?.alpha = if (it) 1f else 0.3f
        })

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
        ABaseHintDialog(
            "Notice",
            getResources().getQuantityString(
                R.plurals.apaperwork_start_viphint,
                SPUtils.poperworkNum,
                SPUtils.poperworkNum
            ),
            "Continue"
        ).showIgnoreState(mActivity)
    }


    fun onUploadClick() {
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
            .isGif(false)
            .isWebp(false)
            .setMaxSelectNum(1)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>, frament: Fragment) {
                    if (result.isNotEmpty()) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                frament.openActivity<AUploadFaceActivity> {
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
                    openActivity<ACameraActivity>()
                }

                override fun onHint(hoACT: FragmentActivity) {
                    ASwapHintDialog().showIgnoreState(hoACT)
                }
            })
    }

    fun postSend() {
        if (SPUtils.poperworkNew >= SPUtils.poperworkNum && !GVM.INSTANT.hasAD.value) {
            AAdsVipShowDialog(
                String.format(
                    resources.getString(R.string.dialog_aads_vip_context),
                    SPUtils.poperworkNum.toString()
                ),
                onToAct = { toShowAD() },
                onNotAds = { onNotAds() }).showIgnoreState(this)
        } else {
            mModel.sendAiTask()
        }
    }

    private fun toShowAD() {
        if (!AdUtil.showRewardedAdForRewarded()) {
            showLoading(false)
            GlobalScope.launch(Dispatchers.Main) {
                delay(6 * 1000) // 延迟6秒
                AdUtil.cancelAds = true
                dismissLoading()
                if (!AdUtil.showAds) toast(getString(com.face.R.string.ad_load_fail))
            }
        }
    }

    fun onNotAds() {
        showLoading()
        SPUtils.useType = UseTypeBean(type = "PaperworkAD")
        launchRequestOnIO({
            ARepository.subUserTFLOPS(
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
