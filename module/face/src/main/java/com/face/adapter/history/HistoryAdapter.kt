package com.face.adapter.history

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.face.util.GVM
import com.face.R
import com.face.adapter.explore.ExploreLikeAdapter
import com.face.bean.TaskBean
import com.face.databinding.ItemHistoryBinding
import com.face.databinding.ItemHistoryHBinding
import com.face.databinding.ItemHistoryTxtBinding
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter


class HistoryAdapter : BaseMultipleAdapter<TaskBean>(
    object : DiffUtil.ItemCallback<TaskBean>() {
        override fun areItemsTheSame(oldItem: TaskBean, newItem: TaskBean): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: TaskBean, newItem: TaskBean): Boolean {
            return false
        }
    }
) {
    var onLongMianClick: ((TaskBean?) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return getItemData(position)?.itemType ?: 0
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<ViewDataBinding> {
        val holder: BaseHolder<ViewDataBinding> = innerCreateViewHolder(
            parent,
            when (viewType) {
                TaskBean.ITEM_TYPE_TEXT -> R.layout.item_history_txt
                TaskBean.ITEM_TYPE_LIST -> R.layout.item_history
                TaskBean.ITEM_TYPE_HLIST -> R.layout.item_history_h
                else -> R.layout.item_history
            }
        )
        return holder
    }

    override fun onBindData(
        holder: BaseHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        data: TaskBean?,
        position: Int
    ) {
        when (getItemViewType(position)) {
            TaskBean.ITEM_TYPE_TEXT -> {
                bindTextData(binding as ItemHistoryTxtBinding, data, position)
            }

            TaskBean.ITEM_TYPE_LIST -> {
                bindListData(binding as ItemHistoryBinding, data, position)
            }

            TaskBean.ITEM_TYPE_HLIST -> {
                bindHListData(binding as ItemHistoryHBinding, data, position)
            }
        }
    }

    private fun bindTextData(
        binding: ItemHistoryTxtBinding,
        data: TaskBean?,
        position: Int
    ) {
        binding.isTask = (data?.subList?.size?:0) > 0
        binding.apply {
            tvName.text = data?.name
        }
    }

    private fun bindListData(
        binding: ItemHistoryBinding,
        data: TaskBean?,
        position: Int
    ) {
        binding.apply {
            tvTime.text = data?.name
        }
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(
                GVM.INSTANT.faceListSpan,
                StaggeredGridLayoutManager.VERTICAL
            )
        val itemAdapter = HistoryFaceAdapter()
        itemAdapter.apply {
            onLongClick = {  bean ->
                bean?.let { onLongMianClick?.invoke(it) }
            }
        }

        binding.recyclerView.adapter = itemAdapter
        itemAdapter.submitList(data?.subList)
    }


    private fun bindHListData(
        binding: ItemHistoryHBinding,
        data: TaskBean?,
        position: Int
    ) {
        binding.apply {
            tvTime.text = data?.name
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(
            null, LinearLayoutManager.HORIZONTAL, false
        )
        val itemHAdapter = HistoryFaceHAdapter()
        binding.recyclerView.adapter = itemHAdapter
        itemHAdapter.submitList(data?.subList)

    }
}