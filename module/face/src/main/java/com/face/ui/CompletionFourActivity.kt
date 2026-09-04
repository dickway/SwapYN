package com.face.ui

import android.os.Bundle
import com.face.util.GVM
import com.face.viewmodel.activity.CompletionFourViewModel
import com.face.R
import com.face.BR
import com.face.adapter.other.CompletionAdapter
import com.face.bean.TaskBean
import com.face.databinding.ActivityCompletionFourBinding
import com.face.util.EventUtil
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity

class CompletionFourActivity :
    BaseBindingActivity<ActivityCompletionFourBinding, CompletionFourViewModel>(
        R.layout.activity_completion_four,
        CompletionFourViewModel::class.java
    ) {

    val taskData by intentExtras("task_data", TaskBean())

    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage(
            "success_pro",
            mapOf("task_id" to taskData.id, "task_type" to taskData.taskType)
        )

        mBinding?.apply {
            val heightImg =
                (getScreenWidth() - 48.dp) / 2 * taskData.media.height / taskData.media.width//根据屏幕宽度适配图片控件高
            val adapterD =
                CompletionAdapter(heightImg, taskData)
            recyclerView.adapter = adapterD
            adapterD.submitList(taskData.result.dataWater.toMutableList())
        }

    }

    fun onBackHome() {
        openActivity<MainActivity>()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}