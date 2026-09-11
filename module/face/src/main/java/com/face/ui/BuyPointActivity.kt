package com.face.ui

import android.os.Bundle
import com.android.billingclient.api.BillingClient
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.R
import com.face.databinding.ActivityBuypointBinding
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.GooglePayUtil
import com.face.view.PointDialog
import com.face.view.PurchaseErrorDialog
import com.face.viewmodel.activity.BuypointViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.toast


class BuyPointActivity : BaseBindingActivity<ActivityBuypointBinding, BuypointViewModel>(
    R.layout.activity_buypoint,
    BuypointViewModel::class.java
) {


    override fun init(savedInstanceState: Bundle?) {
        bar {
            transparent()
        }
        EventUtil.inPage("BuyAIPoints")
        mModel.getTFLOPConfigs()
        mModel.buyAdapter.onItemClick = { _, bean, position ->
            mModel.buyAdapter.selectIndex = position
            mModel.selectedScheme.value = bean
        }

        GVM.INSTANT.userInfo.observe(this) {
            if (it.tflops > mModel.oldPoint&& mModel.isDialog) {//积分有变化就弹窗提示
                PointDialog(
                    titleStr = getString(R.string.purchase_success),
                    cardNum = 0,
                    pointNum = it.tflops - mModel.oldPoint
                ).showIgnoreState(this)
                mModel.oldPoint = it.tflops
            }
        }
    }

    fun onLaunchPayClick() {
        val selectedBean = mModel.selectedScheme.value ?: return
        GooglePayUtil.launchPay(selectedBean) { code, s ->
            mActivity?.dismissLoading()
            when (code) {
                BillingClient.BillingResponseCode.OK -> {
                    mModel.isDialog=true
                    GVM.INSTANT.refreshUserInfo {
                        if (it) {
                            toast(s)
                        } else {
                            toast("Auto refresh failed")
                        }
                    }
                }

                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    PurchaseErrorDialog().showIgnoreState(this)
                }

                else -> {
                    toast(s)
                }
            }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}