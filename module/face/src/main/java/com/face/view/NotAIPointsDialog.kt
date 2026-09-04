package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.BR
import com.face.databinding.DialogNoAipointsBinding
import com.face.ui.BuyPointActivity
import com.face.ui.SigninActivity
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity


class NotAIPointsDialog : BaseBindingDF<DialogNoAipointsBinding>(
    horizontalPadding = 49.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
    }

    fun onRechargeClick() {
        openActivity<BuyPointActivity>()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}