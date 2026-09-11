package com.a.dialog

import android.view.Gravity
import android.view.WindowManager
import com.a.databinding.DialogAcustomBinding
import com.blankj.utilcode.util.LogUtils
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogExploreCustomBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF


class ACustomDialog @JvmOverloads constructor(
    private val onVideoAct: (() -> Unit)? = null,
    private val onPhotoAct: (() -> Unit)? = null
) : BaseBindingDF<DialogAcustomBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    fun onVideo() {
        onVideoAct?.invoke()
        dismissAllowingStateLoss()
    }

    fun onPhoto() {
        onPhotoAct?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}