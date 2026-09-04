package com.a.dialog

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.View
import com.a.R
import com.a.BR
import com.a.databinding.DialogAfilyBinding
import com.face.ui.WebActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getColorX

class AFailYDialog @JvmOverloads constructor(
    private val onBtnOK: (() -> Unit)? = null
) : BaseBindingDF<DialogAfilyBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
) {
    override fun init(savedInstanceState: Bundle?) {
    }

    fun onConfirmClick() {
        onBtnOK?.invoke()
        dismissAllowingStateLoss()
    }

    fun onCancelClick() {
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}