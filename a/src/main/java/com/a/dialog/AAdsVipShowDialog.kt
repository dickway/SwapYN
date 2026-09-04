package com.a.dialog

import android.os.Bundle
import android.view.Gravity
import com.a.databinding.DialogAadsvipshowBinding
import com.a.BR
import com.face.ad.AdUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class AAdsVipShowDialog @JvmOverloads constructor(
    val contentStr: String = "",
    private val onToAct: (() -> Unit)? = null,
    private val onNotAds: (() -> Unit)? = null
) : BaseBindingDF<DialogAadsvipshowBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = true
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            content.text = contentStr
            tvUseNum.text=String.format(
                resources.getString(com.face.R.string.consume_point),
                SPUtils.useAd.toString()
            )
        }
    }

    fun onNoAdsClick() {
        onNotAds?.invoke()
    }

    fun onQClick() {
        dismissAllowingStateLoss()
    }

    fun onConfirmClick() {
        onToAct?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}