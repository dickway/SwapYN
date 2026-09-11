package com.a.activity

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import coil.load
import com.a.R
import com.a.BR
import com.a.adapter.ASwapTabAdapter
import com.a.databinding.ActivityAswapBinding
import com.a.dialog.AAdsShowDialog
import com.a.dialog.AAdsVipShowDialog
import com.a.dialog.ABaseHintDialog
import com.a.dialog.AExperienceDialog
import com.a.dialog.AReportDialog
import com.a.dialog.ASwapHintDialog
import com.a.dialog.BaseAHintDialog
import com.a.dialog.BaseYDialog
import com.a.viewmodel.ASwapViewModel
import com.blankj.utilcode.util.LogUtils
import com.face.ad.AdUtil
import com.face.bean.UseTypeBean
import com.face.bean.VipUserCountBean.Companion.toMap
import com.face.key.AiTaskType
import com.a.net.ARepository
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.view.NotAIPointsDialog
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.face.ui.BaseBindingActivity
import com.face.ui.BuyPointActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.ImgLoader
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ASwapActivity : BaseBindingActivity<ActivityAswapBinding, ASwapViewModel>(
    R.layout.activity_aswap,
    ASwapViewModel::class.java
) {
    private val mediaId by intentExtras("media_id", "")

    private val swapTabAdapter: ASwapTabAdapter = ASwapTabAdapter()

    companion object {
        fun jump(
            context: Context,
            mediaId: String,
            typeCustom: String = "",
        ) {
            context.openActivity<ASwapActivity> {
                putString("media_id", mediaId)
                putString("type_custom", typeCustom)
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("swap_face", mapOf("media_id" to mediaId))
        mModel.mediaId = mediaId
        mModel.getData()
        mModel.mediaByBean.observe(this) { media ->
            if (media == null) return@observe
            mModel.collectStr = media.collectId.orEmpty()
            mModel.isCollect.value = !media.collectId.isNullOrEmpty()
            GVM.INSTANT.hasAD.postValue(false)
            mModel.setSwapFaces(media.faceList)

            val cover = mBinding?.imgCover ?: return@observe
            val hasImageSize = media.width > 1 && media.height > 1
            cover.updateLayoutParams<ConstraintLayout.LayoutParams> {
                dimensionRatio = if (hasImageSize) "${media.width}:${media.height}" else "3:4"
            }
            cover.load(if (media.mediaType == "video") media.webpUrl else media.imageUrl, ImgLoader.imageLoader) {
                placeholder(com.key.R.drawable.img_default_m)
                error(com.key.R.drawable.img_default_m)
                if (!hasImageSize) {
                    listener(onSuccess = { _, result ->
                        val width = result.drawable.intrinsicWidth
                        val height = result.drawable.intrinsicHeight
                        if (mModel.mediaByBean.value === media && width > 0 && height > 0) {
                            cover.updateLayoutParams<ConstraintLayout.LayoutParams> {
                                dimensionRatio = "$width:$height"
                            }
                        }
                    })
                }
            }
        }

        swapTabAdapter.onItemClick = { _, bean, position ->
            mModel.isShow.postValue(true)
            mModel.selectTargetFace(position)
        }

        mModel.hasMultipleFaces.observe(this) { multipleFaces ->
            mBinding?.apply {
                conOperation.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    height = if (multipleFaces) ConstraintLayout.LayoutParams.MATCH_PARENT
                        else ConstraintLayout.LayoutParams.WRAP_CONTENT
                }
                conOperation.setPadding(0, if (multipleFaces) 0 else 12.dp, 0, 0)
                conOperation.setBackgroundColor(if (multipleFaces) 0x4D000000 else Color.TRANSPARENT)
                view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToTop = if (multipleFaces) ConstraintLayout.LayoutParams.UNSET
                        else ConstraintLayout.LayoutParams.PARENT_ID
                }
                imgCover.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    bottomToTop = if (multipleFaces) R.id.conList else R.id.conOperation
                }
            }
        }

        mModel.swapList.observe(this) {
            // Face assignments are not part of Face.equals; refresh their preview badges explicitly.
            swapTabAdapter.notifyDataSetChanged()
        }

        mModel.taskBean.observe(this) {
            if (it?.id != null) {
                val urlSwap = if (mModel.mediaByBean.value?.mediaType == ("video")) {
                    mModel.mediaByBean.value?.videoUrl
                } else {
                    mModel.mediaByBean.value?.imageUrl
                }
                AProductionActivity.jump(
                    this,
                    it.id,
                    urlSwap,
                    mModel.mediaByBean.value?.notZeroWidth(),
                    mModel.mediaByBean.value?.notZeroHeight()
                )
            }
        }

        mModel.swapAdapter.onItemClick = { _, data, _ ->
            if (mModel.selectFace(data)) {
                swapTabAdapter.selectIndex = -1
                if (data?.isSelect == true) mModel.isShow.value = false
            }
        }
    }

    fun onToShow() {
        mModel.isShow.postValue(false)
    }


    fun onToAct() {
        BaseAHintDialog("Disclaimer", SPUtils.txtDisclaimer, "Continue").showIgnoreState(this)
    }

    fun onReport() {
        AReportDialog { black, content ->
            mModel.getReport(mModel.mediaByBean.value?.id, content, black)
        }.showIgnoreState(this)
    }


    fun onProductionClick() {
        var sources = ""//多人脸图片拼接
        var isNull = false
        //多人脸拼接逻辑
        mModel.swapList.value?.forEach {
            if (it.isSelectBean != null) {
                isNull = true
                sources = sources + "," + it.isSelectBean?.pic
            } else {
                sources = sources + ","
            }
        }
        //如果初始没有人脸需要弹窗，不执行生成任务
        if (!isNull) {
            mModel.isShow.postValue(true)
            return
        }

        GVM.INSTANT.payPage.value = "ASwapFace"
        //长视频需要积分换脸
        if (mModel.isAIPoint.value && GVM.INSTANT.userInfo.value.tflops < mModel.usePinot.value) {
            GVM.INSTANT.payPage.value = "Buy_AIPoints_AVideoFace"
//            NotAIPointsDialog().showIgnoreState(mActivity)
            BaseYDialog(
                "Not Enough AIPoints",
                "Click the \\'Recharge\\' button to enter the recharge page",
                "Recharge",
                getString(R.string.dialog_apri_qixiao),
                onBtnOK = {
                    openActivity<BuyPointActivity>()
                }
            ).showIgnoreState(mActivity)
            return
        }

        //非vip用户看广告逻辑
        if (!GVM.INSTANT.userInfo.value.isVip() && !GVM.INSTANT.hasAD.value) {
            val num1 = GVM.INSTANT.userInfo.value.mediaNum//用户生成次数
            if (num1 < SPUtils.freeNum.toInt()) {//用户免费生成一次，不处理
            } else if (SPUtils.nowNum < SPUtils.nowAdsNum.toInt()) {//每日广告生成判断
                AAdsShowDialog(
                    onToAct = { toShowAD() },
                    onNotAds = { onNotAds() }).showIgnoreState(this)
                return
            } else {//无每日广告次数
                val conStr = if (SPUtils.nowAdsNum == "0") {
                    "Your free attempts have been used up. Upgrade now to unlock more exciting features!"
                } else {
                    String.format(
                        resources.getString(R.string.dialog_aexperience_txt1),
                        SPUtils.nowAdsNum
                    )
                }
                AExperienceDialog(conStr).showIgnoreState(this)
                return
            }
        }

        //vip用户看广告逻辑
        val useCount = SPUtils.vipUserCount.toMap()[GVM.INSTANT.userInfo.value.userId] ?: 0
        if (!mModel.isAIPoint.value && SPUtils.vipUsageCount.toInt() > 0 && SPUtils.vipUsageCount.toInt() <= useCount && GVM.INSTANT.userInfo.value.isVip() && !GVM.INSTANT.hasAD.value) {
            AAdsVipShowDialog(
                String.format(
                    resources.getString(R.string.dialog_aads_vip_context),
                    SPUtils.vipUsageCount
                ),
                onToAct = { toShowAD() },
                onNotAds = { onNotAds() }).showIgnoreState(mActivity)
            return
        }

        var taskType = ""
        if (mModel.mediaByBean.value?.mediaType == "video") {//是不是视频
            taskType = AiTaskType.VIDEO_FACESWAP
            if (mModel.isAIPoint.value) {
                taskType = AiTaskType.LONG_VIDEOSWAP
            }
        } else {
            taskType = AiTaskType.FACESWAP
        }

        GVM.INSTANT.userInfo.value.apply {
            mediaNum++
        }

        mModel.sendAiTask(taskType, sources.substring(1, sources.length))
    }

    fun onNotAds() {
        showLoading()
        SPUtils.useType = UseTypeBean(type = "SwapFaceAD")
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
                    onProductionClick()
                }
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                toast(errorMsg)
            }
        }
    }


    fun onCollectClick() {
        val id = mModel.mediaByBean.value?.id?.takeIf { it.isNotBlank() } ?: return
        launchRequestOnIO({
            if (mModel.isCollect.value) {
                EventUtil.clickDisCollectMedia(mModel.collectStr)
                ARepository.removeUserCollect(mModel.collectStr)
            } else {
                ARepository.saveCollect(id, "media")
            }
        }) {
            onSuccess = {
                mModel.collectStr = it.toString()
                mModel.isCollect.value = !mModel.isCollect.value
                toast(getStringX(R.string.asuccess))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun onAddClick() {
        //需要跨界面控制界面销毁的标识
        SPUtils.isMaterial = false
        if (SPUtils.openFace) {
            toAddFace()
        } else {
            SPUtils.openFace = true
            ASwapHintDialog(::toAddFace).showIgnoreState(this)
        }
    }

    fun toAddFace() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setLanguage(LanguageConfig.ENGLISH)
            .setMaxSelectNum(1)
            .isWebp(false)
            .isGif(false)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>, frament: Fragment) {
                    if (result.size > 0) {
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

    override fun onPause() {
        super.onPause()
        GVM.INSTANT.hasAD.value=false
    }

    override fun onResume() {
        super.onResume()
        mModel.getUserPics()
        GVM.INSTANT.refreshUserInfo()
    }


    private fun toShowAD() {
        EventUtil.appClick("toShowAD")
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

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.adapterTab, swapTabAdapter)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}
