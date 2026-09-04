package com.a.activity

import android.os.Bundle
import com.a.viewmodel.ACollectViewModel
import com.a.R
import com.a.databinding.ActivityAcollectBinding
import com.a.BR
import com.face.util.GVM
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import kotlin.jvm.java


class ACollectActivity : BaseBindingActivity<ActivityAcollectBinding, ACollectViewModel>(
    R.layout.activity_acollect,
    ACollectViewModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
        mModel.getCollect()
    }

    override fun onResume() {
        super.onResume()
        mModel.getCollect()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}