package com.face.view

import android.os.Bundle
import android.view.Gravity
import com.face.BR
import com.face.R
import com.face.ad.AdUtil
import com.face.databinding.DialogAdsvippopershowBinding
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getStringX


class AdsVipPoperShowDialog @JvmOverloads constructor(
    private val onToAct: (() -> Unit)? = null,
    private val onNotAds: (() -> Unit)? = null
) : BaseBindingDF<DialogAdsvippopershowBinding>(
    horizontalPadding = 49.dp,
    gravity = Gravity.CENTER,
    isBottomAnimation = false,
    cancelable = false
) {

    var usePinot = NotNullMutableLiveData(SPUtils.useAd)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            title.text= getStringX(R.string.dialog_paper_ads)
            content.text = getStringX(R.string.dialog_paper_ads_txt)
           tvUseNum.text=String.format(
                resources.getString(R.string.consume_point),
                SPUtils.useAd.toString()
            )
        }
    }
    fun onNoAdsClick() {
        onNotAds?.invoke()
        dismissAllowingStateLoss()
    }
    fun onQClick() {
        dismissAllowingStateLoss()
    }

    fun onConfirmClick() {
        onToAct?.invoke()
        dismissAllowingStateLoss()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}