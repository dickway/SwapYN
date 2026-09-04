package com.face.ui.tvoice

import android.os.Bundle
import com.face.BR
import com.face.R
import com.face.bean.ToolTaskBean
import com.face.util.GVM
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.face.databinding.ActivityFailVoiceBinding
import com.face.viewmodel.activity.VoiceFailViewModel
import com.zzkj.structure.util.ktx.intentExtras


class VoiceFailActivity :
    BaseBindingActivity<ActivityFailVoiceBinding, VoiceFailViewModel>(
        R.layout.activity_fail_voice,
        VoiceFailViewModel::class.java
    ) {
    private val dataBean by intentExtras("dataBean", ToolTaskBean())
    override fun init(savedInstanceState: Bundle?) {
        mBinding?.tvHint?.text = dataBean.errorMessage
    }


    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}
