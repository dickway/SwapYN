package com.a.activity

import android.os.Bundle
import androidx.core.view.WindowInsetsControllerCompat
import com.a.viewmodel.AVipSuccessViewModel
import com.face.util.GVM
import com.a.BR
import com.a.R
import com.a.databinding.ActivityAvipSuccessBinding
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.intentExtras


class AVipSuccessActivity : BaseBindingActivity<ActivityAvipSuccessBinding, AVipSuccessViewModel>(
    R.layout.activity_avip_success,
    AVipSuccessViewModel::class.java
) {

    private val jiage by intentExtras("jiage", "")
    override fun init(savedInstanceState: Bundle?) {
        mBinding?.tv2?.text=String.format(
            resources.getString(R.string.successfully_paid),jiage
        )
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}