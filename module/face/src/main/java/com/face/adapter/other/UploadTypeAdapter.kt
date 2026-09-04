package com.face.adapter.other

import androidx.recyclerview.widget.DiffUtil
import com.face.R
import com.face.databinding.ItemUploadTypeBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class UploadTypeAdapter : BaseAdapter<String, ItemUploadTypeBinding>(
    R.layout.item_upload_type, object : DiffUtil.ItemCallback<String>() {
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
        holder: BaseHolder<ItemUploadTypeBinding>,
        binding: ItemUploadTypeBinding,
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
