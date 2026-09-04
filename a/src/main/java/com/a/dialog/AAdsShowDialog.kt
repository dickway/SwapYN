package com.a.dialog

import android.os.Bundle
import android.view.Gravity
import com.a.databinding.DialogAadsshowBinding
import com.a.BR
import com.a.R
import com.a.activity.AVipActivity
import com.face.ad.AdUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity


class AAdsShowDialog @JvmOverloads constructor(
    private val onToAct: (() -> Unit)? = null,
    private val onNotAds: (() -> Unit)? = null
) : BaseBindingDF<DialogAadsshowBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = true
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = true

        val num1 = GVM.INSTANT.userInfo.value.mediaNum//用户生成次数
        val num2 = SPUtils.freeNum.toInt()// 可免费生成次数
        val num3 = SPUtils.nowAdsNum.toInt()// 设备每日能生成次数
        mBinding?.apply {
            if (num1 < (num3 + num2)) {//用户生成次数在设置免费和每日之间就提示总次数
                title.text = String.format(
                    resources.getString(R.string.dialog_aads_title1),
                    (num3 + num2).toString()
                )
                content.text = String.format(
                    resources.getString(R.string.dialog_aads_context1),
                    (num3 + num2 - num1).toString()
                )
            } else {//用户生成次数超过免费和每日就提示每日生成
                title.text = resources.getString(R.string.dialog_aads_title2)
                content.text = String.format(
                    resources.getString(R.string.dialog_aads_context2),
                    num3.toString()
                )
            }
            tvUseNum.text=String.format(
                resources.getString(com.face.R.string.consume_point),
                SPUtils.useAd.toString()
            )
        }

    }

    fun onQClick() {
        dismissAllowingStateLoss()
    }

    fun onVipClick() {
        dismissAllowingStateLoss()
        openActivity<AVipActivity>()
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