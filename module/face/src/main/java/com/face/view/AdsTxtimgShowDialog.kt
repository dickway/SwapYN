package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.BR
import com.face.R
import com.face.ad.AdUtil
import com.face.databinding.DialogAdsshowBinding
import com.face.databinding.DialogAdstxtimgshowBinding
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity


class AdsTxtimgShowDialog @JvmOverloads constructor(
    private val onToAct: (() -> Unit)? = null,
    private val onNotAds: (() -> Unit)? = null
) : BaseBindingDF<DialogAdstxtimgshowBinding>(
    horizontalPadding = 49.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {

    var usePinot = NotNullMutableLiveData(SPUtils.useAd)

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isCancelable = false
        mBinding?.apply {
            title.text = resources.getString(R.string.dialog_ads_title2)
            content.text = String.format(
                resources.getString(R.string.dialog_ads_context2),
                SPUtils.txtimgDayNum.toString()
            )
            tvUseNum.text=String.format(
                resources.getString(R.string.consume_point),
                SPUtils.useAd.toString()
            )
        }

    }

    fun onQClick() {
        dismissAllowingStateLoss()
    }

    fun onVipClick() {
        dismissAllowingStateLoss()
        VipActivity.jump(requireActivity())
    }

    fun onNoAdsClick() {
        dismissAllowingStateLoss()
        onNotAds?.invoke()
    }


    fun onConfirmClick() {
        dismissAllowingStateLoss()
        onToAct?.invoke()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}