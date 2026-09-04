package com.a.dialog

import android.os.Bundle
import android.view.Gravity
import com.a.BR
import com.a.databinding.DialogAhintaBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp

class ABaseHintDialog @JvmOverloads constructor(
    val titleStr: String = "",
    val contentStr: String = "",
    val OkStr: String = "",
) : BaseBindingDF<DialogAhintaBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
) {
    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            tvTitle.text=titleStr
            tvDis.text =contentStr// SPUtils.txtDisclaimer
            btnConfirm.text=OkStr
        }

    }

    fun onConfirmClick() {
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}