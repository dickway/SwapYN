package com.face.view.sharez

import android.os.Bundle
import android.view.Gravity
import androidx.lifecycle.MutableLiveData
import com.face.BR
import com.face.databinding.DialogSharePointsBinding
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class SharePointsDialog : BaseBindingDF<DialogSharePointsBinding>(
    gravity = Gravity.CENTER,
    horizontalPadding = 48.dp,
    isBottomAnimation = false,
    cancelable = false
) {
    val points2s = MutableLiveData(SPUtils.shareGetPoints20s)
    val points3m = MutableLiveData(SPUtils.shareGetPoints3m)
    val points5m = MutableLiveData(SPUtils.shareGetPoints5m)
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

    }

    fun onCancelClick() {
        dismissAllowingStateLoss()
    }

    fun onConfirmClick() {
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
    }
}