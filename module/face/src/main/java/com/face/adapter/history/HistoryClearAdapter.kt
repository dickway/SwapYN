package com.face.adapter.history

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.util.GVM
import com.face.R
import com.face.bean.ToolTaskBean
import com.face.databinding.ItemHistoryBinding
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter


class HistoryClearAdapter : BaseMultipleAdapter<ToolTaskBean>(
    object : DiffUtil.ItemCallback<ToolTaskBean>() {
        override fun areItemsTheSame(
            oldItem: ToolTaskBean,
            newItem: ToolTaskBean
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: ToolTaskBean,
            newItem: ToolTaskBean
        ): Boolean {
            return false
        }
    }
) {
    var onSingOperateClick: ((ToolTaskBean?) -> Unit)? = null
    var onLongOperateClick: ((ToolTaskBean?) -> Unit)? = null

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
                ToolTaskBean.ITEM_TYPE_LIST -> R.layout.item_history
                else -> R.layout.item_history
            }
        )
        return holder
    }

    override fun onBindData(
        holder: BaseHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        when (getItemViewType(position)) {
            ToolTaskBean.ITEM_TYPE_LIST -> {
                bindListData(binding as ItemHistoryBinding, data, position)
            }
        }
    }

    private fun bindListData(
        binding: ItemHistoryBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        binding.apply {
            tvTime.text = data?.name
        }
        if (binding.recyclerView.layoutManager == null) {
            binding.recyclerView.layoutManager =
                StaggeredGridLayoutManager(
                    GVM.INSTANT.faceListSpan,
                    StaggeredGridLayoutManager.VERTICAL
                )
        }
        var itmeAdapter = HistoryClearTaskAdapter()
        binding.recyclerView.adapter = itmeAdapter
        itmeAdapter.apply {
            onOperateClick = { bean ->
                bean?.let { onSingOperateClick?.invoke(it) }
            }
            onLongClick = { bean ->
                bean?.let { onLongOperateClick?.invoke(it) }
            }
        }
        itmeAdapter.submitList(data?.subList)
    }

}