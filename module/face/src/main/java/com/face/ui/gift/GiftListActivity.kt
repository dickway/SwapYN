package com.face.ui.gift

import android.os.Bundle
import com.face.BR
import com.face.R
import com.face.databinding.ActivityCashListBinding
import com.face.databinding.ActivityGiftListBinding
import com.face.ui.BaseBindingActivity
import com.face.util.GVM
import com.face.viewmodel.activity.CashListViewModel
import com.face.viewmodel.activity.GiftListViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity


class GiftListActivity : BaseBindingActivity<ActivityGiftListBinding, GiftListViewModel>(
    R.layout.activity_gift_list,
    GiftListViewModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
        mModel.getGifts()
    }

    fun onHis() {
        openActivity<CashListActivity>()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}