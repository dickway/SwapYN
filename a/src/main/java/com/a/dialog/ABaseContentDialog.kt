package com.a.dialog

import android.os.Bundle
import android.view.Gravity
import com.a.databinding.DialogAcontentBinding
import com.face.util.GVM
import com.a.BR
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class ABaseContentDialog @JvmOverloads constructor(
    val titleStr: String = "",
    val contentStr: String = "",
    val OkStr: String = "",
    val CancelStr: String = "",
    private val onOkData: (() -> Unit)? = null,
    private val onCancelData: (() -> Unit)? = null
) : BaseBindingDF<DialogAcontentBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun init(savedInstanceState: Bundle?) {

        mBinding?.apply {
            title.text = titleStr
            content.text = contentStr
            txtOk.text = OkStr
            txtCancel.text = CancelStr
        }
    }

    fun onOkData() {
        onOkData?.invoke()
        dismissAllowingStateLoss()
    }

    fun onCanceClick() {
        onCancelData?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}