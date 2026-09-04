package com.face.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogDisclaimerBinding
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.getScreenHeight


class DisclaimerDialog : BaseBindingDF<DialogDisclaimerBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    height= getScreenHeight()/3*2,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
        mBinding?.tvDis?.text = SPUtils.txtDisclaimer
    }

//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//        bar {
//            showNavigationBar(false)
//            setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE)
//        }
//    }

    fun onConfirmClick() {
//        onCloseApp?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}