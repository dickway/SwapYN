package com.face.ui.gift

import android.os.Bundle
import com.face.BR
import com.face.R
import com.face.bean.CaskBean
import com.face.databinding.ActivityCashSuccessBinding
import com.face.ui.BaseBindingActivity
import com.face.util.GVM
import com.face.viewmodel.activity.NullViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import kotlin.getValue


class CashSuccessActivity : BaseBindingActivity<ActivityCashSuccessBinding, NullViewModel>(
    R.layout.activity_cash_success,
    NullViewModel::class.java
) {
    val giftData by intentExtras("gift", CaskBean())


    override fun init(savedInstanceState: Bundle?) {
        GVM.INSTANT.refreshUserInfo()
        if (giftData.cardNo.isEmpty()){
            mBinding?.txt2?.text="VIP${getString(R.string.vip_time2)}----/--/--"
            mBinding?.txtBtn?.text=getString(R.string.confirm)
            GVM.INSTANT.userInfo.observe(this){
                mBinding?.apply {
                    txt2.text="VIP${getString(R.string.vip_time2)}${GVM.INSTANT.userInfo.value.vipTime()}"
                }
            }
        }
    }

    fun onBtn() {
        if (giftData.cardNo.isNotEmpty()){
            openActivity<CashDetailsActivity> {
                putParcelable("gift", giftData)
            }
        }
        finish()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}