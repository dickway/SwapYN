package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.apkfuns.logutils.LogUtils
import com.face.BR
import com.face.R
import com.face.ad.AdUtil
import com.face.databinding.DialogAdsvipshowBinding
import com.face.databinding.DialogViplifetimeBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp


class VipLifeTimeDialog @JvmOverloads constructor(var jifen: String = "") :
    BaseBindingDF<DialogViplifetimeBinding>(
        horizontalPadding = 49.dp,
        gravity = Gravity.CENTER,
        isBottomAnimation = false,
        cancelable = false
    ) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        mBinding?.txtViplife4?.text = String.format(
            getString(R.string.viplife4), jifen
        )
    }

    fun onConfirmClick() {
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}