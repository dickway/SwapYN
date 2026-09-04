package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogFrameHintBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF


class FrameHintDialog @JvmOverloads constructor(
//    private val onCloseApp: (() -> Unit)? = null
) : BaseBindingDF<DialogFrameHintBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
    }

    fun onConfirmClick() {
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}