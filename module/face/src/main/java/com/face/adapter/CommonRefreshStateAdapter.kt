package com.face.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.face.R
import com.face.databinding.ItemLoadingStatusBinding
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseLoadStateAdapter

/**
 * @author 再战科技
 * @date 2022/8/25
 * @description
 */
class CommonRefreshStateAdapter(
    private val retry: (() -> Unit)? = null
) : BaseLoadStateAdapter<ItemLoadingStatusBinding>(R.layout.item_loading_status) {

    /**
     * 解决 StaggeredGridLayoutManager 加载更多时，加载动画导致最后一个item没有占满屏幕宽度问题
     * @param holder
     */
    override fun onViewAttachedToWindow(holder: BaseHolder<ItemLoadingStatusBinding>) {
        super.onViewAttachedToWindow(holder)
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder)
        }
    }

    private fun isStaggeredGridLayout(holder: RecyclerView.ViewHolder): Boolean {
        val layoutParams: ViewGroup.LayoutParams = holder.itemView.layoutParams
        return layoutParams is StaggeredGridLayoutManager.LayoutParams
    }

    private fun handleLayoutIfStaggeredGridLayout(
        holder: RecyclerView.ViewHolder
    ) {
        val p: StaggeredGridLayoutManager.LayoutParams =
            holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
        p.isFullSpan = true
    }


    override fun onBindViewHolder(
        holder: BaseHolder<ItemLoadingStatusBinding>,
        binding: ItemLoadingStatusBinding,
        loadState: LoadState
    ) {
        binding.tvLoading.isVisible = loadState is LoadState.Loading
        binding.tvRetry.isVisible = loadState is LoadState.Error
        binding.root.setOnClickListener {
            if (loadState is LoadState.Error) {
                retry?.invoke()
            }
        }
    }

}