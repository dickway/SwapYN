package com.zzkj.structure.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.blankj.utilcode.util.LogUtils

/**
 * @author lmk
 * @date 2022/2/11
 * @description
 */
abstract class BaseLoadStateAdapter<B : ViewDataBinding>(
    private val layoutResId: Int,
    retry: (() -> Unit)? = null
) : LoadStateAdapter<BaseHolder<B>>() {

    companion object {
        const val VIEW_ITEM_TYPE = 100
    }

    override fun onBindViewHolder(holder: BaseHolder<B>, loadState: LoadState) {
        onBindViewHolder(holder, holder.binding, loadState)
    }

    abstract fun onBindViewHolder(holder: BaseHolder<B>, binding: B, loadState: LoadState)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): BaseHolder<B> {
        val holder: BaseHolder<B> = BaseHolder(
            DataBindingUtil.inflate<B>(
                LayoutInflater.from(parent.context),
                layoutResId,
                parent,
                false
            ).apply {
                try {
                    val findFragment = parent.findFragment<Fragment>()
                    lifecycleOwner = findFragment.viewLifecycleOwner
                } catch (e: Exception) {
                    if (parent.context is AppCompatActivity) {
                        LogUtils.i(parent.context)
                        lifecycleOwner = parent.context as AppCompatActivity
                    }
                }
            }
        )
        onBindViewHolder(holder, holder.binding, loadState)
        return holder
    }

    override fun getStateViewType(loadState: LoadState):Int{
        LogUtils.i("getStateViewType")
        return VIEW_ITEM_TYPE
    }
}