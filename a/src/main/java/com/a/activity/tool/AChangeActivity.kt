package com.a.activity.tool

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.a.BR
import com.a.R
import com.a.activity.AToolGeneratingActivity
import com.a.databinding.ActivityAtoolChangeBinding
import com.a.dialog.AAdsVipShowDialog
import com.a.dialog.ABaseHintDialog
import com.a.viewmodel.AChangeViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.face.ad.AdUtil
import com.face.bean.UseTypeBean
import com.a.net.ARepository
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AChangeActivity : BaseBindingActivity<ActivityAtoolChangeBinding, AChangeViewModel>(
    R.layout.activity_atool_change,
    AChangeViewModel::class.java
) {
    val imgUrl by intentExtras("img_url", "")

    var tvNum: TextView? = null
    var tvClick: TextView? = null


    override fun init(savedInstanceState: Bundle?) {
        bar {
            transparent()
            light(false)
        }
        if (SPUtils.adsAgeShow) {
            onHint()
        }

        Glide.with(this@AChangeActivity).asBitmap()
            .load(imgUrl)
            .skipMemoryCache(true)  // 禁用内存缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE)  // 禁用磁盘缓存
            .placeholder(com.key.R.drawable.img_default_m)
            .override(getScreenWidth(), getScreenHeight())
            .into(object : CustomTarget<Bitmap>() {
                override fun onStart() {
                    showLoading()
                    super.onStart()
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
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

                    val widths = getScreenWidth()//控件最大宽度
                    val heights = (getScreenHeight() - 268.dp)//控件最大高度

                    val thresholdValue = widths / heights.toDouble() //阈值
                    var realValue = width / height //实际比例
                    dismissLoading()
                    mBinding?.initImg?.apply {
                        if (thresholdValue > realValue) {
                            layoutParams.width = heights * width / height
                            layoutParams.height = heights
                        } else {
                            layoutParams.width = widths
                            layoutParams.height = widths * height / width
                        }
                        setImageBitmap(mModel.initBitmap)
                    }
                    dismissLoading()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        mModel.taskId.observe(this) {
            if (it.isNotEmpty()) {
                SPUtils.ageNew += 1
                openActivity<AToolGeneratingActivity> {
                    putString("title", "Change Age")
                    putString("taskId", it)
                }
                finish()
            }
        }

        GVM.INSTANT.hasAD.observe(this) {//广告拿到奖励，取消弹窗
            dismissLoading()
        }


        tvNum = mBinding?.tvNum
        tvClick = mBinding?.tvClick
        mBinding?.seekbarEraser?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val thumbPosX = (seekBar!!.paddingLeft + seekBar.thumb.bounds.centerX()) + 12.dp
                // 设置 TextView 的位置
                tvNum?.x = (thumbPosX).toFloat()
                tvNum?.text = (progress * 10).toString()

                val thumbPosX2 = seekBar.thumb.bounds.centerX() + seekBar.paddingLeft + 24.dp

                tvClick?.x = (thumbPosX2).toFloat()
                tvClick?.text = (progress * 10).toString()

                mModel.seekbarNum = (progress * 10)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                tvClick?.visibility = View.GONE
                tvNum?.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                tvClick?.visibility = View.VISIBLE
                tvNum?.visibility = View.GONE
            }

        })

    }

    /**
     * 提交图片
     */
    fun saveBitmap() {
        SPUtils.isMaterial = true
        showLoading("Uploading picture…", false)
        mModel.uploadPicture(mModel.initBitmap)
    }

    fun onHint() {
        SPUtils.adsAgeShow = false
        ABaseHintDialog(
            "Notice",
            getResources().getQuantityString(
                R.plurals.achange_phint,
                SPUtils.ageNum,
                SPUtils.ageNum
            ),
            "Continue"
        ).showIgnoreState(mActivity)
    }

    fun postSend() {
        if (SPUtils.ageNew >= SPUtils.ageNum && !GVM.INSTANT.hasAD.value) {
            AAdsVipShowDialog(
                String.format(
                    resources.getString(R.string.dialog_aads_vip_context),
                    SPUtils.ageNum.toString()
                ),
                onToAct = { toShowAD() },
                onNotAds = { onNotAds() }).showIgnoreState(this)
        } else {
            saveBitmap()
        }
    }

    fun onNotAds() {
        showLoading()
        SPUtils.useType = UseTypeBean(type = "AgeAD")
        launchRequestOnIO({ ARepository.subUserTFLOPS(SPUtils.useAd,type = MoshiHelper.convertObjectToJson(SPUtils.useType)) }) {
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


    private fun toShowAD() {
        if (!AdUtil.showRewardedAdForRewarded()) {
            showLoading(false)
            GlobalScope.launch(Dispatchers.Main) {
                delay(6 * 1000) // 延迟6秒
                AdUtil.cancelAds=true
                dismissLoading()
                if (!AdUtil.showAds) toast(getString(com.face.R.string.ad_load_fail))
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
