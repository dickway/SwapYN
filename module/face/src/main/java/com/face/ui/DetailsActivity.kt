package com.face.ui

import android.os.Bundle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.face.util.GVM
import com.face.R
import com.face.viewmodel.activity.DetailsViewModel
import com.face.BR
import com.face.adapter.other.FacePageAdapter
import com.face.databinding.ActivityDetailsBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.ktx.setUpWithPaging3Adapter
import kotlinx.coroutines.flow.collectLatest

class DetailsActivity : BaseBindingActivity<ActivityDetailsBinding, DetailsViewModel>(
    R.layout.activity_details,
    DetailsViewModel::class.java
) {
    val openValue by intentExtras("openValue", 0)
    val titleStr by intentExtras("title", "")
    val type by intentExtras("type", "Topic")

    override fun init(savedInstanceState: Bundle?) {

        when (type) {
            "Banner" -> mModel.getBanner()
            "Hot" -> mModel.getHot()
            "Type" ->mModel.getType(titleStr)
            "Like" -> mModel.getUserLikeMedia()
            else ->{
                mModel.faceAdapter = FacePageAdapter(titleStr)
                mModel.getPicture(openValue)
            }
        }

        mBinding?.apply {
            title.text = titleStr.ifEmpty { getString(R.string.frament_explore_liketitle) }
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