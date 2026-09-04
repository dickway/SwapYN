package com.a.dialog

import android.view.Gravity
import android.view.WindowManager
import com.a.BR
import com.a.databinding.DialogAlogoutBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class ALogoutDialog @JvmOverloads constructor(
    private val onLogout: (() -> Unit)? = null
) : BaseBindingDF<DialogAlogoutBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = true
) {

//    override fun init(savedInstanceState: Bundle?) {
//        super.init(savedInstanceState)
//        isCancelable = true
//    }

    fun onConfirmClick() {
        onLogout?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}