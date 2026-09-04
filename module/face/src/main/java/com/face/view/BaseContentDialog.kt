package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogContentBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class BaseContentDialog @JvmOverloads constructor(
    val titleStr: String = "",
    val contentStr: String = "",
    val leftStr: String = "",
    val rightStr: String = "",
    val isok: Boolean = true,
    private val onLeftData: (() -> Unit)? = null,
    private val onRightData: (() -> Unit)? = null
) : BaseBindingDF<DialogContentBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    var isOkBold = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isOkBold = isok
        isCancelable = false
    }

    override fun init(savedInstanceState: Bundle?) {

        mBinding?.apply {
            mBinding?.title?.text = titleStr
            mBinding?.content?.text = contentStr
            mBinding?.txtOk?.text = leftStr
            mBinding?.txtCancel?.text = rightStr
        }
    }

    fun onOkData() {
        onLeftData?.invoke()
        dismissAllowingStateLoss()
    }

    fun onCanceClick() {
        onRightData?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}