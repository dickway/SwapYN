package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.face.util.GVM
import com.face.BR
import com.face.R
import com.face.bean.BuySlotBean
import com.face.bean.VideoAipointBean
import com.face.databinding.DialogBugSlotsBinding
import com.face.databinding.DialogFrameHintBinding
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.ktx.singleClick
import com.zzkj.structure.util.moshi.MoshiHelper


class BugSlotsDialog @JvmOverloads constructor(
    private val title: String = "",
    private val onBuy: ((Int) -> Unit)? = null
) : BaseBindingDF<DialogBugSlotsBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true
) {
    val buySlotsList = MoshiHelper.listAdapter(BuySlotBean::class.java).fromJson(SPUtils.buySlots)
        ?: mutableListOf()
    var buyNum = 0
    var buyPoint = 0
    var isSelect = NotNullMutableLiveData(true)
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        mBinding?.apply {
            bugHint.text = String.format(
                resources.getString(R.string.extra_slots),
                title
            )
            buyNum = buySlotsList.getOrNull(0)?.num ?: 0
            buyPoint= buySlotsList.getOrNull(0)?.point ?: 0
            tvNum1.text = buySlotsList.getOrNull(0)?.num.toString()
            tvQuota1.text = buySlotsList.getOrNull(0)?.point.toString()

            tvNum2.text = buySlotsList.getOrNull(1)?.num.toString()
            tvQuota2.text = buySlotsList.getOrNull(1)?.point.toString()
            tvQuota3.text = buySlotsList.getOrNull(1)?.oldPoint.toString()
            con1.singleClick {
                buyPoint= buySlotsList.getOrNull(0)?.point ?: 0
                buyNum = buySlotsList.getOrNull(0)?.num ?: 0
                isSelect.value = true
            }
            con2.singleClick {
                buyPoint= buySlotsList.getOrNull(1)?.point ?: 0
                buyNum = buySlotsList.getOrNull(1)?.num ?: 0
                isSelect.value = false
            }
        }
    }

    fun onConfirmClick() {
        val points=GVM.INSTANT.userInfo.value.tflops
        if (points<buyPoint){
            GVM.INSTANT.payPage.value = "Buy_AIPoints_Quota"
            NotAIPointsDialog().showIgnoreState(mActivity)
        }else{
            onBuy?.invoke(buyNum)
            dismissAllowingStateLoss()
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}