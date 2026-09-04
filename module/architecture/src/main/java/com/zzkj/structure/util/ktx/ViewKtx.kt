package com.zzkj.structure.util.ktx

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

/**
 * @author lmk
 * @date 2022/3/25
 * @description
 */
fun View.setVisibleIfNot(isVisible: Boolean): Boolean {
    return if (this.isVisible != isVisible) {
        this.isVisible = isVisible
        true
    } else {
        false
    }
}

fun View.singleClick(intervalMill: Int = 500, block: View.OnClickListener?) {
    setOnClickListener(object : View.OnClickListener {
        var last = 0L
        override fun onClick(v: View?) {
            if (System.currentTimeMillis() - last > intervalMill) {
                block?.onClick(v)
                last = System.currentTimeMillis()
            }
        }
    })
}


//suspend fun SwipeRefreshLayout.setUpWithPaging3Adapter(adapter: PagingDataAdapter<*, *>) {
//    setOnRefreshListener { adapter.refresh() }
//    adapter.loadStateFlow.collectLatest {
//        isRefreshing = adapter.itemCount == 0 && it.refresh is LoadState.Loading
//    }
//}
fun SwipeRefreshLayout.setUpWithPaging3Adapter(adapter: PagingDataAdapter<*, *>) {
    setOnRefreshListener { adapter.refresh() }
    adapter.addLoadStateListener { isRefreshing = it.refresh is LoadState.Loading }
}

/**
 * 自定义圆角矩形
 */
fun View.setRoundRectBg(color: Int, cornerRadius: Int) {
    background = GradientDrawable().apply {
        setColor(color)
        setCornerRadius(cornerRadius.toFloat())
    }
}

fun View.removeSelf() = (parent as? ViewGroup)?.let {
    it.removeView(this)
    true
} ?: false


fun <D : Any, B : ViewDataBinding> RecyclerView.simpleAdapter(
    layoutResId: Int,
    diffCallback: DiffUtil.ItemCallback<D> = object : DiffUtil.ItemCallback<D>() {
        override fun areItemsTheSame(oldItem: D, newItem: D): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: D, newItem: D): Boolean {
            return false
        }
    },
    setClickListener: Boolean = true,
    setLongClickListener: Boolean = false,
    onItemClick: ((View, D?, Int) -> Unit)? = null,
    onItemLongClick: ((View, D?, Int) -> Boolean)? = null,
    bindData: (B.(D?, Int) -> Unit)
) = run {
    adapter = object : BaseAdapter<D, B>(
        layoutResId = layoutResId,
        diffCallback = diffCallback,
        setClickListener = setClickListener,
        setLongClickListener = setLongClickListener,
        onItemClick = onItemClick,
        onItemLongClick = onItemLongClick
    ) {
        override fun onBindData(holder: BaseHolder<B>, binding: B, data: D?, position: Int) {
            binding.bindData(data, position)
        }
    }
}

//val RecyclerView.simpleAdapter: BaseAdapter<Any, ViewDataBinding>?
//    get() = adapter as? BaseAdapter<Any, ViewDataBinding>