package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.BR
import com.face.databinding.DialogUploadfailedBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class UploadFailedDialog @JvmOverloads constructor(
    val txtContent: String = "",
    private val onToAct: (() -> Unit)? = null
) : BaseBindingDF<DialogUploadfailedBinding>(
    horizontalPadding = 32.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
        mBinding?.content?.text = txtContent

    }

    fun onConfirmClick() {
        onToAct?.invoke()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}