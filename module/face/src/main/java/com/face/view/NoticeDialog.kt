package com.face.view

import android.view.Gravity
import android.view.WindowManager
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogNoticeBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF


class NoticeDialog @JvmOverloads constructor(
) : BaseBindingDF<DialogNoticeBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}