package com.face.adapter.tool

import android.graphics.Rect
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.util.GVM
import com.face.R
import com.face.adapter.history.HistoryClearTaskAdapter
import com.face.bean.ToolTaskBean
import com.face.databinding.ItemHistoryBinding
import com.face.databinding.ItemHistoryPaperworkHBinding
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter
import com.zzkj.structure.util.ktx.dp


class HistoryPaperworkAdapter : BaseMultipleAdapter<ToolTaskBean>(
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

    fun setItemOffsets(outRect: Rect, position: Int) {
        getItemViewType(position).let {
            if (it == ToolTaskBean.ITEM_TYPE_HLIST &&
                (getItemData(position)?.subList?.size ?: 0) > 0
            ) {
                outRect.set(0.dp, 20.dp, 0.dp, 20.dp)
            } else {
                outRect.set(0, 0, 0, 0)
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<ViewDataBinding> {
        val holder: BaseHolder<ViewDataBinding> = innerCreateViewHolder(
            parent,
            when (viewType) {
                ToolTaskBean.ITEM_TYPE_HLIST -> R.layout.item_history_paperwork_h
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
            ToolTaskBean.ITEM_TYPE_HLIST -> {
                bindHListData(binding as ItemHistoryPaperworkHBinding, data, position)
            }

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
        val itmeAdapter = HistoryClearTaskAdapter()
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

    private fun bindHListData(
        binding: ItemHistoryPaperworkHBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        binding.apply {
            tvTime.text = data?.name
        }
        if (binding.recyclerView.layoutManager == null) {
            binding.recyclerView.layoutManager = LinearLayoutManager(
                null, LinearLayoutManager.HORIZONTAL, false
            )
        }
        val itmeHAdapter = HistoryPaperworkHAdapter()
        itmeHAdapter.apply {
            onOperateClick = { bean ->
                bean?.let { onSingOperateClick?.invoke(it) }
            }
            onLongClick = { bean ->
                bean?.let { onLongOperateClick?.invoke(it) }
            }
        }
        binding.recyclerView.adapter = itmeHAdapter
        itmeHAdapter.submitList(data?.subList)
    }
}