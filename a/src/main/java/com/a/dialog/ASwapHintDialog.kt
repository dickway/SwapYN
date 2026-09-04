package com.a.dialog

import android.view.Gravity
import android.view.WindowManager
import com.a.databinding.DialogAswaphintBinding
import com.face.util.GVM
import com.a.BR
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class ASwapHintDialog @JvmOverloads constructor(
    private val selectorPhoto: (() -> Unit)? = null
) : BaseBindingDF<DialogAswaphintBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    verticalPadding = 1.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false
) {

    fun onUploadClick() {
        selectorPhoto?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}