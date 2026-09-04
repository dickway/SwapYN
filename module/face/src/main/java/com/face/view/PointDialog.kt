package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.BR
import com.face.databinding.DialogPointBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.dp


class PointDialog @JvmOverloads constructor(
    val titleStr: String = "",
    val contentStr: String = "",
    val pointNum: Int = 0,
    val cardNum: Int = 0,
) : BaseBindingDF<DialogPointBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    dimAmount = 0.6f,
    isBottomAnimation = false,
    cancelable = false
) {
    val showCard = NotNullMutableLiveData(false)
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        if (cardNum > 0) showCard.value=true
        mBinding?.apply {
            tvPoint.text = "+$pointNum"
            tvCard.text = "+$cardNum"
            title.text = titleStr
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}