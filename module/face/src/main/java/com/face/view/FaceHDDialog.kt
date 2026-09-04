package com.face.view

import android.view.Gravity
import com.face.BR
import com.face.databinding.DialogFacehdBinding
import com.face.databinding.DialogPrivacyBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth


class FaceHDDialog (
    private val okClick: (() -> Unit)? = null
): BaseBindingDF<DialogFacehdBinding>(
    width = getScreenWidth(),
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    dimAmount = 0.6f,
    isBottomAnimation = false
) {
    var checked = NotNullMutableLiveData(false)

    fun onOkClick() {
        SPUtils.faceHD= !checked.value
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