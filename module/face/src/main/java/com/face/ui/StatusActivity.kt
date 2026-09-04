package com.face.ui

import android.os.Bundle
import com.face.util.GVM
import com.face.viewmodel.activity.StatusViewModel
import com.face.R
import com.face.BR
import com.face.bean.TaskBean
import com.face.databinding.ActivityStatusBinding
import com.face.util.EventUtil
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras

class StatusActivity : BaseBindingActivity<ActivityStatusBinding, StatusViewModel>(
    R.layout.activity_status,
    StatusViewModel::class.java
) {
    val taskData by intentExtras("task_data", TaskBean())
    override fun init(savedInstanceState: Bundle?) {

        mModel.queryAiTask(taskData.id)
        EventUtil.inPage(
            "aitask_fail",
            mapOf("task_id" to taskData.id, "ee" to taskData.ee, "task_type" to taskData.taskType)
        )

        val widths = (getScreenWidth() - 92.dp)//控件最大宽度
        val heights = (getScreenHeight() - 280.dp)//控件最大高度，280手动去布局加的
        mBinding?.imgCompletion?.apply {
            loadImage(
                taskData.media.webpUrl.ifEmpty { taskData.media.imageUrl },
                placeholderResId = com.key.R.drawable.img_default_m
            )

            if (taskData.media.width >= taskData.media.height) {//根据图片大小优先显示宽
                val height = widths * taskData.media.height / taskData.media.width

                layoutParams.width = widths
                layoutParams.height = height
                if (height > heights) {//根据控件最大值优先显示高
                    layoutParams.width = heights * taskData.media.width / taskData.media.height
                    layoutParams.height = heights
                }
            } else {//根据图片大小优先显示高
                val width = heights * taskData.media.width / taskData.media.height
                layoutParams.width = width
                layoutParams.height = heights
                if (width > widths) {//根据控件最大值优先显示宽
                    layoutParams.width = widths
                    layoutParams.height = widths * taskData.media.height / taskData.media.width
                }
            }
        }


    }

    fun setRetry() {
        SwapFaceNewActivity.jump(this@StatusActivity, taskData.media.mediaId)
    }


    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}