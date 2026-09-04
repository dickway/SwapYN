package com.face.view

import android.view.Gravity
import android.view.WindowManager
import com.face.BR
import com.face.databinding.DialogExitBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF

/**
 * @author 再战科技
 * @date 2022/4/19
 * @description
 */
class ExitDialog @JvmOverloads constructor(
    private val onCloseApp: (() -> Unit)? = null
) : BaseBindingDF<DialogExitBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true,
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