package com.face.adapter.other

import androidx.recyclerview.widget.DiffUtil
import com.face.R
import com.face.bean.SizeBean
import com.face.databinding.ItemTxtimgSizeBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder


class TxtImgSizeAdapter : BaseAdapter<SizeBean, ItemTxtimgSizeBinding>(
    R.layout.item_txtimg_size, object : DiffUtil.ItemCallback<SizeBean>() {
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
    var selectIndex = 0
        set(value) {
            if (value != field) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onBindData(
        holder: BaseHolder<ItemTxtimgSizeBinding>,
        binding: ItemTxtimgSizeBinding,
        data: SizeBean?,
        position: Int
    ) {
        holder.binding.apply {
            isSelected = selectIndex == position
            tvName.text = data?.size

            tvBl.text = "${data?.proportionW}:${data?.proportionH}"

            imgCover.setImageResource(data?.img ?: com.key.R.mipmap.img_icon_txt_size1)

            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
