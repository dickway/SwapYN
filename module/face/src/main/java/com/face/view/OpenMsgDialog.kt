package com.face.view

import android.view.Gravity
import android.view.WindowManager
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogOpenMsgBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF


class OpenMsgDialog @JvmOverloads constructor(
    private val onOpenApp: (() -> Unit)? = null
) : BaseBindingDF<DialogOpenMsgBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {

    override fun onResume() {
        super.onResume()
    }

    fun onConfirmClick() {
        onOpenApp?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}