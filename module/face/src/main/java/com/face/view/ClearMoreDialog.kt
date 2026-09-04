package com.face.view

import android.view.Gravity
import android.view.WindowManager
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogClearmoreBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF


class ClearMoreDialog @JvmOverloads constructor(
    private val onDeleteAct: (() -> Unit)? = null,
    private val onUpdateAct: (() -> Unit)? = null
) : BaseBindingDF<DialogClearmoreBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    fun onUpdateAct() {
        dismissAllowingStateLoss()
        onUpdateAct?.invoke()
    }

    fun onDeleteAct() {
        dismissAllowingStateLoss()
        onDeleteAct?.invoke()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}