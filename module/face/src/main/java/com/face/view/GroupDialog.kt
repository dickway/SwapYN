package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.face.util.GVM
import com.face.BR
import com.face.databinding.DialogExploreCustomBinding
import com.face.databinding.DialogGroupBinding
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF


class GroupDialog @JvmOverloads constructor(
    private val onWhatapp: (() -> Unit)? = null,
    private val onTelegram: (() -> Unit)? = null,
    private val onDiscord: (() -> Unit)? = null
) : BaseBindingDF<DialogGroupBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        mBinding?.apply {
            if (SPUtils.txtWhatapp == "1") {
                whatapp.visibility = View.GONE
                tvWhatapp.visibility = View.GONE
            }
            if (SPUtils.txtTelegram == "1") {
                telegram.visibility = View.GONE
                tvTelegram.visibility = View.GONE
            }
            if (SPUtils.txtDiscord == "1") {
                discord.visibility = View.GONE
                tvDiscord.visibility = View.GONE
            }
        }
    }

    fun onWhatapp() {
        onWhatapp?.invoke()
        dismissAllowingStateLoss()
    }

    fun onTelegram() {
        onTelegram?.invoke()
        dismissAllowingStateLoss()
    }

    fun onDiscord() {
        onDiscord?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}