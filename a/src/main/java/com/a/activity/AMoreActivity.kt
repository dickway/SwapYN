package com.a.activity

import android.os.Bundle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.a.R
import com.a.databinding.ActivityAmoreBinding
import com.a.viewmodel.AMoreViewModel
import com.face.util.GVM
import com.a.BR
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.ktx.setUpWithPaging3Adapter
import kotlinx.coroutines.flow.collectLatest

class AMoreActivity : BaseBindingActivity<ActivityAmoreBinding, AMoreViewModel>(
    R.layout.activity_amore,
    AMoreViewModel::class.java
) {
    val titleStr by intentExtras("title", "")
    val type by intentExtras("type", "Topic")

    override fun init(savedInstanceState: Bundle?) {
        when (type) {
            "Banner" -> mModel.getBanner()
            "Type" -> mModel.getType(titleStr)
        }

        mBinding?.apply {
            title.text = titleStr
        }

        launch {
            mModel.faceAdapter.loadStateFlow.collectLatest {
                mModel.refreshError.value =
                    it.refresh is LoadState.Error
            }
        }

        mBinding?.apply {
            refreshLayout.setUpWithPaging3Adapter(mModel.faceAdapter)
        }
        mModel.faceAdapter.addLoadStateListener(loadStateListener)
    }

    private val loadStateListener: (CombinedLoadStates) -> Unit = {
        if (it.refresh is LoadState.NotLoading) {
            mModel.isNoData.setIfNot(mModel.faceAdapter.itemCount == 0)
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}