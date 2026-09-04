package com.a.adapter

import androidx.recyclerview.widget.DiffUtil
import com.a.R
import com.a.databinding.ItemAswapItemBinding
import com.face.bean.MyFaceImgBean
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage


class ASwapAdapter : BaseAdapter<MyFaceImgBean, ItemAswapItemBinding>(
    R.layout.item_aswap_item, object : DiffUtil.ItemCallback<MyFaceImgBean>() {
        override fun areItemsTheSame(oldItem: MyFaceImgBean, newItem: MyFaceImgBean): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: MyFaceImgBean,
            newItem: MyFaceImgBean
        ): Boolean {
            return false
        }
    }
) {

    var onAddClick: ((MyFaceImgBean?) -> Unit)? = null

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
        holder: BaseHolder<ItemAswapItemBinding>,
        binding: ItemAswapItemBinding,
        data: MyFaceImgBean?,
        position: Int
    ) {
        holder.binding.apply {
            isAdd = data?.isSelect
            isSelected = selectIndex == position
            imgCover.loadImage(
                data?.pic
            )
            imgCover.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
            addImg.setOnClickListener {
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
