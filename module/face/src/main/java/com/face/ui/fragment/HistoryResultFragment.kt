package com.face.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.face.util.GVM
import com.face.viewmodel.activity.HistoryViewModel
import com.face.BR
import com.face.R
import com.face.bean.TaskBean
import com.face.bean.TaskBean.Companion.ITEM_TYPE_TEXT
import com.face.databinding.FragmentHistoryResultBinding
import com.face.view.DeteleTaskDialog
import com.zzkj.structure.base.DataBindingArguments

class HistoryResultFragment : BaseBindingFragment<FragmentHistoryResultBinding, HistoryViewModel>(
    R.layout.fragment_history_result,
    HistoryViewModel::class.java
) {

    private val type: String by lazy {
        arguments?.getString("type")?:""
    }

    companion object {
        fun newInstance(type: String): HistoryResultFragment {
            val fragment = HistoryResultFragment()
            val args = Bundle()
            args.putString("type", type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        mModel.mediaType.value = type
        mBinding?.apply {
            recyclerView.addOnScrollListener(onScrollListener)
        }
        mModel.historyAdapter.apply {
            onLongMianClick = this@HistoryResultFragment.onLongClick
        }
        mModel.homeRefreshing.postValue(true)
        mModel.sendAiTask()
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager: RecyclerView.LayoutManager? = recyclerView.layoutManager;
            // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
            val linearManager = layoutManager as LinearLayoutManager
            //获取第一个可见view的位置
            val firstItemPosition = linearManager.findFirstVisibleItemPosition()

            mBinding?.apply {
                if (firstItemPosition < 1) {
                    tvTime.visibility = View.GONE
                } else {
                    tvTime.visibility = View.VISIBLE
                }
            }
            mBinding?.apply {
                mModel.historyFaceList.value?.apply {
                    if (size > firstItemPosition && get(firstItemPosition).itemType != ITEM_TYPE_TEXT)
                        tvTime.text = get(firstItemPosition).name
                }
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        }
    }
    private val onLongClick: (TaskBean?) -> Unit = {
        DeteleTaskDialog { onDeleteData(it) }.showIgnoreState(activity)
    }

    private fun onDeleteData(bean: TaskBean?) {
        mModel.deleteTask(bean)
    }

    override fun onResume() {
        super.onResume()
        mModel.sendAiTask()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}