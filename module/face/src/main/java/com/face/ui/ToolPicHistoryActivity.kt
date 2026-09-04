package com.face.ui

import android.os.Bundle
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityHistoryClearBinding
import com.face.key.AiTaskType
import com.face.view.ClearMoreDialog
import com.face.view.BaseDeleteDialog
import com.face.view.BaseUpdateDialog
import com.face.viewmodel.activity.ClearHistoryViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras

class ToolPicHistoryActivity :
    BaseBindingActivity<ActivityHistoryClearBinding, ClearHistoryViewModel>(
        R.layout.activity_history_clear,
        ClearHistoryViewModel::class.java
    ) {
    val aiTaskType by intentExtras("aiTaskType", AiTaskType.LAMA_CLEANER)

    override fun init(savedInstanceState: Bundle?) {
        mModel.aiType = aiTaskType
        mModel.getUserRecordPage()
        mModel.historyAdapter.apply {
            onSingOperateClick = this@ToolPicHistoryActivity.onSingOClick
        }
        mModel.historyAdapter.onLongOperateClick = { bean ->
            ClearMoreDialog(
                onDeleteAct = { onDeleteData(bean) },
                onUpdateAct = { onUpdataData(bean) })
                .showIgnoreState(this)
        }
    }


    val onSingOClick: (ToolTaskBean?) -> Unit = {
        ClearMoreDialog(
            onDeleteAct = { onDeleteData(it) },
            onUpdateAct = { onUpdataData(it) })
            .showIgnoreState(this)
    }

    private fun onUpdataData(bean: ToolTaskBean?) {
        BaseUpdateDialog(
            getString(R.string.modify_name),
            bean?.name?:"",
        ) { onUpdateData(bean, it) }.showIgnoreState(this)
    }

    private fun onDeleteData(bean: ToolTaskBean?) {
        BaseDeleteDialog(
            getString(R.string.remove_record),
            getString(R.string.remove_record_content)
        ) { mModel.deleteTask(bean) }.showIgnoreState(this)
    }

    private fun onUpdateData(bean: ToolTaskBean?, name: String) {
        mModel.updataUserGenerateRecords(bean?.id, name)
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}