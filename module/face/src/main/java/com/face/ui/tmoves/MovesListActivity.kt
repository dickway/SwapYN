package com.face.ui.tmoves

import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.BR
import com.face.R
import com.face.databinding.ActivityBrowsingBinding
import com.face.databinding.ActivityMovesListBinding
import com.face.key.AiTaskType
import com.face.ui.BaseBindingActivity
import com.face.ui.FunZoneHistoryActivity
import com.face.util.GVM
import com.face.view.BaseDeleteDialog
import com.face.viewmodel.activity.BrowsingViewModel
import com.face.viewmodel.activity.MovesListViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.openActivity


class MovesListActivity : BaseBindingActivity<ActivityMovesListBinding, MovesListViewModel>(
    R.layout.activity_moves_list,
    MovesListViewModel::class.java
) {


    override fun init(savedInstanceState: Bundle?) {
        mModel.getUserViewHistory()
    }

    fun toRecord() {
        openActivity<FunZoneHistoryActivity> {
            putString("aiTaskType", AiTaskType.IMG2VIDEO)
        }
    }

    private val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.layoutManager, layoutManager)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}