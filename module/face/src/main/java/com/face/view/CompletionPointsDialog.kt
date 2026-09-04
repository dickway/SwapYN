package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.BR
import com.face.databinding.DialogCompletionPointsBinding
import com.face.ui.BuyPointActivity
import com.face.ui.VipActivity
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity


class CompletionPointsDialog : BaseBindingDF<DialogCompletionPointsBinding>(
    horizontalPadding = 49.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
    }

    fun onVipClick() {
        GVM.INSTANT.payPage.value =
            "Download_material_pointsdialog"
        VipActivity.jump(requireActivity())
        dismissAllowingStateLoss()
    }

    fun onPointClick() {
        dismissAllowingStateLoss()
        GVM.INSTANT.payPage.value="Buy_AIPoints_Download"
        openActivity<BuyPointActivity>()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}