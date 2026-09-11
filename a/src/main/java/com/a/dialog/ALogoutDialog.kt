package com.a.dialog

import android.view.Gravity
import android.view.WindowManager
import com.a.BR
import com.a.databinding.DialogAlogoutBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class ALogoutDialog @JvmOverloads constructor(
    private val onLogout: (() -> Unit)? = null
) : BaseBindingDF<DialogAlogoutBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    horizontalPadding = 48.dp,
    maxWidth = 360.dp,
    dimAmount = 0.6f,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = true
) {

    fun onConfirmClick() {
        onLogout?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
    }
}
