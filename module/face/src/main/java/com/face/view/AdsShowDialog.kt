package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.BR
import com.face.R
import com.face.ad.AdUtil
import com.face.databinding.DialogAdsshowBinding
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity


class AdsShowDialog @JvmOverloads constructor(
    private val onToAct: (() -> Unit)? = null,
    private val onNotAds: (() -> Unit)? = null
) : BaseBindingDF<DialogAdsshowBinding>(
    horizontalPadding = 49.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {

    var usePinot = NotNullMutableLiveData(SPUtils.useAd)

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false

        val num1 = GVM.INSTANT.userInfo.value.mediaNum//用户生成次数
        val num2 = SPUtils.freeNum.toInt()// 可免费生成次数
        val num3 = SPUtils.nowAdsNum.toInt()// 设备每日能生成次数
        mBinding?.apply {
            tvUseNum.text=String.format(
                resources.getString(R.string.consume_point),
                SPUtils.useAd.toString()
            )

            if (num1 < (num3 + num2)) {//用户生成次数在设置免费和每日之间就提示总次数
                title.text = String.format(
                    resources.getString(R.string.dialog_ads_title1),
                    (num3 + num2).toString()
                )
                content.text = String.format(
                    resources.getString(R.string.dialog_ads_context1),
                    (num3 + num2 - num1).toString()
                )
            } else {//用户生成次数超过免费和每日就提示每日生成
                title.text = resources.getString(R.string.dialog_ads_title2)
                content.text = String.format(
                    resources.getString(R.string.dialog_ads_context2),
                    num3.toString()
                )
            }

        }

    }

    fun onQClick() {
        dismissAllowingStateLoss()
    }

    fun onVipClick() {
        dismissAllowingStateLoss()
        VipActivity.jump(requireActivity())
    }

    fun onNoAdsClick() {
        dismissAllowingStateLoss()
        onNotAds?.invoke()
    }


    fun onConfirmClick() {
        dismissAllowingStateLoss()
        onToAct?.invoke()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}