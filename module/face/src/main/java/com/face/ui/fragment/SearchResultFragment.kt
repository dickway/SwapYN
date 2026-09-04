package com.face.ui.fragment

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.net.Repository
import com.face.util.GVM
import com.face.viewmodel.activity.SearchViewModel
import com.face.BR
import com.face.R
import com.face.adapter.other.FacePageAdapter
import com.face.databinding.FragmentSearchResultBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.ktx.setUpWithPaging3Adapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchResultFragment: BaseBindingFragment<FragmentSearchResultBinding, SearchViewModel>(
    R.layout.fragment_search_result,
    SearchViewModel::class.java
) {

    private val type: String by lazy {
        arguments?.getString("type")?:""
    }

    companion object {
        fun newInstance(type: String): SearchResultFragment {
            val fragment = SearchResultFragment()
            val args = Bundle()
            args.putString("type", type)
            fragment.arguments = args
            return fragment
        }
    }

    override val mModel: SearchViewModel by lazy {
        getViewModel(SearchViewModel::class.java, requireActivity())
    }
    val isNoData = MutableLiveData<Boolean?>()
    private var isFromClear = false

    // 防止重复搜
    private var searchStep = 0

    //防止重复报
    private var eventStep = 0
    private var needScrollTop = false
    val faceAdapter by lazy { FacePageAdapter() }

    override fun init(savedInstanceState: Bundle?) {

        mBinding?.apply {
            refreshLayout.setUpWithPaging3Adapter(faceAdapter)
        }

        mModel.startSearch.observe(this, Observer {
            if (it > searchStep) {
                searchFace()
                searchStep = it
            }
        })
        faceAdapter.addLoadStateListener(loadStateListener)

        isNoData.observe(this,Observer {
            if (type == null && it != null && !isFromClear) {
                // 每次离开搜索页后回来又会调,防止重复报
                if (searchStep > eventStep) {
                    eventStep = searchStep
                }
            }
        })
    }

    fun searchFace() {
        val keywords = mModel.getKeywords()
        if (keywords.isNullOrBlank()) {
            return
        }
        isFromClear = false
        isNoData.value = null
        lifecycleScope.launch {
            if (type != null) {
                Repository.searchAi(
                    type,
                    keywords = keywords
                ).cachedIn(mModel.viewModelScope)
                    .collectLatest {
                        faceAdapter.submitData(it)
                    }
            }
        }
    }

    fun clearData() {
        isFromClear = true
        isNoData.value = null
        if (faceAdapter.itemCount > 0) {
            faceAdapter.submitData(lifecycle, PagingData.empty())
        }
    }

    private val loadStateListener: (CombinedLoadStates) -> Unit = {
//        if (it.refresh is LoadState.NotLoading) {
//            isNoData.setIfNot(faceAdapter.itemCount == 0)
//        }
        if (it.refresh is LoadState.Loading && faceAdapter.itemCount > 0) {
            needScrollTop = true
        }
        if (it.refresh is LoadState.NotLoading) {
            isNoData.setIfNot(faceAdapter.itemCount == 0)
            // 有数据时会自动滚到下面去, 设置下
            if (needScrollTop) {
                needScrollTop = false
                mBinding?.recyclerView?.scrollToPosition(0)
            }
        }
    }

    private val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(GVM.INSTANT.faceListSpan, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        faceAdapter.removeLoadStateListener(loadStateListener)
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.layoutManager, layoutManager)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}