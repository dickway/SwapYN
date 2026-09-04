package com.face.adapter.other

import androidx.recyclerview.widget.DiffUtil
import com.face.R
import com.face.databinding.ItemTypeItemBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class FaceTypeAdapter : BaseAdapter<String, ItemTypeItemBinding>(
    R.layout.item_type_item, object : DiffUtil.ItemCallback<String>() {
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
        holder: BaseHolder<ItemTypeItemBinding>,
        binding: ItemTypeItemBinding,
        data: String?,
        position: Int
    ) {
        holder.binding.apply {
            tvType.text = data
            isSelected = selectIndex == position
            root.setOnClickListener {
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
