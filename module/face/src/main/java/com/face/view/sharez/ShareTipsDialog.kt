package com.face.view.sharez

import android.os.Bundle
import android.view.Gravity
import com.face.BR
import com.face.R
import com.face.ad.AdUtil
import com.face.databinding.DialogAdsshowBinding
import com.face.databinding.DialogShareTipsBinding
import com.face.ui.SearchActivity
import com.face.ui.VipActivity
import com.face.ui.share.ShareMeActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity


class ShareTipsDialog @JvmOverloads constructor(
    private val isFirst: Boolean = false
) : BaseBindingDF<DialogShareTipsBinding>(
    horizontalPadding = 48.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

    }

    fun onCancelClick() {
        dismissAllowingStateLoss()
    }

    fun onConfirmClick() {
        if(isFirst){
            openActivity<ShareMeActivity>()
        }
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
    }
}