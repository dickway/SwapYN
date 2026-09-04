package com.face.view

import com.face.BR
import com.face.databinding.DialogPurchaseErrorBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


/**
 * @author 再战科技
 * @date 2023/10/12
 * @description
 */
class PurchaseErrorDialog : BaseBindingDF<DialogPurchaseErrorBinding>(
    horizontalPadding = 36.dp,
    isBottomAnimation = true
) {
    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }

}

