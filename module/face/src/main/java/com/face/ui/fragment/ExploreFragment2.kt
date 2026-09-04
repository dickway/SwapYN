package com.face.ui.fragment

import android.os.Bundle
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import androidx.recyclerview.widget.RecyclerView
import com.face.BR
import com.face.R
import com.face.databinding.FragmentExplore2Binding
import com.face.util.GVM
import com.face.viewmodel.fragment.ExploreViewModel2
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.BarHelper.bar


class ExploreFragment2 : BaseBindingFragment<FragmentExplore2Binding, ExploreViewModel2>(
    R.layout.fragment_explore2,
    ExploreViewModel2::class.java
) {


    override fun init(savedInstanceState: Bundle?) {
        bar { transparent() }
        mModel.getData()
        mBinding?.apply {
            recyclerView.addOnScrollListener(onScrollListener)
        }
    }


    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (GVM.INSTANT.isShowTool.value != 0) {
                when (newState) {
                    SCROLL_STATE_IDLE -> {//不滚动时的状态，通常会在滚动停止时监听到此状态
                    }

                    SCROLL_STATE_TOUCH_SCROLL -> {//正在滚动的状态
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            //获取屏幕第一个item是多少
//            val layoutManager: LinearLayoutManager =
//                recyclerView.layoutManager as LinearLayoutManager
//            val firstItemPosition = layoutManager.findFirstVisibleItemPosition()
        }
    }


    override fun onDestroyView() {
        mBinding?.refreshLayout?.apply {
            setOnRefreshListener(null)
            setOnLoadMoreListener(null)
        }
        mBinding?.recyclerView?.apply {
            layoutManager = null
            adapter = null
        }
        super.onDestroyView()
    }


    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}