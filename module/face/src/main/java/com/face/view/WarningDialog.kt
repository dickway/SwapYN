package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.BR
import com.face.databinding.DialogFacehdBinding
import com.face.databinding.DialogPrivacyBinding
import com.face.databinding.DialogWarningBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth


class WarningDialog(
    private val okClick: (() -> Unit)? = null
) : BaseBindingDF<DialogWarningBinding>(
    width = getScreenWidth(),
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    dimAmount = 0.6f,
    isBottomAnimation = false,
    cancelable = false
) {
    var checked = NotNullMutableLiveData(true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    fun onOkClick() {
        SPUtils.faceWarning = checked.value
        okClick?.invoke()
        dismissAllowingStateLoss()
    }

    fun onCheckboxClick() {
        checked.value = !checked.value
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}