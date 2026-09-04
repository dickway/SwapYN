package com.a.adapter

import androidx.recyclerview.widget.DiffUtil
import com.a.R
import com.a.databinding.ItemAtoolSizeBinding
import com.face.bean.SizeBean
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder


class ASizeAdapter : BaseAdapter<SizeBean, ItemAtoolSizeBinding>(
    R.layout.item_atool_size, object : DiffUtil.ItemCallback<SizeBean>() {
        override fun areItemsTheSame(oldItem: SizeBean, newItem: SizeBean): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: SizeBean,
            newItem: SizeBean
        ): Boolean {
            return false
        }
    }
) {
    var selectIndex = -1
        set(value) {
            if (value != field) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onBindData(
        holder: BaseHolder<ItemAtoolSizeBinding>,
        binding: ItemAtoolSizeBinding,
        data: SizeBean?,
        position: Int
    ) {
        holder.binding.apply {
            isSelected = selectIndex == position
            tvName.text = data?.size
            if (selectIndex != -1) {
                imgCover.alpha = 1f
            } else {
                imgCover.alpha = 0.5f
            }

            tvBl.text = "${data?.proportionW}:${data?.proportionH}"

            imgCover.setImageResource(data?.img ?: com.key.R.mipmap.img_icon_size_w)

            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
