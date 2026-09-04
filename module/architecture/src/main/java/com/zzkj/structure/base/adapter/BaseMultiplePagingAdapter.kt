package com.zzkj.structure.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

/**
 * @author
 * @date 2022/3/24
 * @description
 */
abstract class BaseMultiplePagingAdapter<D : Any>(
    diffCallback: DiffUtil.ItemCallback<D>,
    open var onItemClick: ((View, D?, position: Int, type: Int) -> Unit)? = null,
    open var onItemLongClick: ((View, D?, position: Int, type: Int) -> Boolean)? = null
) : PagingDataAdapter<D, BaseHolder<ViewDataBinding>>(diffCallback) {

    fun <B : ViewDataBinding> innerCreateViewHolder(
        parent: ViewGroup,
        layoutResId: Int,
        autoSetClickListener: Boolean = true,
        autoSetLongClickListener: Boolean = false,
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
                        lifecycleOwner = parent.context as AppCompatActivity
                    }
                }
            }
        )
        if (autoSetClickListener) {
            holder.binding.root.setOnClickListener { view ->
                onItemClick?.apply {
                    val position = holder.bindingAdapterPosition
                    invoke(view, getItemData(position), position, getItemViewType(position))
                }
            }
        }
        if (autoSetLongClickListener) {
            holder.binding.root.setOnLongClickListener { view ->
                onItemLongClick?.run {
                    val position = holder.bindingAdapterPosition
                    invoke(view, getItemData(position), position, getItemViewType(position))
                } ?: false
            }
        }
        return holder
    }

    /**
     * 方便外部调用
     */
    fun getItemData(position: Int): D? {
        return try {
            super.getItem(position)
        } catch (t: Throwable) {
            null
        }
    }

    override fun onBindViewHolder(holder: BaseHolder<ViewDataBinding>, position: Int) {
        onBindData(
            holder,
            holder.binding,
            try {
                getItem(position)
            } catch (t: Throwable) {
                null
            },
            position
        )
        holder.binding.executePendingBindings()
    }

    abstract fun onBindData(
        holder: BaseHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        data: D?,
        position: Int
    )
}
