package com.face.ui

import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.BR
import com.face.R
import com.face.databinding.ActivityBrowsingBinding
import com.face.util.GVM
import com.face.view.BaseDeleteDialog
import com.face.viewmodel.activity.BrowsingViewModel
import com.zzkj.structure.base.DataBindingArguments


class BrowsingActivity : BaseBindingActivity<ActivityBrowsingBinding, BrowsingViewModel>(
    R.layout.activity_browsing,
    BrowsingViewModel::class.java
) {


    override fun init(savedInstanceState: Bundle?) {
        mModel.getUserViewHistory()
    }

    fun deleteAll(){
        BaseDeleteDialog(
            getString(R.string.remove_record),
            getString(R.string.remove_record_content1)
        ) { mModel.deleteTask() }.showIgnoreState(this)
    }

    private val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.layoutManager, layoutManager)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}