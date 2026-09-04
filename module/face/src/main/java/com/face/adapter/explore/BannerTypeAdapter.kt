package com.face.adapter.explore

import androidx.recyclerview.widget.DiffUtil
import com.face.R
import com.face.databinding.ItemBannerTypeBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class BannerTypeAdapter : BaseAdapter<String, ItemBannerTypeBinding>(
    R.layout.item_banner_type, object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
           return false
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return false
        }
    }
) {

    override fun onBindData(
        holder: BaseHolder<ItemBannerTypeBinding>,
        binding: ItemBannerTypeBinding,
        data: String?,
        position: Int
    ) {
        holder.binding.apply {
            tvType.text = data
            root.setOnClickListener {
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
