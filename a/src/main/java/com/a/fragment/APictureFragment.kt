package com.a.fragment

import android.os.Bundle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.a.R
import com.a.databinding.FragmentAprctureBinding
import com.a.viewmodel.APictureViewModel
import com.a.BR
import com.a.activity.ASearchActivity
import com.a.activity.AVipActivity
import com.face.util.GVM
import com.face.ui.fragment.BaseBindingFragment
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.ktx.setUpWithPaging3Adapter
import kotlinx.coroutines.flow.collectLatest


class APictureFragment : BaseBindingFragment<FragmentAprctureBinding, APictureViewModel>(
    R.layout.fragment_aprcture, APictureViewModel::class.java
) {

    override fun init(savedInstanceState: Bundle?) {
        launch {
            mModel.faceAdapter.loadStateFlow.collectLatest {
                mModel.refreshError.value = it.refresh is LoadState.Error
            }
        }
        mBinding?.apply {
            refreshLayout.setUpWithPaging3Adapter(mModel.faceAdapter)
            recyclerType.adapter = mModel.typeAdapter
        }
        mModel.getPicture()
        mModel.faceAdapter.addLoadStateListener(loadStateListener)

        mModel.typeAdapter.submitList(mModel.typeList2)
        mModel.typeAdapter.onItemClick = { _, data, _ ->
            mModel.selectedName2.value = data ?: ""
            //类型选择后回到顶部
            mModel.getPicture()
        }
    }

    private val loadStateListener: (CombinedLoadStates) -> Unit = {
        if (it.refresh is LoadState.NotLoading) {
            mModel.isNoData.setIfNot(mModel.faceAdapter.itemCount == 0)
        }
    }


    fun onVipClick() {
        GVM.INSTANT.payPage.value = "APicture"
        openActivity<AVipActivity>()
    }

    fun onSearchClick() {
        openActivity<ASearchActivity>()
    }

    private val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(GVM.INSTANT.faceListSpan, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this).addArgument(BR.layoutManager, layoutManager)
            .addArgument(BR.vm, mModel).addArgument(BR.gvm, GVM.INSTANT)
    }
}