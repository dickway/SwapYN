package com.face.view.sharez

import android.os.Bundle
import android.view.Gravity
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogShareStopBinding
import com.face.databinding.DialogSharingBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class SharingDialog @JvmOverloads constructor(
    private val onSharing: ((SharingDialog) -> Unit)? = null
) : BaseBindingDF<DialogSharingBinding>(
    horizontalPadding=48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onConfirmClick() {
        onSharing?.invoke(this)
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}