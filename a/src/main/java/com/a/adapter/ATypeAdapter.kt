package com.a.adapter

import androidx.recyclerview.widget.DiffUtil
import com.a.R
import com.a.databinding.ItemATypeBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class ATypeAdapter : BaseAdapter<String, ItemATypeBinding>(
    R.layout.item_a_type, object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return false
        }
    }
) {

    var selectIndex = 0
        set(value) {
            if (value != field) {
                val origin = field
                field = value
                notifyItemChanged(origin)
                notifyItemChanged(field)
            }
        }

    override fun onBindData(
        holder: BaseHolder<ItemATypeBinding>,
        binding: ItemATypeBinding,
        data: String?,
        position: Int
    ) {
        holder.binding.apply {
            tvType.text = data
            isSelected = selectIndex == position
            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
