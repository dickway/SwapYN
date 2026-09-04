package com.a.fragment

import android.os.Bundle
import com.a.R
import com.a.databinding.FragmentMeHistoryBinding
import com.a.viewmodel.AHistoryViewModel
import com.face.BR
import com.face.util.GVM
import com.face.ui.fragment.BaseBindingFragment
import com.zzkj.structure.base.DataBindingArguments

class AHistoryFragment : BaseBindingFragment<FragmentMeHistoryBinding, AHistoryViewModel>(
    R.layout.fragment_me_history, AHistoryViewModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
//        mModel.getUserViewHistory()
        GVM.INSTANT.aIsMeRefresh.observe(this) {
            if(it){
                mModel.getUserViewHistory()
            }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}