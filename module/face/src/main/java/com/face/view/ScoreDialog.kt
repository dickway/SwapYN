package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.face.BR
import com.face.databinding.DialogExitSettingBinding
import com.face.databinding.DialogScoreBinding
import com.face.util.GVM
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF

class ScoreDialog @JvmOverloads constructor(
    val score: Float? = 0f,
) : BaseBindingDF<DialogScoreBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true,
    cancelable = false
) {

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        mBinding?.tvScore?.text = score.toString()
        mBinding?.starRating?.setRating(score ?: 0f)
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}