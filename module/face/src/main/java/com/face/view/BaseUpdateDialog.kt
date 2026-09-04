package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.util.GVM
import com.face.BR
import com.face.R
import com.face.databinding.DialogUpdateBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.toast


class BaseUpdateDialog @JvmOverloads constructor(
    val title: String="",
    val content: String="",
    private val onOkData: ((String) -> Unit)? = null
) : BaseBindingDF<DialogUpdateBinding>(
    horizontalPadding=48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        mBinding?.tvTitle?.text = title
        mBinding?.etContent?.setText(content)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    fun onConfirmClick() {
        val name= mBinding?.etContent?.text.toString()
        if (name.isEmpty()){
            toast(getStringX(R.string.not_empty))
            return
        }
        onOkData?.invoke(name)
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}