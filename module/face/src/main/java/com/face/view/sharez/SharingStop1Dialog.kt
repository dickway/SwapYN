package com.face.view.sharez

import android.os.Bundle
import android.view.Gravity
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogDeleteBinding
import com.face.databinding.DialogShareStopBinding
import com.face.databinding.DialogSharingStop1Binding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class SharingStop1Dialog @JvmOverloads constructor(
    private val onStopData1: ((SharingStop1Dialog) -> Unit)? = null
) : BaseBindingDF<DialogSharingStop1Binding>(
    horizontalPadding=0.dp,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onConfirmClick() {
        onStopData1?.invoke(this)
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
    }
}