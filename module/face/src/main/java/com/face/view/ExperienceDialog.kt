package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.BR
import com.face.R
import com.face.databinding.DialogExperienceBinding
import com.face.ui.ShareActivity
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity


class ExperienceDialog @JvmOverloads constructor(
    val contentStr: String = "",
) : BaseBindingDF<DialogExperienceBinding>(
    horizontalPadding = 49.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
        mBinding?.content?.text = contentStr
    }

    fun onShareClick() {
        openActivity<ShareActivity>()
    }

    fun onConfirmClick() {
        VipActivity.jump(requireActivity())
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}