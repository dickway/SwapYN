package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogCancelBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class CancelDialog @JvmOverloads constructor(
    private val onCancel: (() -> Unit)? = null
) : BaseBindingDF<DialogCancelBinding>(
    horizontalPadding=48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onConfirmClick() {
        onCancel?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}