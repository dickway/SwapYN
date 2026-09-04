package com.face.ui.gift

import android.os.Bundle
import com.face.BR
import com.face.R
import com.face.databinding.ActivityCashListBinding
import com.face.ui.BaseBindingActivity
import com.face.util.GVM
import com.face.viewmodel.activity.CashListViewModel
import com.zzkj.structure.base.DataBindingArguments


class CashListActivity : BaseBindingActivity<ActivityCashListBinding, CashListViewModel>(
    R.layout.activity_cash_list,
    CashListViewModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
        mModel.userGifts()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}