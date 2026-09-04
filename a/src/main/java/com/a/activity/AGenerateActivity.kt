package com.a.activity

import android.os.Bundle
import com.a.R
import com.a.BR
import com.a.databinding.ActivityAgenerateBinding
import com.a.dialog.ABaseContentDialog
import com.a.dialog.BaseYDialog
import com.a.viewmodel.AGenerateViewModel
import com.face.util.GVM
import com.face.ui.BaseBindingActivity
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.openActivity
import kotlin.jvm.java

class AGenerateActivity : BaseBindingActivity<ActivityAgenerateBinding, AGenerateViewModel>(
    R.layout.activity_agenerate,
    AGenerateViewModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
//        mModel.sendAiTask()
        mModel.historyAdapter.onLongClick = { taskBean ->
            ABaseContentDialog(
                "Remove record",
                "Are you sure you want to delete this record?",
                "Delete",
                "Cancel",
                onOkData = {
                    mModel.deleteTask(taskBean)
                })
                .showIgnoreState(mActivity)
        }
        mModel.historyAdapter.onItemClick = { view, taskBean, po ->
            if (taskBean?.state == 3) {//点击生成失败
                ABaseContentDialog(
                    "Build Failure",
                    "Unexpected error happened,\\nplease try again!",
                    "Retry",
                    getString(R.string.dialog_apri_qixiao),
                    onOkData = {
                        if (taskBean.media.mediaType!="video"){
                            ASwapActivity.jump(this, taskBean.media.mediaId)
                        }else{
                            SwapFaceNewActivity.jump(this, taskBean.media.mediaId)
                        }
                    }
                ).showIgnoreState(mActivity)
            } else if (taskBean?.state == 2) {//点击完成
                openActivity<ACompletionActivity>() {
                    putString("task_id", taskBean.id)
                }
            } else {//点击生成中
                val urlSwap = if ( taskBean?.media?.mediaType == ("video")) {
                    taskBean.media.videoUrl
                } else {
                    taskBean?.media?.imageUrl
                }
                AProductionActivity.jump(
                    this,
                    taskBean?.id,
                    urlSwap,
                    taskBean?.media?.notZeroWidth(),
                    taskBean?.media?.notZeroHeight()
                )
            }
        }

    }


    override fun onResume() {
        super.onResume()
        mModel.sendAiTask()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}