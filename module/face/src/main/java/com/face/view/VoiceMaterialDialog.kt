package com.face.view

import android.view.Gravity
import android.view.WindowManager
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogSoundMaterialBinding
import com.face.ui.tvoice.VoiceRecordActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.openActivity


class VoiceMaterialDialog @JvmOverloads constructor(
    private val onRecordAct: (() -> Unit)? = null,
    private val onVideoidAct: (() -> Unit)? = null
) : BaseBindingDF<DialogSoundMaterialBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    fun onRecordAct() {
        dismissAllowingStateLoss()
        onRecordAct?.invoke()
    }

    fun onVideoidAct() {
        dismissAllowingStateLoss()
        onVideoidAct?.invoke()
    }

    fun onYourRecord() {
        openActivity<VoiceRecordActivity>()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}