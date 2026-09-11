package com.a.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.a.R
import com.a.databinding.DialogAswaphintBinding
import com.face.util.GVM
import com.a.BR
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF


class ASwapHintDialog @JvmOverloads constructor(
    private val selectorPhoto: (() -> Unit)? = null
) : BaseBindingDF<DialogAswaphintBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    height = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.CENTER,
    isBottomAnimation = false
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AThemeSwapHint)
    }

    fun onUploadClick() {
        selectorPhoto?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}
