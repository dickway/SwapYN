package com.a.activity

import android.os.Bundle
import com.a.viewmodel.AToolHistoryViewModel
import com.face.util.GVM
import com.face.BR
import com.face.databinding.ActivityHistoryClearBinding
import com.face.key.AiTaskType
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras

class AToolHistoryActivity :
    BaseBindingActivity<ActivityHistoryClearBinding, AToolHistoryViewModel>(
        com.a.R.layout.activity_atool_history,
        AToolHistoryViewModel::class.java
    ) {
    val aiTaskType by intentExtras("aiTaskType", AiTaskType.LAMA_CLEANER)

    override fun init(savedInstanceState: Bundle?) {
        mModel.aiType = aiTaskType
//        mModel.getUserRecordPage()
    }

    override fun onResume() {
        super.onResume()
        mModel.getUserRecordPage()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}