package com.a.dialog

import android.view.Gravity
import android.view.WindowManager
import com.a.BR
import com.a.databinding.DialogAexitBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp

class AExitDialog @JvmOverloads constructor(
    private val onCloseApp: (() -> Unit)? = null
) : BaseBindingDF<DialogAexitBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    horizontalPadding = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = false,
    cancelable = false
) {

    fun onConfirmClick() {
        onCloseApp?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}