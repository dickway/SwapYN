package com.a.adapter

import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.a.R
import com.a.databinding.ItemAswaptabItemBinding
import com.face.bean.Face
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import kotlin.apply


class ASwapTabAdapter : BaseAdapter<Face, ItemAswaptabItemBinding>(
    R.layout.item_aswaptab_item, object : DiffUtil.ItemCallback<Face>() {
        override fun areItemsTheSame(oldItem: Face, newItem: Face): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Face, newItem: Face): Boolean {
            return false
        }
    }
) {

    var selectIndex = -1
        set(value) {
            if (value != field) {
                val origin = field
                field = value
                notifyItemChanged(origin)
                notifyItemChanged(field)
            }
        }

    override fun onBindData(
        holder: BaseHolder<ItemAswaptabItemBinding>,
        binding: ItemAswaptabItemBinding,
        data: Face?,
        position: Int
    ) {
        holder.binding.apply {
            isSelected = selectIndex == position
            imgTab.loadImage(data?.url)
            imgChooes.loadImage(
                data?.isSelectBean?.pic,
                placeholderResId = R.mipmap.a_icon_swap_add_m
            )
            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
