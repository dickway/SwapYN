package com.a.activity

import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.a.viewmodel.ABrowsingViewModel
import com.a.R
import com.a.databinding.ActivityAbrowsingBinding
import com.a.BR
import com.face.util.GVM
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.BarHelper.bar
import kotlin.jvm.java


class ABrowsingActivity : BaseBindingActivity<ActivityAbrowsingBinding, ABrowsingViewModel>(
    R.layout.activity_abrowsing,
    ABrowsingViewModel::class.java
) {


    override fun init(savedInstanceState: Bundle?) {
        mModel.getUserViewHistory()
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