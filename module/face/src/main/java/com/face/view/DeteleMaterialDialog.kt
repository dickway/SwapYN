package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogDeteleMaterialBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class DeteleMaterialDialog @JvmOverloads constructor(
    private val taskCancel: (() -> Unit)? = null
) : BaseBindingDF<DialogDeteleMaterialBinding>(
    horizontalPadding=48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    fun onConfirmClick() {
        taskCancel?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}