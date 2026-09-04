package com.zzkj.structure.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * @author
 * @date 2022/2/11
 * @description
 */
abstract class BaseAdapter<D : Any, B : ViewDataBinding>(
    private val layoutResId: Int,
    diffCallback: DiffUtil.ItemCallback<D>,
    private val setClickListener: Boolean = true,
    private val setLongClickListener: Boolean = false,
    open var onItemClick: ((View, D?, Int) -> Unit)? = null,
    open var onItemLongClick: ((View, D?, Int) -> Boolean)? = null
) : ListAdapter<D, BaseHolder<B>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<B> {
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
        if (setClickListener) {
            holder.binding.root.setOnClickListener { view ->
                onItemClick?.apply {
                    val position = holder.bindingAdapterPosition
                    invoke(view, getItemData(position), position)
                }
            }
        }
        if (setLongClickListener) {
            holder.binding.root.setOnLongClickListener { view ->
                onItemLongClick?.run {
                    val position = holder.bindingAdapterPosition
                    invoke(view, getItemData(position), position)
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

    override fun submitList(list: List<D>?) {
        if (list.isNullOrEmpty()) {
            super.submitList(list)
        } else if (list != currentList) {
            super.submitList(ArrayList(list))
        }
    }

    override fun onBindViewHolder(holder: BaseHolder<B>, position: Int) {
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
        (holder.binding as ViewDataBinding).executePendingBindings()
    }

    override fun onBindViewHolder(
        holder: BaseHolder<B>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            onBindData(
                holder,
                holder.binding,
                getItemData(position),
                position,
                payloads
            )
        }
    }
    open fun onBindData(
        holder: BaseHolder<B>,
        binding: B,
        data: D?,
        position: Int,
        payloads: List<Any>
    ) {
        onBindData(holder, binding, data, position)
    }
    abstract fun onBindData(holder: BaseHolder<B>, binding: B, data: D?, position: Int)
}
