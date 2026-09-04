package com.face.ui.live

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.face.net.Repository
import com.face.BR
import com.face.R
import com.face.ad.AdUtil
import com.face.bean.UseTypeBean
import com.face.databinding.ActivityAiliveBinding
import com.face.key.AiTaskType
import com.face.ui.ToolGeneratingActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.AdsVipPoperShowDialog
import com.face.view.BaseHintDialog2
import com.face.view.BaseRedDialog
import com.face.viewmodel.activity.LiveAiViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
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


class LiveAiActivity : BaseBindingActivity<ActivityAiliveBinding, LiveAiViewModel>(
    R.layout.activity_ailive,
    LiveAiViewModel::class.java
) {
    val imgUrl by intentExtras("img_url", "")

    override fun init(savedInstanceState: Bundle?) {
        if (SPUtils.adsLiveShow) {
            onHint()
        }

        Glide.with(this@LiveAiActivity).asBitmap()
            .load(imgUrl)
            .skipMemoryCache(true)  // 禁用内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE)  // 禁用磁盘缓存
            .placeholder(com.key.R.drawable.img_default_m)
            .override(getScreenWidth(), getScreenHeight())
            .into(object : CustomTarget<Bitmap>() {

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    showLoading()
                    //检查是否需要压缩
                    mModel.initBitmap = resource
                    val width = resource.getWidth()
                    val height = resource.getHeight()
                    var inSampleSize = 1
                    val max = 20 * 1024 * 1024
                    while (width * height * 4 / inSampleSize > max) {
                        inSampleSize *= 2
                    }
                    if (inSampleSize > 1) {
                        val m2 = Matrix()
                        m2.setScale(1 / inSampleSize.toFloat(), 1 / inSampleSize.toFloat())
                        mModel.initBitmap =
                            Bitmap.createBitmap(resource, 0, 0, width, height, m2, false);
                    }
                    mBinding?.initImg?.setImageBitmap(mModel.initBitmap)
                    dismissLoading()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        mModel.taskId.observe(this) {
            if (it.isNotEmpty()) {
                SPUtils.liveNew += 1
                openActivity<ToolGeneratingActivity> {
                    putString("taskType", AiTaskType.DYNAMIC)
                    putString("taskId", it)
                }
                finish()
            }
        }

        GVM.INSTANT.hasAD.observe(this) {//广告拿到奖励，取消弹窗
            dismissLoading()
        }

    }

    /**
     * 提交图片
     */
    fun saveBitmap() {
        SPUtils.isMaterial = true
        showLoading(getStringX(R.string.picpost_title), false)
        mModel.uploadPicture(mModel.initBitmap)
    }

    fun onHint() {
        SPUtils.adsLiveShow = false
        BaseHintDialog2(
            getResources().getQuantityString(
                R.plurals.paperwork_start_viphint,
                SPUtils.liveNum,
                SPUtils.liveNum
            )
        ).showIgnoreState(mActivity)
    }

    fun postSend() {
        if (SPUtils.liveNew >= SPUtils.liveNum && !GVM.INSTANT.hasAD.value) {
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
            saveBitmap()
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
        SPUtils.useType = UseTypeBean(type = "LiveAD")
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
                    saveBitmap()
                }
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                toast(errorMsg)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        GVM.INSTANT.hasAD.postValue(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mModel.initBitmap = null
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}