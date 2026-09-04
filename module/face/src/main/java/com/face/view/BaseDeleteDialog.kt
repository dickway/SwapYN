package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogBaseDeteleBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class BaseDeleteDialog @JvmOverloads constructor(
    val title: String="",
    val content: String="",
    private val onDeleteData: (() -> Unit)? = null
) : BaseBindingDF<DialogBaseDeteleBinding>(
    horizontalPadding=48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        mBinding?.tvTitle?.text = title
        mBinding?.tvContent?.text = content
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    fun onConfirmClick() {
        onDeleteData?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}