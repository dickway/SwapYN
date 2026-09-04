package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.face.BR
import com.face.databinding.DialogUphintBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF

/**
 * @author 再战科技
 * @date 2022/4/19
 * @description
 */
class UpHintDialog : BaseBindingDF<DialogUphintBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.CENTER,
    dimAmount = 0.6F,
    isBottomAnimation = false
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}