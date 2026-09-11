package com.a.dialog

import android.os.Bundle
import android.view.Gravity
import com.a.BR
import com.a.databinding.DialogAuploadFailedBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp

class AUploadFailedDialog : BaseBindingDF<DialogAuploadFailedBinding>(
    horizontalPadding = 48.dp,
    maxWidth = 360.dp,
    gravity = Gravity.CENTER,
    dimAmount = 0.5f,
    cancelable = false,
    isBottomAnimation = false
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    fun onConfirmClick() {
        activity?.finish()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
    }
}
