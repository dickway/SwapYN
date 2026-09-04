package com.face.ui

import android.graphics.Rect
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityHistoryPaperworkBinding
import com.face.key.AiTaskType
import com.face.view.ClearMoreDialog
import com.face.view.BaseDeleteDialog
import com.face.view.BaseUpdateDialog
import com.face.viewmodel.activity.PaperworkHistoryViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras

class FunZoneHistoryActivity :
    BaseBindingActivity<ActivityHistoryPaperworkBinding, PaperworkHistoryViewModel>(
        R.layout.activity_history_paperwork,
        PaperworkHistoryViewModel::class.java
    ) {

    val aiTaskType by intentExtras("aiTaskType", AiTaskType.ID_CARD)

    private val itemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
            mModel.historyAdapter.setItemOffsets(outRect, itemPosition)
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        mModel.aiType = aiTaskType
        mModel.getUserRecordPage()
        mModel.historyAdapter.apply {
            onSingOperateClick = this@FunZoneHistoryActivity.onSingOClick
        }
        mModel.historyAdapter.onLongOperateClick = { bean ->
            if (bean?.state == 2) {
                ClearMoreDialog(
                    onDeleteAct = { onDeleteData(bean) },
                    onUpdateAct = { onUpdataData(bean) })
                    .showIgnoreState(this)
            } else {
                onDeleteData(bean)
            }
        }

        mBinding?.recyclerView?.addItemDecoration(itemDecoration)
    }


    val onSingOClick: (ToolTaskBean?) -> Unit = {
        if (it?.state == 2) {
            ClearMoreDialog(
                onDeleteAct = { onDeleteData(it) },
                onUpdateAct = { onUpdataData(it) })
                .showIgnoreState(this)
        } else {
            onDeleteData(it)
        }
    }

    private fun onUpdataData(bean: ToolTaskBean?) {
        BaseUpdateDialog(
            getString(R.string.modify_name),
            bean?.name?:"",
        ) { onUpdateData(bean, it) }.showIgnoreState(this)
    }

    private fun onDeleteData(bean: ToolTaskBean?) {
        if (bean?.state == 2 || bean?.state == 3) {
            BaseDeleteDialog(
                getString(R.string.remove_record),
                getString(R.string.remove_record_content)
            ) { mModel.deleteTask(bean) }.showIgnoreState(this)
        }
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