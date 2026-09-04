package com.face.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.util.GVM
import com.face.viewmodel.activity.CollectViewModel
import com.face.BR
import com.face.R
import com.face.adapter.collect.CollectFragmentAdapter
import com.face.databinding.FragmentCollectResultBinding
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.setIfNot

class CollectResultFragment: BaseBindingFragment<FragmentCollectResultBinding, CollectViewModel>(
    R.layout.fragment_collect_result,
    CollectViewModel::class.java
) {

    private val type: String by lazy {
        arguments?.getString("type")?:""
    }

    companion object {
        fun newInstance(type: String): CollectResultFragment {
            val fragment = CollectResultFragment()
            val args = Bundle()
            args.putString("type", type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        mModel.type.value = type

        mModel.homeRefreshing.value = true
        mModel.getUserCollect()

        mBinding?.apply {
            recyclerView.addOnScrollListener(onScrollListener)
        }

    }


    private val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager

            val firstPositions = layoutManager.findFirstVisibleItemPositions(null)

            // 获取最小的 position（最靠上的 item）
            val firstItemPosition = firstPositions.minOrNull() ?: 0

            mBinding?.apply {

                val list = mModel.dataList.value

                if (!list.isNullOrEmpty() && firstItemPosition < list.size) {

                    tvTime.text = list[firstItemPosition].getTime()
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {}
    }

    override fun onResume() {
        super.onResume()
        mModel.getUserCollect()
    }

    private val layoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(GVM.INSTANT.faceListSpan, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.layoutManager, layoutManager)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}