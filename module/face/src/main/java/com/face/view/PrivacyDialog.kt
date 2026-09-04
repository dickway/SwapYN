package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.face.BR
import com.face.R
import com.face.databinding.DialogPrivacyBinding
import com.face.ui.tcustom.CustomStartActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth


class PrivacyDialog (
    private val okClick: (() -> Unit)? = null
): BaseBindingDF<DialogPrivacyBinding>(
    width = getScreenWidth(),
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    dimAmount = 0.6f,
    isBottomAnimation = false
) {
    var checked = NotNullMutableLiveData(false)

    fun onOkClick() {
        SPUtils.privacyHint= checked.value == false
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