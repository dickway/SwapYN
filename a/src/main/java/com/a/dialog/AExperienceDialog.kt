package com.a.dialog

import android.os.Bundle
import android.view.Gravity
import com.a.databinding.DialogAexperienceBinding
import com.a.BR
import com.a.R
import com.a.activity.AVipActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity


class AExperienceDialog @JvmOverloads constructor(
    val contentStr: String = "",
) : BaseBindingDF<DialogAexperienceBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = true
) {

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = true
        mBinding?.content?.text = contentStr
    }

    fun onConfirmClick() {
        openActivity<AVipActivity>()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}