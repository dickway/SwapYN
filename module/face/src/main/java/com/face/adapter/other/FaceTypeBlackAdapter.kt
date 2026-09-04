package com.face.adapter.other

import androidx.recyclerview.widget.DiffUtil
import com.face.R
import com.face.databinding.ItemTypeItem2Binding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class FaceTypeBlackAdapter : BaseAdapter<String, ItemTypeItem2Binding>(
    R.layout.item_type_item2, object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            TODO("Not yet implemented")
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            TODO("Not yet implemented")
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
        holder: BaseHolder<ItemTypeItem2Binding>,
        binding: ItemTypeItem2Binding,
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
