package com.face.ui.gift

import android.os.Bundle
import androidx.lifecycle.Observer
import com.face.BR
import com.face.R
import com.face.bean.CaskBean
import com.face.bean.GiftInfoBean
import com.face.databinding.ActivityCashBinding
import com.face.net.Repository
import com.face.ui.BaseBindingActivity
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.BaseContentDialog
import com.face.viewmodel.activity.CashViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlin.getValue


class CashActivity : BaseBindingActivity<ActivityCashBinding, CashViewModel>(
    R.layout.activity_cash,
    CashViewModel::class.java
) {

    val giftData by intentExtras("gift", GiftInfoBean())


    override fun init(savedInstanceState: Bundle?) {
        if (giftData.key == "gift_use"){
            mModel.giftOverNum()
        }else{
            mModel.giftNum.value= "10"
        }

        mBinding?.apply {
            imgAm.loadImage(
                giftData.img,
                placeholderResId = com.key.R.drawable.img_default_m
            )
            txtC.text=giftData.getName()
            tvPointZ.text = giftData.aipoints.toString()
            tvGuide.text = giftData.getDescribe()
        }



        mModel.giftNum.observe(this, Observer {
            mModel.isGift.value = it.toInt() > 0
//            if (it.toInt() > 0) {
//                mBinding?.tvHintCash?.text = getString(R.string.stock) + it
//            } else {
//                mBinding?.tvHintCash?.text =
//                    getString(R.string.stock) + it + getString(R.string.stock2)
//            }
        })


        GVM.INSTANT.userInfo.observe(this, Observer {
            launch(Dispatchers.Main) {
                mBinding?.apply {
                    txtBalance.text = String.format(
                        resources.getString(R.string.balance_alpoints),
                        it.tflops.toString()
                    )
                    if (mModel.isGift.value) {
                        if (it.isVip()) {
                            txtBtn.text = String.format(
                                resources.getString(R.string.cash_aipoints),
                                giftData.aipoints.toString()
                            )
                        } else {
                            txtBtn.text = getString(R.string.cash_aipoints2)
                        }
                    } else {
                        txtBtn.text = getString(R.string.cash_aipoints3)
                    }
                }
            }
        })
    }

    fun onHis() {
        openActivity<CashListActivity>()
    }

    fun onCashClick() {

        if (!mModel.isGift.value) {
            toast(getString(R.string.cash_aipoints4))
            return
        }

        if (!GVM.INSTANT.isVip.value) {
            GVM.INSTANT.payPage.value = "Cash_Pay"
            VipActivity.jump(this@CashActivity)
            return
        }

        if (GVM.INSTANT.userInfo.value.tflops < giftData.aipoints) {
            toast(getString(R.string.cash_e))
            return
        }

        val con = String.format(
            resources.getString(R.string.note_cash_dialog),
            giftData.aipoints.toString()
        )
        BaseContentDialog(
            getString(R.string.note_dialog),
            con,
            getString(R.string.cancel),
            getString(R.string.confirm),
            false,
            onRightData = {
                saveGiftUser()
            })
            .showIgnoreState(this)
    }


    fun saveGiftUser() {
        if (giftData.key == "gift_use"){
            launchRequestWithLoadingOnIO({ Repository.saveGiftUser() }) {
                onSuccess = {
                    openActivity<CashSuccessActivity> {
                        putParcelable("gift", it as CaskBean)
                    }
                }
                onFailed = { _, errorCode, errorMsg ->
                    when (errorCode) {
                        71 -> {//库存不足
                            toast(getString(R.string.cash_aipoints3))
                        }
                        52 -> {
                            toast(getString(R.string.cash_e))
                        }
                        else -> {
                            toast(errorMsg)
                        }
                    }
                }
            }
        }else{
            launchRequestWithLoadingOnIO({ Repository.exchangeUserVip(giftData.key) }) {
                onSuccess = {
                    openActivity<CashSuccessActivity> {
                        putParcelable("gift", null)
                    }
                }
                onFailed = { _, errorCode, errorMsg ->
                    toast(errorMsg)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        GVM.INSTANT.refreshUserInfo()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}