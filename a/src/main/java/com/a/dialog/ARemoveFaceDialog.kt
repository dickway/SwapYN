package com.a.dialog

import android.view.Gravity
import com.a.BR
import com.a.databinding.DialogARemoveFaceBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp

class ARemoveFaceDialog @JvmOverloads constructor(
    private val onRemove: (() -> Unit)? = null
) : BaseBindingDF<DialogARemoveFaceBinding>(
    horizontalPadding = 48.dp,
    maxWidth = 360.dp,
    gravity = Gravity.CENTER,
    dimAmount = 0.6f,
    cancelable = true
) {
    fun onConfirmClick() {
        onRemove?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments() = DataBindingArguments(BR.handler, this)
}
