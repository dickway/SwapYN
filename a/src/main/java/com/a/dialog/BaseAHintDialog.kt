package com.a.dialog

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import com.a.BR
import com.a.databinding.DialogAhintBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight

class BaseAHintDialog @JvmOverloads constructor(
    val titleStr: String = "",
    val contentStr: String = "",
    val OkStr: String = "",
) : BaseBindingDF<DialogAhintBinding>(
    horizontalPadding = 48.dp,
    height = getScreenHeight()-180.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
) {
    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            tvTitle.text=titleStr
            tvDis.text =contentStr// SPUtils.txtDisclaimer
            btnConfirm.text=OkStr
//            tvDis.movementMethod = ScrollingMovementMethod.getInstance();
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