package com.face.view

import android.os.Bundle
import com.face.BR
import com.face.databinding.DialogHintBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp

class BaseHintDialog @JvmOverloads constructor(
    val title: String = "",
    val content: String = "",
    val btnStr: String = "",
    private val onBtn: (() -> Unit)? = null
)  : BaseBindingDF<DialogHintBinding>(
    horizontalPadding = 48.dp,
    isBottomAnimation = false
) {
    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }

    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            mBinding?.textView2?.text = title
            mBinding?.textView3?.text = content
            mBinding?.btn?.text = btnStr
        }
    }

    fun onOkData() {
        onBtn?.invoke()
        dismissAllowingStateLoss()
    }
}

