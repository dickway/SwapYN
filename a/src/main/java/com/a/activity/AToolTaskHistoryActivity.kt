package com.a.activity

import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.a.databinding.ActivityAtoolTaskhistoryBinding
import com.a.dialog.ABaseContentDialog
import com.a.viewmodel.AToolTaskHistoryViewModel
import com.face.util.GVM
import com.face.BR
import com.face.bean.ToolTaskBean
import com.face.key.AiTaskType
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras

class AToolTaskHistoryActivity :
    BaseBindingActivity<ActivityAtoolTaskhistoryBinding, AToolTaskHistoryViewModel>(
        com.a.R.layout.activity_atool_taskhistory,
        AToolTaskHistoryViewModel::class.java
    ) {

    val aiTaskType by intentExtras("aiTaskType", AiTaskType.ID_CARD)

    override fun init(savedInstanceState: Bundle?) {
        mModel.aiType = aiTaskType
        mModel.historyAdapter.onOperateClick = { bean ->
            onDeleteData(bean)
        }
    }

    override fun onResume() {
        super.onResume()
        mModel.getUserRecordPage()
    }

    private fun onDeleteData(bean: ToolTaskBean?) {
        if (bean?.state == 2 || bean?.state == 3) {
            ABaseContentDialog(
                "Remove record",
                "Are you sure you want to delete this record?",
                "Delete",
                "Cancel",
                onOkData = {
                    mModel.deleteTask(bean)
                })
                .showIgnoreState(this)
        }
    }

    private val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(GVM.INSTANT.faceListSpan, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
            .addArgument(com.a.BR.layoutManager, layoutManager)
    }
}