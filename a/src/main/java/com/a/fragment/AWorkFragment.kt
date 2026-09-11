package com.a.fragment

import android.os.Bundle
import com.a.R
import com.a.view.AMeGridItemDecoration
import com.a.activity.ACompletionActivity
import com.a.activity.AProductionActivity
import com.a.activity.ASwapActivity
import com.a.databinding.FragmentMeWorkBinding
import com.a.dialog.AFailYDialog
import com.a.viewmodel.AWorkViewModel
import com.face.BR
import com.face.key.AiTaskType
import com.face.ui.ProductionActivity
import com.face.ui.SwapFaceNewActivity
import com.face.util.GVM
import com.face.ui.fragment.BaseBindingFragment
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity

class AWorkFragment : BaseBindingFragment<FragmentMeWorkBinding, AWorkViewModel>(
    R.layout.fragment_me_work, AWorkViewModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
        mBinding?.recyclerView?.addItemDecoration(AMeGridItemDecoration())
        mModel.workAdapter.onItemClick = { view, taskBean, po ->
            if (taskBean?.media?.title == "owner") {
                if (taskBean.state == 3) {//点击生成失败
                    AFailYDialog(onBtnOK = {
                        SwapFaceNewActivity.jump(requireActivity(), taskBean.media.mediaId)
                    }
                    ).showIgnoreState(mActivity)
                }else if(taskBean.state == 2){//点击完成
                    openActivity<ACompletionActivity>() {
                        putString("task_id", taskBean.id)
                    }
                } else {//点击生成中
                    ProductionActivity.jump(
                        requireActivity(),
                        if (taskBean.taskType == AiTaskType.GEN_PERSONPIC) 1 else 0,
                        taskBean.id,
                        true,
                        if (taskBean.media.mediaType == ("video")) {
                            taskBean.media.webpUrl.ifEmpty {
                                taskBean.media.imageUrl
                            }
                        } else taskBean.media.imageUrl
                    )
                }
            } else {
                if (taskBean?.state == 3) {//点击生成失败
                    AFailYDialog(onBtnOK = {
                        if (taskBean.media.mediaType!="video"){
                            ASwapActivity.jump(requireActivity(), taskBean.media.mediaId)
                        }else{
                            SwapFaceNewActivity.jump(requireActivity(), taskBean.media.mediaId)
                        }
                    }
                    ).showIgnoreState(mActivity)
                }else if(taskBean?.state == 2){//点击完成
                    openActivity<ACompletionActivity>() {
                        putString("task_id", taskBean.id)
                    }
                } else {//点击生成中
                    AProductionActivity.jump(
                        requireActivity(),
                        taskBean?.id,
                        taskBean?.media?.imageUrl,
                        taskBean?.media?.notZeroWidth(),
                        taskBean?.media?.notZeroHeight()
                    )
                }
            }
        }
        GVM.INSTANT.aIsMeRefresh.observe(this) {
            if(it){
                mModel.sendAiTask()
            }
        }
    }

    fun clickMaking(){
        GVM.INSTANT.aSelectIndex.value=0
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this).addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}
