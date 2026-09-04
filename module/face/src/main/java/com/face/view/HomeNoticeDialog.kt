package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogHomeNoticeBinding
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class HomeNoticeDialog @JvmOverloads constructor(
    val titleStr: String = "",
    val contentStr: String = "",
    val confirmStr: String = "",
) : BaseBindingDF<DialogHomeNoticeBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {

    override fun init(savedInstanceState: Bundle?) {
        SPUtils.noticeLook = true
        mBinding?.apply {
            mBinding?.tvTitle?.text = titleStr
            mBinding?.tvContent?.text = contentStr
            mBinding?.btnConfirm?.text = confirmStr
        }
    }

    fun onOkData() {
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}