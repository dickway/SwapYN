package com.a.adapter

import com.a.R
import com.a.databinding.ItemAcartoonHBinding
import com.bumptech.glide.Glide
import com.face.bean.StyleBean
import com.face.databinding.ItemStyleHBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder


class ACartoonAdapter : BaseAdapter<StyleBean, ItemAcartoonHBinding>(
   R.layout.item_acartoon_h, StyleBean.differCallback
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
        holder: BaseHolder<ItemAcartoonHBinding>,
        binding: ItemAcartoonHBinding,
        data: StyleBean?,
        position: Int
    ) {
        holder.binding.apply {
            isSelected = selectIndex == position
            tvName.text = data?.nameEN
            Glide.with(imgCover.context)
                .load(data?.url)
                .placeholder(com.key.R.drawable.img_default_m)
                .skipMemoryCache(true)
                .into(imgCover)
            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
