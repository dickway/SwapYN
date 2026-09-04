package com.a.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.a.R
import com.a.databinding.FragmentMeColletBinding
import com.a.databinding.FragmentMeWorkBinding
import com.a.viewmodel.AColletViewModel
import com.a.viewmodel.AWorkViewModel
import com.face.BR
import com.face.util.GVM
import com.face.ui.fragment.BaseBindingFragment
import com.zzkj.structure.base.DataBindingArguments

class AColletFragment : BaseBindingFragment<FragmentMeColletBinding, AColletViewModel>(
    R.layout.fragment_me_collet, AColletViewModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
        GVM.INSTANT.aIsMeRefresh.observe(this) {
            if(it){
                mModel.getCollect()
            }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this).addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}