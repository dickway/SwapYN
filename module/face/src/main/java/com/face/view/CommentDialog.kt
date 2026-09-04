package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogCommentBinding
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class CommentDialog @JvmOverloads constructor(
    private val onOpenCode: (() -> Unit)? = null,
    private val onFeedback: (() -> Unit)? = null
) : BaseBindingDF<DialogCommentBinding>(
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
        onOpenCode?.invoke()
        dismissAllowingStateLoss()
    }

    fun onFeedback() {
        SPUtils.notComment=true
        onFeedback?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}